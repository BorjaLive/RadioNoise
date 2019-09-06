#include<Servo.h>

#define PIN_Voltaje_Bateria_1 A15
#define PIN_Voltaje_Bateria_2 A14
#define PIN_Voltaje_Bateria_3 A13
#define PIN_Voltaje_Bateria_4 A12
#define PIN_Voltaje_Pilas A11
#define PIN_Camara_Z 7
#define PIN_Camara_Y 6
#define PIN_LED_Modulo_Controller 26
#define PIN_LED_Modulo_Video 28
#define PIN_LED_Modulo_AudioIN 30
#define PIN_LED_Modulo_AudioOUT 32
#define PIN_PWM_1 12
#define PIN_PWM_2 11
#define PIN_PWM_3 10
#define PIN_PWM_4 9
#define PIN_Sentido_A1 31
#define PIN_Sentido_B1 33
#define PIN_Sentido_A2 35
#define PIN_Sentido_B2 37
#define PIN_Sentido_A3 39
#define PIN_Sentido_B3 41
#define PIN_Sentido_A4 43
#define PIN_Sentido_B4 45

#define PORT_SPEED 9600
#define BYTES_IN 15
#define BYTES_OUT 6

Servo servoZ, servoY;

const int voltajesSensors = 5;
int voltajesSuma[voltajesSensors];
int voltajesTomas;
int voltajesDelay;
int voltajesLast[voltajesSensors];
const int VOLTAJE_TOMAS = 10;
const int VOLTAJE_DELAY = 100;

void setup() {
  for(int i = 0; i < voltajesSensors; i++){
    voltajesSuma[i] = 0;
    voltajesLast[i] = 0;
  }
  voltajesTomas = 0;
  voltajesDelay = 0;

  servoZ.attach(PIN_Camara_Z);
  servoY.attach(PIN_Camara_Y);
  
  pinMode(PIN_LED_Modulo_Controller, OUTPUT);
  pinMode(PIN_LED_Modulo_Video, OUTPUT);
  pinMode(PIN_LED_Modulo_AudioIN, OUTPUT);
  pinMode(PIN_LED_Modulo_AudioOUT, OUTPUT);
  pinMode(PIN_PWM_1, OUTPUT);
  pinMode(PIN_PWM_2, OUTPUT);
  pinMode(PIN_PWM_3, OUTPUT);
  pinMode(PIN_PWM_4, OUTPUT);
  pinMode(PIN_Sentido_A1, OUTPUT);
  pinMode(PIN_Sentido_B1, OUTPUT);
  pinMode(PIN_Sentido_A2, OUTPUT);
  pinMode(PIN_Sentido_B2, OUTPUT);
  pinMode(PIN_Sentido_A3, OUTPUT);
  pinMode(PIN_Sentido_B3, OUTPUT);
  pinMode(PIN_Sentido_A4, OUTPUT);
  pinMode(PIN_Sentido_B4, OUTPUT);
  
  Serial.begin(PORT_SPEED);
  Serial.setTimeout(50);
}

byte buffIN[BYTES_IN];

void loop() {
  //Comprobar que se ha enviado una orden completa
  if(Serial.available() == BYTES_IN){
    //Leer todos los bytes recividos
    for(int i = 0; i < BYTES_IN; i++)
      buffIN[i] = Serial.read();

    if(buffIN[0] == (byte)1){
      //TEST: se contesta con un patron alternado
      bool last = false;
      for(int i = 0; i < BYTES_OUT; i++){
        Serial.write(last?0:1);
        last = !last;
      }
    }else if(buffIN[0] == 2 || buffIN[0] == 3){
      //RECIVE: hay que cambiar las salidas
      analogWrite(PIN_PWM_1, buffIN[1]);    //Bit 1
      analogWrite(PIN_PWM_2, buffIN[2]);    //Bit 2
      analogWrite(PIN_PWM_3, buffIN[3]);    //Bit 3
      analogWrite(PIN_PWM_4, buffIN[4]);    //Bit 4
      if(buffIN[1] == 0){                   //Bit 5
        digitalWrite(PIN_Sentido_A1, LOW);
        digitalWrite(PIN_Sentido_B1, LOW);
      }else{
        if(buffIN[5] == 0){
          digitalWrite(PIN_Sentido_A1, HIGH);
          digitalWrite(PIN_Sentido_B1, LOW);
        }else{
          digitalWrite(PIN_Sentido_A1, LOW);
          digitalWrite(PIN_Sentido_B1, HIGH);
        }
      }
      if(buffIN[2] == 0){                   //Bit 6
        digitalWrite(PIN_Sentido_A2, LOW);
        digitalWrite(PIN_Sentido_B2, LOW);
      }else{
        if(buffIN[6] == 0){
          digitalWrite(PIN_Sentido_A2, HIGH);
          digitalWrite(PIN_Sentido_B2, LOW);
        }else{
          digitalWrite(PIN_Sentido_A2, LOW);
          digitalWrite(PIN_Sentido_B2, HIGH);
        }
      }
      if(buffIN[3] == 0){                   //Bit 7
        digitalWrite(PIN_Sentido_A3, LOW);
        digitalWrite(PIN_Sentido_B3, LOW);
      }else{
        if(buffIN[7] == 0){
          digitalWrite(PIN_Sentido_A3, HIGH);
          digitalWrite(PIN_Sentido_B3, LOW);
        }else{
          digitalWrite(PIN_Sentido_A3, LOW);
          digitalWrite(PIN_Sentido_B3, HIGH);
        }
      }
      if(buffIN[4] == 0){                   //Bit 8
        digitalWrite(PIN_Sentido_A4, LOW);
        digitalWrite(PIN_Sentido_B4, LOW);
      }else{
        if(buffIN[8] == 0){
          digitalWrite(PIN_Sentido_A4, HIGH);
          digitalWrite(PIN_Sentido_B4, LOW);
        }else{
          digitalWrite(PIN_Sentido_A4, LOW);
          digitalWrite(PIN_Sentido_B4, HIGH);
        }
      }
      digitalWrite(PIN_LED_Modulo_Controller, buffIN[5]==0?LOW:HIGH);         //Bit 9
      digitalWrite(PIN_LED_Modulo_Video, buffIN[6]==0?LOW:HIGH);              //Bit 10
      digitalWrite(PIN_LED_Modulo_AudioIN, buffIN[7]==0?LOW:HIGH);            //Bit 11
      digitalWrite(PIN_LED_Modulo_AudioOUT, buffIN[8]==0?LOW:HIGH);           //Bit 12
      //Los bits 13 y 14 son sobrantes
    }
    if(buffIN[0] == 3){
      //SEND: Leer las entradas y enviarlas
      //Aqui hay que enviar los voltajes, que es complicado
      Serial.write((byte)voltajesLast[0]);             //Bit 0
      Serial.write((byte)voltajesLast[1]);             //Bit 1
      Serial.write((byte)voltajesLast[2]);             //Bit 2
      Serial.write((byte)voltajesLast[3]);             //Bit 3
      Serial.write((byte)voltajesLast[4]);             //Bit 4
      Serial.write((byte)0);                           //Bit 5
    }
  }

  if(voltajesDelay == VOLTAJE_DELAY){
    if(voltajesTomas == VOLTAJE_TOMAS){
      for(int i = 0; i < voltajesSensors; i++){
        voltajesLast[i] = voltajesSuma[i]/VOLTAJE_TOMAS;
        voltajesSuma[i] = 0;
      }
    }else{
      voltajesTomas++;
      voltajesSuma[0] += analogRead(PIN_Voltaje_Bateria_1);
      voltajesSuma[1] += analogRead(PIN_Voltaje_Bateria_2);
      voltajesSuma[2] += analogRead(PIN_Voltaje_Bateria_3);
      voltajesSuma[3] += analogRead(PIN_Voltaje_Bateria_4);
      voltajesSuma[4] += analogRead(PIN_Voltaje_Pilas);
    }
    voltajesDelay = 0;
  }else voltajesDelay++;
  
}
