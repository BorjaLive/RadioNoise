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

#define PORT_SPEED 9600
#define BYTES_IN 22
#define BYTES_OUT 35

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
  Serial.setTimeout(50);
  
  digitalWrite(PIN_Ready, HIGH);
}

byte buffIN[BYTES_IN];

void loop() {
  int rueda_1 = digitalRead(PIN_Rueda_1);

  //Comprobar que se ha enviado una orden completa
  if(Serial.available() == BYTES_IN){
    digitalWrite(PIN_Ready, LOW);

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
    }else if(buffIN[0] == 2){
      //RECIVE: hay que cambiar las salidas
      digitalWrite(PIN_LED_verde_rueda_1, buffIN[1]==0?LOW:HIGH);             //Bit 1
      digitalWrite(PIN_LED_verde_rueda_2, buffIN[2]==0?LOW:HIGH);             //Bit 2
      digitalWrite(PIN_LED_verde_rueda_3, buffIN[3]==0?LOW:HIGH);             //Bit 3
      digitalWrite(PIN_LED_verde_rueda_4, buffIN[4]==0?LOW:HIGH);             //Bit 4
      digitalWrite(PIN_LED_rojo_rueda_1, buffIN[5]==0?LOW:HIGH);              //Bit 5
      digitalWrite(PIN_LED_rojo_rueda_2, buffIN[6]==0?LOW:HIGH);              //Bit 6
      digitalWrite(PIN_LED_rojo_rueda_3, buffIN[7]==0?LOW:HIGH);              //Bit 7
      digitalWrite(PIN_LED_rojo_rueda_4, buffIN[8]==0?LOW:HIGH);              //Bit 8
      digitalWrite(PIN_LED_verde_modulo_wifi, buffIN[9]==0?LOW:HIGH);         //Bit 9
      digitalWrite(PIN_LED_verde_modulo_controller, buffIN[10]==0?LOW:HIGH);  //Bit 10
      digitalWrite(PIN_LED_verde_modulo_video, buffIN[11]==0?LOW:HIGH);       //Bit 11
      digitalWrite(PIN_LED_verde_modulo_audioIN, buffIN[12]==0?LOW:HIGH);     //Bit 12
      digitalWrite(PIN_LED_verde_modulo_audioOUT, buffIN[13]==0?LOW:HIGH);    //Bit 13
      digitalWrite(PIN_LED_rojo_modulo_wifi, buffIN[14]==0?LOW:HIGH);         //Bit 14
      digitalWrite(PIN_LED_rojo_modulo_controller, buffIN[15]==0?LOW:HIGH);   //Bit 15
      digitalWrite(PIN_LED_rojo_modulo_video, buffIN[16]==0?LOW:HIGH);        //Bit 16
      digitalWrite(PIN_LED_rojo_modulo_audioIN, buffIN[17]==0?LOW:HIGH);      //Bit 17
      digitalWrite(PIN_LED_rojo_modulo_audioOUT, buffIN[18]==0?LOW:HIGH);     //Bit 18
      digitalWrite(PIN_LED_verde_tanque, buffIN[19]==0?LOW:HIGH);             //Bit 19
      digitalWrite(PIN_LED_rojo_tanque, buffIN[20]==0?LOW:HIGH);              //Bit 20
      //El bit 21 es sobrante
    }else if(buffIN[0] == 3){
      //SEND: Leer las entradas y enviarlas
      Serial.write(digitalRead(PIN_Rueda_1));             //Bit 0
      Serial.write(digitalRead(PIN_Rueda_2));             //Bit 1
      Serial.write(digitalRead(PIN_Rueda_3));             //Bit 2
      Serial.write(digitalRead(PIN_Rueda_4));             //Bit 3
      Serial.write(digitalRead(PIN_Modulo_AudioIN));      //Bit 4
      Serial.write(digitalRead(PIN_Modulo_AudioOUT));     //Bit 5
      Serial.write(digitalRead(PIN_Modulo_Video));        //Bit 6
      Serial.write(digitalRead(PIN_Modulo_Controller));   //Bit 7
      Serial.write(digitalRead(PIN_Modulo_Wifi));         //Bit 8
      Serial.write(digitalRead(PIN_Activar_PAN));         //Bit 9
      Serial.write(digitalRead(PIN_Activar_Safe));        //Bit 10
      Serial.write(digitalRead(PIN_Activar_Tanque));      //Bit 11
      Serial.write(digitalRead(PIN_Activar_Turbo));       //Bit 12
      //Los botones estan invertidos al usar internal pullup
      Serial.write(!digitalRead(PIN_Boton_Hablar));       //Bit 13
      Serial.write(!digitalRead(PIN_Boton_Turbo));        //Bit 14
      Serial.write(!digitalRead(PIN_Boton_TurboRevert));  //Bit 15
      Serial.write(!digitalRead(PIN_Boton_Claxon));       //Bit 16
      Serial.write(!digitalRead(PIN_Boton_ResetView));    //Bit 17
      Serial.write((byte)(analogRead(PIN_Potencia_Mover_X)/4));           //Bit 18
      Serial.write((byte)(analogRead(PIN_Potencia_Mover_Y)/4));           //Bit 19
      Serial.write((byte)(analogRead(PIN_Potencia_Ver_X)/4));             //Bit 20
      Serial.write((byte)(analogRead(PIN_Potencia_Ver_Y)/4));             //Bit 21
      Serial.write((byte)(analogRead(PIN_Potencia_PAN)/4));               //Bit 22
      Serial.write((byte)(analogRead(PIN_Potencia_Claxon)/4));            //Bit 23
      Serial.write((byte)(analogRead(PIN_Potencia_Velocidad)/4));         //Bit 24
      Serial.write((byte)(analogRead(PIN_Potencia_Sensibilidad)/4));      //Bit 25
      Serial.write((byte)(analogRead(PIN_Potencia_Volumen_Mando)/4));     //Bit 26
      Serial.write((byte)(analogRead(PIN_Potencia_Volumen_Vehiculo)/4));  //Bit 27
      //Salir esta invertido, cuando esta apagado es cuando hay que salir
      Serial.write(digitalRead(PIN_Salir));       //Bit 28
      //Bytes sobrandes, puestos ahí porque quizas amplie luego
      Serial.write((byte)0);                      //Bit 29
    }else{
      //Debe ser un error en la transferencia, es raro
      Serial.write("ERROR");
    }
    
    digitalWrite(PIN_Ready, HIGH);
  }

}
