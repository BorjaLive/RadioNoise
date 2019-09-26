#define PIN_Rueda_1 22
#define PIN_Rueda_2 24
#define PIN_Rueda_3 26
#define PIN_Rueda_4 28
#define PIN_Modulo_Wifi 30
#define PIN_Modulo_Controller 32
#define PIN_Modulo_Video 34
#define PIN_Modulo_AudioIN 36
#define PIN_Modulo_AudioOUT 38
#define PIN_Activar_PAN 40
#define PIN_Activar_Safe 42
#define PIN_Activar_Tanque 44
#define PIN_Activar_Turbo 46
#define PIN_Boton_Turbo 48
#define PIN_Boton_TurboRevert 31
#define PIN_Boton_Claxon 33
#define PIN_Boton_ResetView 35
#define PIN_Boton_Hablar 37
#define PIN_Potencia_Mover_X A0
#define PIN_Potencia_Mover_Y A1
#define PIN_Potencia_Ver_X A2
#define PIN_Potencia_Ver_Y A3
#define PIN_Potencia_PAN A4
#define PIN_Potencia_Claxon A5
#define PIN_Potencia_Velocidad A6
#define PIN_Potencia_Sensibilidad A7
#define PIN_Potencia_Volumen_Mando A8
#define PIN_Potencia_Volumen_Vehiculo A9
#define PIN_LED_verde_rueda_1 39
#define PIN_LED_verde_rueda_2 41
#define PIN_LED_verde_rueda_3 43
#define PIN_LED_verde_rueda_4 45
#define PIN_LED_rojo_rueda_1 47
#define PIN_LED_rojo_rueda_2 49
#define PIN_LED_rojo_rueda_3 12
#define PIN_LED_rojo_rueda_4 11
#define PIN_LED_verde_tanque 10
#define PIN_LED_rojo_tanque 9
#define PIN_LED_verde_modulo_wifi 8
#define PIN_LED_verde_modulo_controller 7
#define PIN_LED_verde_modulo_video 6
#define PIN_LED_verde_modulo_audioIN 5
#define PIN_LED_verde_modulo_audioOUT 4
#define PIN_LED_rojo_modulo_wifi 3
#define PIN_LED_rojo_modulo_controller 2
#define PIN_LED_rojo_modulo_video 14
#define PIN_LED_rojo_modulo_audioIN 15
#define PIN_LED_rojo_modulo_audioOUT 16
#define PIN_Ready LED_BUILTIN
#define PIN_Salir 52

#define HC12_SET 23
#define HC12 Serial1

#define PORT_SPEED 9600

#define MIN_STEER 50

const int DELAY = 1000;

void setup() {
  pinMode(PIN_Ready, OUTPUT);
  digitalWrite(PIN_Ready, LOW);
  
  pinMode(PIN_Rueda_1, INPUT);
  pinMode(PIN_Rueda_2, INPUT);
  pinMode(PIN_Rueda_3, INPUT);
  pinMode(PIN_Rueda_4, INPUT);
  pinMode(PIN_Modulo_Wifi, INPUT);
  pinMode(PIN_Modulo_Controller, INPUT);
  pinMode(PIN_Modulo_Video, INPUT);
  pinMode(PIN_Modulo_AudioIN, INPUT);
  pinMode(PIN_Modulo_AudioOUT, INPUT);
  pinMode(PIN_Activar_PAN, INPUT);
  pinMode(PIN_Activar_Safe, INPUT);
  pinMode(PIN_Activar_Tanque, INPUT);
  pinMode(PIN_Activar_Turbo, INPUT);
  pinMode(PIN_Boton_Turbo, INPUT_PULLUP);
  pinMode(PIN_Boton_TurboRevert, INPUT_PULLUP);
  pinMode(PIN_Boton_Claxon, INPUT_PULLUP);
  pinMode(PIN_Boton_ResetView, INPUT_PULLUP);
  pinMode(PIN_Boton_Hablar, INPUT_PULLUP);
  pinMode(PIN_Salir, INPUT_PULLUP);
  
  pinMode(PIN_LED_verde_rueda_1, OUTPUT);
  pinMode(PIN_LED_verde_rueda_2, OUTPUT);
  pinMode(PIN_LED_verde_rueda_3, OUTPUT);
  pinMode(PIN_LED_verde_rueda_4, OUTPUT);
  pinMode(PIN_LED_rojo_rueda_1, OUTPUT);
  pinMode(PIN_LED_rojo_rueda_2, OUTPUT);
  pinMode(PIN_LED_rojo_rueda_3, OUTPUT);
  pinMode(PIN_LED_rojo_rueda_4, OUTPUT);
  pinMode(PIN_LED_verde_tanque, OUTPUT);
  pinMode(PIN_LED_rojo_tanque, OUTPUT);
  pinMode(PIN_LED_verde_modulo_wifi, OUTPUT);
  pinMode(PIN_LED_verde_modulo_controller, OUTPUT);
  pinMode(PIN_LED_verde_modulo_video, OUTPUT);
  pinMode(PIN_LED_verde_modulo_audioIN, OUTPUT);
  pinMode(PIN_LED_verde_modulo_audioOUT, OUTPUT);
  pinMode(PIN_LED_rojo_modulo_wifi, OUTPUT);
  pinMode(PIN_LED_rojo_modulo_controller, OUTPUT);
  pinMode(PIN_LED_rojo_modulo_video, OUTPUT);
  pinMode(PIN_LED_rojo_modulo_audioIN, OUTPUT);
  pinMode(PIN_LED_rojo_modulo_audioOUT, OUTPUT);

  Serial.begin(PORT_SPEED);
  HC12.begin(9600);               // Serial port to HC12

  digitalWrite(PIN_Ready, HIGH);
}

byte dirW1, dirW2, dirW3, dirW4, pwmW1, pwmW2, pwmW3, pwmW4;
float powerW1, powerW2, powerW3, powerW4;
bool activeW1, activeW2, activeW3, activeW4, tank, pan, turbo, controll;
//Hay que cambiar esto
/*
 * Bytes:
 *    PWM W1
 *    PWM W2
 *    PWM W3
 *    PWM W4
 *    DIR W1
 *    DIR W2
 *    DIR W3
 *    DIR W4
*/
void loop() {
  activeW1 = digitalRead(PIN_Rueda_1);
  activeW2 = digitalRead(PIN_Rueda_2);
  activeW3 = digitalRead(PIN_Rueda_3);
  activeW4 = digitalRead(PIN_Rueda_4);
  tank = digitalRead(PIN_Activar_Tanque);
  pan = digitalRead(PIN_Activar_PAN);
  turbo = digitalRead(PIN_Activar_Turbo);
  controll = digitalRead(PIN_Modulo_Controller);
  
  int raw_x = analogRead(PIN_Potencia_Mover_X);
  int raw_y = analogRead(PIN_Potencia_Ver_Y);
  float power = analogRead(PIN_Potencia_Velocidad)/1023.0, x, y;
  int dirX, dirY;
  
  if(raw_x > 512 + MIN_STEER){
    x = (raw_x - 512)/512.0;
    dirX = 1;
  }else if(raw_x < 512 - MIN_STEER){
    x = (512 - raw_x)/512.0;
    dirX = 2;
  }else{
    x = 0;
    dirX = 0;
  }
  if(raw_y > 512 + MIN_STEER){
    y = (raw_y - 512)/512.0;
    dirY = 2;
  }else if(raw_y < 512 - MIN_STEER){
    y = (512 - raw_y)/512.0;
    dirY = 1;
  }else{
    y = 0;
    dirY = 0;
  }

  if(tank){
    if(dirX == 1 && dirY == 0){//Delante
      Serial.println("Delante");
      powerW1 = power;
      powerW2 = power;
      powerW3 = power;
      powerW4 = power;
      dirW1 = (byte)1;
      dirW2 = (byte)1;
      dirW3 = (byte)1;
      dirW4 = (byte)1;
    }else if(dirX == 2 && dirY == 0){//Atras
      Serial.println("Atras");
      powerW1 = power;
      powerW2 = power;
      powerW3 = power;
      powerW4 = power;
      dirW1 = (byte)0;
      dirW2 = (byte)0;
      dirW3 = (byte)0;
      dirW4 = (byte)0;
    }else if(dirX == 0 && dirY == 1){//Giro Izquierda
      Serial.println("Giro izquierda");
      powerW1 = power;
      powerW2 = power;
      powerW3 = power;
      powerW4 = power;
      dirW1 = (byte)0;
      dirW2 = (byte)1;
      dirW3 = (byte)0;
      dirW4 = (byte)1;
    }else if(dirX == 0 && dirY == 2){//Giro Derecha
      Serial.println("Giro derecha");
      powerW1 = power;
      powerW2 = power;
      powerW3 = power;
      powerW4 = power;
      dirW1 = (byte)1;
      dirW2 = (byte)0;
      dirW3 = (byte)1;
      dirW4 = (byte)0;
    }else if(dirX == 1 && dirY == 1){//Delante Izquierda
      Serial.println("Delante izquierda");
      powerW1 = 0;
      powerW2 = power;
      powerW3 = 0;
      powerW4 = power;
      dirW1 = (byte)1;
      dirW2 = (byte)1;
      dirW3 = (byte)1;
      dirW4 = (byte)1;
    }else if(dirX == 1 && dirY == 2){//Delante Derecha
      Serial.println("Delante derecha");
      powerW1 = power;
      powerW2 = 0;
      powerW3 = power;
      powerW4 = 0;
      dirW1 = (byte)1;
      dirW2 = (byte)1;
      dirW3 = (byte)1;
      dirW4 = (byte)1;
    }else if(dirX == 2 && dirY == 1){//Detras Izquierda
      Serial.println("Detras izquierda");
      powerW1 = power;
      powerW2 = 0;
      powerW3 = power;
      powerW4 = 0;
      dirW1 = (byte)0;
      dirW2 = (byte)0;
      dirW3 = (byte)0;
      dirW4 = (byte)0;
    }else if(dirX == 2 && dirY == 2){//Detras Derecha
      Serial.println("Detras derecha");
      powerW1 = 0;
      powerW2 = power;
      powerW3 = 0;
      powerW4 = power;
      dirW1 = (byte)0;
      dirW2 = (byte)0;
      dirW3 = (byte)0;
      dirW4 = (byte)0;
    }else if(dirX == 0 && dirY == 0){//Parado
      Serial.println("Parado");
      powerW1 = 0;
      powerW2 = 0;
      powerW3 = 0;
      powerW4 = 0;
    }
  }else{
    if(dirX == 1){
      dirW1 = (byte)1;
      dirW2 = (byte)1;
      dirW3 = (byte)1;
      dirW4 = (byte)1;
    }else{
      dirW1 = (byte)0;
      dirW2 = (byte)0;
      dirW3 = (byte)0;
      dirW4 = (byte)0;
    }
    if(dirX == 0){
      powerW1 = 0;
      powerW2 = 0;
      powerW3 = 0;
    }else{
      power *= x;
      powerW1 = activeW1?power:0.0;
      powerW2 = activeW2?power:0.0;
      powerW3 = activeW3?power:0.0;
      powerW4 = activeW4?power:0.0;
      if(dirY == 1){//Izquierda
        powerW1 *= 1.0-y;
        powerW3 *= 1.0-y;
      }else if(dirY == 2){
        powerW2 *= 1.0-y;
        powerW3 *= 1.0-y;
      }
    }
  }

  if(pan){
    int pan_raw = analogRead(PIN_Potencia_PAN);
    float pan;
    if(pan_raw > 512){
      pan = (pan_raw - 512)/512.0;
      powerW1 *= 1.0-pan;
      powerW2 *= 1.0-pan;
    }else{
      pan = (512 - pan_raw)/512.0;
      powerW3 *= 1.0-pan;
      powerW4 *= 1.0-pan;
    }
  }

  if(turbo){
    if(!digitalRead(PIN_Boton_Turbo)){
      powerW1 = 1;
      powerW2 = 1;
      powerW3 = 1;
      powerW4 = 1;
      dirW1 = (byte)1;
      dirW2 = (byte)1;
      dirW3 = (byte)1;
      dirW4 = (byte)1;
    }else if(!digitalRead(PIN_Boton_TurboRevert)){
      powerW1 = 1;
      powerW2 = 1;
      powerW3 = 1;
      powerW4 = 1;
      dirW1 = (byte)0;
      dirW2 = (byte)0;
      dirW3 = (byte)0;
      dirW4 = (byte)0;
    }
  }
  //Serial.println((byte)(powerW3*255.0));
  pwmW1 = (byte)(powerW1*255.0);
  pwmW2 = (byte)(powerW2*255.0);
  pwmW3 = (byte)(powerW3*255.0);
  pwmW4 = (byte)(powerW4*255.0);
  
  if(controll){
    HC12.write((byte)144);
    HC12.write((byte)pwmW1);
    HC12.write((byte)pwmW2);
    HC12.write((byte)pwmW3);
    HC12.write((byte)pwmW4);
    HC12.write((byte)dirW1);
    HC12.write((byte)dirW2);
    HC12.write((byte)dirW3);
    HC12.write((byte)dirW4);
    HC12.write((byte)4);
  }

  digitalWrite(PIN_LED_verde_rueda_1, activeW1);
  digitalWrite(PIN_LED_verde_rueda_2, activeW2);
  digitalWrite(PIN_LED_verde_rueda_3, activeW3);
  digitalWrite(PIN_LED_verde_rueda_4, activeW4);
  digitalWrite(PIN_LED_rojo_rueda_1, !activeW1);
  digitalWrite(PIN_LED_rojo_rueda_2, !activeW2);
  digitalWrite(PIN_LED_rojo_rueda_3, !activeW3);
  digitalWrite(PIN_LED_rojo_rueda_4, !activeW4);
  digitalWrite(PIN_LED_verde_tanque, tank);
  digitalWrite(PIN_LED_rojo_tanque, !tank);
  digitalWrite(PIN_LED_verde_modulo_controller, controll);
  digitalWrite(PIN_LED_rojo_modulo_controller, !controll);
}
