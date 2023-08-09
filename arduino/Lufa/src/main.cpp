#include "BluetoothSerial.h"

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

BluetoothSerial SerialBT;
int received;// received value will be stored in this variable
char receivedChar;// received value will be stored as CHAR in this variable
const int ledPin = 2 ; // Pin 2 jako wyjściowy, podłączony do diody LED


void setup() {
  Serial.begin(115200);
  SerialBT.begin("LufaNaLezaco"); //Bluetooth device name
}

void loop() {
  if (Serial.available()) {
    SerialBT.write(Serial.read());
  }
  if (SerialBT.available()) {
    Serial.write(SerialBT.read());
  }
  if (Serial.available()) {
SerialBT.write(Serial.read());
}
 if (SerialBT.available()) {
    if (receivedChar == 'a'){
      digitalWrite(ledPin, HIGH); // Włączenie diody (ustawienie stanu wysokiego)
    }

    if (receivedChar == 'b') {
      digitalWrite(ledPin, LOW); // Wyłączenie diody (ustawienie stanu niskiego)
    }
  }
  delay(20);
}




// #include "BluetoothSerial.h"

// #if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
// #error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
// #endif

// BluetoothSerial SerialBT;
// int received;// received value will be stored in this variable
// char receivedChar;// received value will be stored as CHAR in this variable
// const int ledPin = 2 ; // Pin 2 jako wyjściowy, podłączony do diody LED

// void setup() {
//   Serial.begin(115200);
//   SerialBT.begin("LufaNaLezaco"); //Bluetooth device name
//   pinMode(ledPin, OUTPUT);
// }

// void loop() {
//   receivedChar =(char)SerialBT.read();

//   if (Serial.available()) {
//     SerialBT.write(Serial.read());
//   }
//   if (SerialBT.available()) {
//     if (receivedChar == 'a'){
//       digitalWrite(ledPin, HIGH); // Włączenie diody (ustawienie stanu wysokiego)
//     }

//     if (receivedChar == 'b') {
//       digitalWrite(ledPin, LOW); // Wyłączenie diody (ustawienie stanu niskiego)
//     }
//   }
//   delay(20);
// }
