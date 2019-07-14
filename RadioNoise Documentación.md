# RadioNoise TCP/IP Vehicle Controller
Software para controlar un vehículo radio-control con Raspberry Pi y Arduino, acompañado de diagramas de hardware.
## Índice
### Introducción
### Diseño
### Modelos de comunicación
### Descripción de hardware
### Consideraciones adicionales
### Manual de construcción

## Introducción
Este es un proyecto open source ideado por Borja López y se cede a todo el que quiera intentar replicarlo.
### Objetivos
Crear un vehículo capaz de alcanzar velocidades considerables y portar cargas útiles, controlado mediante TCP/IP usando el modelo cliente servidor y comunicación orientada a conexiones. Se pretende usar componentes accesibles: Arduino y Raspberry Pi (De ahora en adelante RPI).
### Utilidades del proyecto
A efectos prácticos, ninguna más allá del propósito educativo.
## Diseño
Se usará un Arduino Mega en el vehículo y otro en el mando, una RPI 3 en el vehículo y una RPI 4 en el mando. Los adaptadores de red serán dos Alfa Network AWUS036NH con chip set RT3070 y antenas de 5db y 9db intercambiables.

//Agregar imágenes de los modelos 3D

En software se usará Java como lenguaje de programación con las librerías JavaCV, jSerialComm y LWJGL3. Los programas estarán compuestos por módulos independientes de control. Por el momento estos son. 

 - Wifi: Crea el punto de acceso o se conecta a la red.
 - Controlador: Envía información sobre los módulos y la conducción.
 - Vídeo: Envía capturas de una cámara en el vehículo al mando.
 - Audio IN: Recive audio.
 - Audio OUT: Envía audio, en el mando puede activarse el mute.

## Modelos de comunicación
A continuación se detallan los estándares de comunicación empleados.
El arduino del mando envía los controles a la RPI del mando, que los transmite procesados a la RPI del vehículo y esta se los envía su arduino. También es posible la comunicación en el otro sentido. La RPI 3 del vehículo es la que crea el punto de acceso y la RPI4 del mando se conecta.
El siguiente esquema corresponde al modelo de comunicación del modulo de control.
NOTA: Cando un byte se usa de forma booleana toma los valores 0 y 1 en base diez.
### Arduino -> RPI 3 Mando
Esta comunicación es serial y se realiza con un intervalo objetivo de 5ms. El tamaño del buffer es de 30 bytes.

| Posición | Uso |
|--|--|
| 0 | Toggle rueda 1 |
| 1 | Toggle rueda 2 |
| 2 | Toggle rueda 3 |
| 3 | Toggle rueda 4 |
| 4 | Toggle modulo Audio IN |
| 5 | Toggle modulo Audio OUT |
| 6 | Toggle modulo Video |
| 7 | Toggle modulo Controller |
| 8 | Toggle modulo Wifi |
| 9 | Toggle PAN de tracción |
| 10 | Toggle Safe frenada |
| 11 | Toggle Modo tanque |
| 12 | Toggle Turbo |
| 13 | Button Turbo |
| 14 | Button Reverse turbo |
| 15 | Button Claxon |
| 16 | Button Reset view |
| 17 | Direction Move X |
| 18 | Direction Move Y |
| 19 | Direction View X |
| 20 | Direction View Y |
| 21 | Potencia PAN de tracción |
| 22 | Potencia Claxon |
| 23 | Potencia Velocidad |
| 24 | Potencia Audio mando |
| 25 | Potencia Audio vehículo |
| 26 | SALIR |
| 27 | NO SE USA |
| 28 | NO SE USA |
| 29 | NO SE USA |

Aclaración: La rueda inferior izquierda es la 1, la inferior derecha es la 2, la superior izquierda es la 3 y la superior derecha es la 4.
Aclaración: PAN de tracción distribuye la potencia entre las ruedas delanteras y traseras. Safe frenada atenúa la velocidad en inversa para que la frenada activa sea más precisa. ResetView mueve a la posición original los servomotores que controlan la cámara.
### RPI 3 -> Arduino  Mando
Esta comunicación es serial y se realiza con un intervalo objetivo de 5ms. El tamaño del buffer es de 22 bytes.

| Posición | Uso |
|--|--|
| 0 | Control (1 Test, 2 Transferencia, 3 Petición) |
| 1 | LED Rueda 1 Verde |
| 2 | LED Rueda 2 Verde |
| 3 | LED Rueda 3 Verde |
| 4 | LED Rueda 4 Verde |
| 5 | LED Rueda 1 Rojo |
| 6 | LED Rueda 2 Rojo |
| 7 | LED Rueda 3 Rojo |
| 8 | LED Rueda 4 Rojo |
| 0 | LED Modulo Wifi Verde |
| 10 | LED Modulo Controller Verde |
| 11 | LED Modulo Video Verde |
| 12 | LED Modulo Audio IN Verde |
| 13 | LED Modulo Audio OUT Verde |
| 14 | LED Modulo Wifi Rojo |
| 15 | LED Modulo Controller Rojo |
| 16 | LED Modulo Video Rojo |
| 17 | LED Modulo Audio IN Rojo |
| 18 | LED Modulo Audio OUT Rojo |
| 19 | LED Modo tanque Verde |
| 20 | LED Modo tanque Rojo |
| 21 | NO SE USA |

### Arduino -> RPI 4  Vehículo
Esta comunicación es serial y se realiza con un intervalo objetivo de 5ms. El tamaño del buffer es de 5 bytes.

| Posición | Uso |
|--|--|
| 0 | Voltaje Batería 1 (12v) |
|--|--|
| 1 | Voltaje Batería 2 (12v) |
|--|--|
| 2 | Voltaje Motores (24v) |
|--|--|
| 3 | Voltaje Servos (6v) |
|--|--|
| 4 | NO SE USA |

### RPI 4 -> Arduino Vehículo
Esta comunicación es serial y se realiza con un intervalo objetivo de 5ms. El tamaño del buffer es de 10 bytes.

| Posición | Uso |
|--|--|
| 0 | Control (1 Test, 2 Transferencia, 3 Petición) |
| 1 | Potencia Rueda 1 |
| 2 | Potencia Rueda 2 |
| 3 | Potencia Rueda 3 |
| 4 | Potencia Rueda 4 |
| 5 | LED Azul Modulo Controller |
| 6 | LED Azul Modulo Video |
| 7 | LED Azul Modulo Audio IN |
| 8 | LED Azul Modulo Audio OUT |
| 9 | NO SE USA |

### RPI 4 mando <-> RPI 3 vehículo
A continuación los procesos de comunicación de los diferentes módulos.
#### Controller
Se realiza en intervalos objetivos de 10ms.
Comienza hablando el cliente. Si no se recibe respuesta antes del timeout la conexión se da por perdida.
#### Video
Una vez se establece la conxión el servidor toma 10 fotografías por segundo de 640x480, primero envía el tamaño del frame comprimido con jpeg (usando 4 bytes) y luego envía el frame completo en un solo paquete. El cliente las recibe y muestra por pantalla.
#### Audio IN y OUT
El micrófono y altavoces se configuran en mono 16 bits y 4800 muestras por segundo. El tamaño del buffer es de 1024 bytes.
## Descripción de hardware
//Crear y adjuntar modelos creados con software especifico. Y que no se te olviden las tablas del pinout de arduino.
## Consideraciones adicionales
JavaCV es una jodienda para instalar en RPI, 5 horas compilando.
## Manual de construcción
