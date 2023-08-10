#include "BluetoothSerial.h"

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to enable it
#endif

BluetoothSerial SerialBT;
int received;         // odebrana zmienna
char receivedChar;    // zmienna przechowywana jako char
const int ledPin = 2; // wyjscie drugie jako LED

void setup()
{
  Serial.begin(115200);
  SerialBT.begin("LufaNaLezaco"); // nazwa urządzenia Bluetooth
  pinMode(ledPin, OUTPUT);        // pin LED jako wyjscie
}

void loop()
{
  if (Serial.available())
  {
    SerialBT.write(Serial.read());
  }

  if (SerialBT.available())
  {
    receivedChar = SerialBT.read();

    if (receivedChar == 'a')
    {
      digitalWrite(ledPin, HIGH);
    }

    if (receivedChar == 'b')
    {
      digitalWrite(ledPin, LOW);
    }
  }

  delay(20);
}
