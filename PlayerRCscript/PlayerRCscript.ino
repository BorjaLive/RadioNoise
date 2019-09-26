#include <SoftwareSerial.h>

#define PIN_LED_1 26
#define PIN_LED_2 28
#define PIN_LED_3 30
#define PIN_LED_4 32
#define PIN_PWM_1R 2
#define PIN_PWM_1L 3
#define PIN_PWM_2R 4
#define PIN_PWM_2L 5
#define PIN_PWM_3R 6
#define PIN_PWM_3L 7
#define PIN_PWM_4R 8
#define PIN_PWM_4L 9
#define PIN_KEY 31

#define SET 12
#define HC12 Serial1

void setup() {
  pinMode(PIN_PWM_1R, OUTPUT);
  pinMode(PIN_PWM_1L, OUTPUT);
  pinMode(PIN_PWM_2R, OUTPUT);
  pinMode(PIN_PWM_2L, OUTPUT);
  pinMode(PIN_PWM_3R, OUTPUT);
  pinMode(PIN_PWM_3L, OUTPUT);
  pinMode(PIN_PWM_4R, OUTPUT);
  pinMode(PIN_PWM_4L, OUTPUT);
  pinMode(PIN_KEY, INPUT_PULLUP);
  pinMode(PIN_LED_1, OUTPUT);
  pinMode(PIN_LED_2, OUTPUT);
  pinMode(PIN_LED_3, OUTPUT);
  pinMode(PIN_LED_4, OUTPUT);
  
  analogWrite(PIN_PWM_1R, 0);
  analogWrite(PIN_PWM_1L, 0);
  analogWrite(PIN_PWM_2R, 0);
  analogWrite(PIN_PWM_2L, 0);
  analogWrite(PIN_PWM_3R, 0);
  analogWrite(PIN_PWM_3L, 0);
  analogWrite(PIN_PWM_4R, 0);
  analogWrite(PIN_PWM_4L, 0);
  digitalWrite(PIN_LED_1, HIGH);
  digitalWrite(PIN_LED_2, HIGH);
  digitalWrite(PIN_LED_3, HIGH);
  digitalWrite(PIN_LED_4, HIGH);

  pinMode(SET, OUTPUT);
  
  HC12.begin(9600);               // Serial port to HC12

  digitalWrite(SET, LOW);
  HC12.print("AT+B9600");
  HC12.print("AT+C001");
  delay(1000);
  digitalWrite(SET, HIGH);

  //Serial.begin(9600);
  
  digitalWrite(PIN_LED_1, LOW);
  delay(250);
  digitalWrite(PIN_LED_2, LOW);
  delay(250);
  digitalWrite(PIN_LED_3, LOW);
  delay(250);
  digitalWrite(PIN_LED_4, LOW);
  delay(250);
}

byte powerW1, powerW2, powerW3, powerW4, dirW1, dirW2, dirW3, dirW4, tmp;

void loop() {
  if(digitalRead(PIN_KEY) == LOW){
    digitalWrite(PIN_LED_4, HIGH);
    do{
      while(HC12.available() == 0) delay(1);
      tmp = HC12.read();
      //Serial.println(tmp);
    }while(tmp != (byte)144);
    digitalWrite(PIN_LED_4, LOW);
    digitalWrite(PIN_LED_3, LOW);
    while(HC12.available() < 9 && digitalRead(PIN_KEY) == LOW) delay(1);
    if(HC12.available() >= 9) {        // If HC-12 has data
      powerW1 = HC12.read();
      powerW2 = HC12.read();
      powerW3 = HC12.read();
      powerW4 = HC12.read();
      dirW1 = HC12.read();
      dirW2 = HC12.read();
      dirW3 = HC12.read();
      dirW4 = HC12.read();
      tmp = HC12.read();
      if(tmp != 4 && ((dirW1 != 0 && dirW1 != 1) || (dirW2 != 0 && dirW2 != 1) || (dirW3 != 0 && dirW3 != 1) || (dirW4 != 0 && dirW4 != 1)) || powerW1 == 1 || powerW2 == 1 || powerW3 == 1 || powerW4 == 1 ){
        //Serial.println("Ha ocurrido");
        return;
      }
  
      //Serial.print(powerW1);
      //Serial.print(powerW2);
      //Serial.print(powerW3);
      //Serial.print(powerW4);
      //Serial.print(dirW1);
      //Serial.print(dirW2);
      //Serial.print(dirW3);
      //Serial.print(dirW4);
      //Serial.println("");
  
      if(dirW1 == 1){
        analogWrite(PIN_PWM_1L, 0);
        analogWrite(PIN_PWM_1R, powerW1);
      }else{
        analogWrite(PIN_PWM_1R, 0);
        analogWrite(PIN_PWM_1L, powerW1);
      }
      if(dirW2 == 1){
        analogWrite(PIN_PWM_2L, 0);
        analogWrite(PIN_PWM_2R, powerW2);
      }else{
        analogWrite(PIN_PWM_2R, 0);
        analogWrite(PIN_PWM_2L, powerW2);
      }
      if(dirW3 == 1){
        analogWrite(PIN_PWM_3L, 0);
        analogWrite(PIN_PWM_3R, powerW3);
      }else{
        analogWrite(PIN_PWM_3R, 0);
        analogWrite(PIN_PWM_3L, powerW3);
      }
      if(dirW4 == 1){
        analogWrite(PIN_PWM_4L, 0);
        analogWrite(PIN_PWM_4R, powerW4);
      }else{
        analogWrite(PIN_PWM_4R, 0);
        analogWrite(PIN_PWM_4L, powerW4);
      }
    }
  }else{
    analogWrite(PIN_PWM_1R, 0);
    analogWrite(PIN_PWM_1L, 0);
    analogWrite(PIN_PWM_2R, 0);
    analogWrite(PIN_PWM_2L, 0);
    analogWrite(PIN_PWM_3R, 0);
    analogWrite(PIN_PWM_3L, 0);
    analogWrite(PIN_PWM_4R, 0);
    analogWrite(PIN_PWM_4L, 0);
    if(HC12.available() > 0) {
        HC12.read();
        //Serial.println("Las instrucciones fueron ignoradas");
    }
  }
}
