#include<Servo.h>

#define PIN_Voltaje_Bateria_1 A15
#define PIN_Voltaje_Bateria_2 A14
#define PIN_Voltaje_Bateria_3 A13
#define PIN_Voltaje_Bateria_4 A12
#define PIN_Voltaje_Pilas A11
#define PIN_Camara_Z 10
#define PIN_Camara_Y 11
#define PIN_LED_Modulo_Controller 26
#define PIN_LED_Modulo_Video 28
#define PIN_LED_Modulo_AudioIN 30
#define PIN_LED_Modulo_AudioOUT 32
#define PIN_PWM_1R 2
#define PIN_PWM_1L 3
#define PIN_PWM_2R 4
#define PIN_PWM_2L 5
#define PIN_PWM_3R 6
#define PIN_PWM_3L 7
#define PIN_PWM_4R 8
#define PIN_PWM_4L 9
#define PIN_KEY 9

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
  pinMode(PIN_PWM_1R, OUTPUT);
  pinMode(PIN_PWM_1L, OUTPUT);
  pinMode(PIN_PWM_2R, OUTPUT);
  pinMode(PIN_PWM_2L, OUTPUT);
  pinMode(PIN_PWM_3R, OUTPUT);
  pinMode(PIN_PWM_3L, OUTPUT);
  pinMode(PIN_PWM_4R, OUTPUT);
  pinMode(PIN_PWM_4L, OUTPUT);
  pinMode(PIN_KEY, INPUT_PULLUP);
  
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
  
      //Prueba de la llave
      digitalWrite(PIN_LED_Modulo_Controller, HIGH);
      digitalWrite(PIN_LED_Modulo_Video, HIGH);
      digitalWrite(PIN_LED_Modulo_AudioIN, HIGH);
      digitalWrite(PIN_LED_Modulo_AudioOUT, HIGH);
      while(digitalRead(PIN_KEY) == HIGH) delay(10);
      digitalWrite(PIN_LED_Modulo_Controller, LOW);
      digitalWrite(PIN_LED_Modulo_Video, LOW);
      digitalWrite(PIN_LED_Modulo_AudioIN, LOW);
      digitalWrite(PIN_LED_Modulo_AudioOUT, LOW);
      
      bool last = false;
      for(int i = 0; i < BYTES_OUT; i++){
        Serial.write(last?0:1);
        last = !last;
      }
    }else if(buffIN[0] == 2 || buffIN[0] == 3){
      //RECIVE: hay que cambiar las salidas
      
      if(buffIN[5] == 0){
        analogWrite(PIN_PWM_1L, 0);
        analogWrite(PIN_PWM_1R, buffIN[1]);
      }else{
        analogWrite(PIN_PWM_1R, 0);
        analogWrite(PIN_PWM_1L, buffIN[1]);
      }
      if(buffIN[6] == 0){
        analogWrite(PIN_PWM_2L, 0);
        analogWrite(PIN_PWM_2R, buffIN[2]);
      }else{
        analogWrite(PIN_PWM_2R, 0);
        analogWrite(PIN_PWM_2L, buffIN[2]);
      }
      if(buffIN[7] == 0){
        analogWrite(PIN_PWM_3L, 0);
        analogWrite(PIN_PWM_3R, buffIN[3]);
      }else{
        analogWrite(PIN_PWM_3R, 0);
        analogWrite(PIN_PWM_3L, buffIN[3]);
      }
      if(buffIN[8] == 0){
        analogWrite(PIN_PWM_4L, 0);
        analogWrite(PIN_PWM_4R, buffIN[4]);
      }else{
        analogWrite(PIN_PWM_4R, 0);
        analogWrite(PIN_PWM_4L, buffIN[4]);
      }
      
      digitalWrite(PIN_LED_Modulo_Controller, buffIN[9]==0?LOW:HIGH);
      digitalWrite(PIN_LED_Modulo_Video, buffIN[10]==0?LOW:HIGH);
      digitalWrite(PIN_LED_Modulo_AudioIN, buffIN[11]==0?LOW:HIGH);
      digitalWrite(PIN_LED_Modulo_AudioOUT, buffIN[12]==0?LOW:HIGH);

      servoZ.write(buffIN[13]);
      servoY.write(buffIN[14]);
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
