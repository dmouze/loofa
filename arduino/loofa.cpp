#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>

const int ledPin = 2; 
bool ledState = false;

BLEServer* pServer = NULL;
BLECharacteristic* pCharacteristic = NULL;
bool deviceConnected = false;

class MyServerCallbacks : public BLEServerCallbacks {
  void onConnect(BLEServer* pServer) {
    deviceConnected = true;
  }

  void onDisconnect(BLEServer* pServer) {
    deviceConnected = false;
  }
};

void setup() {
  Serial.begin(115200);
  pinMode(ledPin, OUTPUT);

  BLEDevice::init("Loofa na lezaco");
  pServer = BLEDevice::createServer();
  pServer->setCallbacks(new MyServerCallbacks());

  BLEService *pService = pServer->createService(BLEUUID((uint16_t)0x180D));

  pCharacteristic = pService->createCharacteristic(
                      BLEUUID((uint16_t)0x2A37),
                      BLECharacteristic::PROPERTY_READ   |
                      BLECharacteristic::PROPERTY_WRITE
                    );

  pCharacteristic->addDescriptor(new BLE2902());

  pService->start();

  BLEAdvertising *pAdvertising = pServer->getAdvertising();
  pAdvertising->addServiceUUID(BLEUUID((uint16_t)0x180D));
  pAdvertising->start();
}

void loop() {
  if (deviceConnected) {
    std::string value = pCharacteristic->getValue();
    if (value.length() > 0) {
      // Odczyt wartości przysłanej z aplikacji Android
      char command = value[0];
      if (command == '1') {
        // Wysłano sygnał '1' - zapal diodę LED
        digitalWrite(ledPin, HIGH);
        ledState = true;
        Serial.println("Dioda zapalona!");
      } else if (command == '0') {
        // Wysłano sygnał '0' - zgaś diodę LED
        digitalWrite(ledPin, LOW);
        ledState = false;
        Serial.println("Dioda zgaszona!");
      }
    }
  }
}
