#define PORT_SPEED 9600
#define BYTES_IN 20
#define BYTES_OUT 35

#define PIN_1 8
#define PIN_2 9
#define PIN_3 10
#define PIN_4 7
#define PIN_5 6
#define PIN_6 5
#define PIN_7 4
#define PIN_8 3


void setup() {
  // put your setup code here, to run once:
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(PIN_1, INPUT);
  pinMode(PIN_2, INPUT);
  pinMode(PIN_3, INPUT);
  pinMode(PIN_4, INPUT);
  pinMode(PIN_5, INPUT);
  pinMode(PIN_6, INPUT);
  pinMode(PIN_7, INPUT);
  pinMode(PIN_8, INPUT);
  Serial.begin(PORT_SPEED);
  Serial.setTimeout(50);
}

byte buffIN[BYTES_IN];

void loop() {
  if(Serial.available() == BYTES_IN){

    //digitalWrite(LED_BUILTIN, HIGH);
    //delay(500);
    //digitalWrite(LED_BUILTIN, LOW);
    
    for(int i = 0; i < BYTES_IN; i++)
      buffIN[i] = Serial.read();

    if(buffIN[0] == (byte)1){
      //TEST
      bool last = false;
      for(int i = 0; i < BYTES_OUT; i++){
        Serial.write(last?0:1);
        last = !last;
      }
    }else if(buffIN[0] == 2){
      //RECIVE
      digitalWrite(LED_BUILTIN, buffIN[1]==0?LOW:HIGH);
    }else if(buffIN[0] == 3){
      //SEND
      Serial.write(digitalRead(PIN_1));
      Serial.write(digitalRead(PIN_2));
      Serial.write(digitalRead(PIN_3));
      Serial.write(digitalRead(PIN_5));
      Serial.write(digitalRead(PIN_6));
      Serial.write(digitalRead(PIN_7));
      Serial.write(digitalRead(PIN_8));
      for(int i = 0; i < BYTES_OUT-8; i++)
        Serial.write(0);
      Serial.write(digitalRead(PIN_4));
    }else{
      Serial.write("ERROR");
    }
  }
}
