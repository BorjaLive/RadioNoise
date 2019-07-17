
# RadioNoise TCP/IP Vehicle Controller
Software para controlar un vehículo radio-control con Raspberry Pi y Arduino, acompañado de diagramas de hardware.
## Índice
### Introducción
### Diseño
### Modelos de comunicación
### Descripción de hardware
### Consideraciones adicionales
### Instalación de software
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
### Arduino -> RPI 4 Mando
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
| 13 | Button Audio OUT activar |
| 14 | Button Turbo |
| 15 | Button Reverse turbo |
| 16 | Button Claxon |
| 17 | Button Reset view |
| 18 | Direction Move X |
| 19 | Direction Move Y |
| 20 | Direction View X |
| 21 | Direction View Y |
| 22 | Potencia PAN de tracción |
| 23 | Potencia Claxon |
| 24 | Potencia Velocidad |
| 25 | Potencia Sensividlidad camara |
| 26 | Potencia Audio mando |
| 27 | Potencia Audio vehículo |
| 28 | SALIR |
| 29 | NO SE USA |

Aclaración: La rueda inferior izquierda es la 1, la inferior derecha es la 2, la superior izquierda es la 3 y la superior derecha es la 4.
Aclaración: PAN de tracción distribuye la potencia entre las ruedas delanteras y traseras. Safe frenada atenúa la velocidad en inversa para que la frenada activa sea más precisa. ResetView mueve a la posición original los servomotores que controlan la cámara.
### RPI 4 -> Arduino  Mando
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
| 9 | LED Modulo Wifi Verde |
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

### Arduino -> RPI 3  Vehículo
Esta comunicación es serial y se realiza con un intervalo objetivo de 5ms. El tamaño del buffer es de 5 bytes.

| Posición | Uso |
|--|--|
| 0 | Voltaje Batería 1 (12v) |
| 1 | Voltaje Batería 2 (12v) |
| 2 | Voltaje Motores (24v) |
| 3 | Voltaje Servos (6v) |
| 4 | NO SE USA |

### RPI 3 -> Arduino Vehículo
Esta comunicación es serial y se realiza con un intervalo objetivo de 5ms. El tamaño del buffer es de 15 bytes.

| Posición | Uso |
|--|--|
| 0 | Control (1 Test, 2 Transferencia, 3 Petición) |
| 1 | Potencia Rueda 1 |
| 2 | Potencia Rueda 2 |
| 3 | Potencia Rueda 3 |
| 4 | Potencia Rueda 4 |
| 5 | Sentido Rueda 1 |
| 6 | Sentido Rueda 2 |
| 7 | Sentido Rueda 3 |
| 8 | Sentido Rueda 4 |
| 9 | LED Azul Modulo Controller |
| 10 | LED Azul Modulo Video |
| 11 | LED Azul Modulo Audio IN |
| 12 | LED Azul Modulo Audio OUT |
| 13 | NO SE USA |
| 14 | NO SE USA |

### RPI 4 mando <-> RPI 3 vehículo
A continuación los procesos de comunicación de los diferentes módulos.
#### Controller
Se realiza en intervalos objetivos de 10 ms.
Comienza hablando el cliente. Si no se recibe respuesta antes del timeout la conexión se da por perdida.

#### RPI4 Mando -> RPI3 Verhiculo
El mando envía 18 bytes.

| Posición | Uso |
|--|--|
| 0 | Potencia Rueda 1 |
| 1 | Potencia Rueda 2 |
| 2 | Potencia Rueda 3 |
| 3 | Potencia Rueda 4 |
| 4 | Sentido Rueda 1 |
| 5 | Sentido Rueda 2 |
| 6 | Sentido Rueda 3 |
| 7 | Sentido Rueda 4 |
| 8 | Toggle AudioIN |
| 9 | Toggle AudioOUT |
| 10 | Toggle Video |
| 11 | Claxon |
| 12 | Angulo vista Z |
| 13 | Angulo vista Y |
| 14 | Potencia claxon |
| 15 | Potencia audio |
| 16 | NO SE USA |
| 17 | NO SE USA |

#### RPI3 Verhiculo -> RPI4 Mando
El vehículo envía 5 bytes.

| Posición | Uso |
|--|--|
| 0 | Voltaje batería 1 |
| 1 | Voltaje batería 2 |
| 2 | Voltaje baterias |
| 3 | Voltaje pilas |
| 4 | NO SE USA |

#### Video
Una vez se establece la conxión el servidor toma 10 fotografías por segundo de 640x480, primero envía el tamaño del frame comprimido con jpeg (usando 4 bytes) y luego envía el frame completo en un solo paquete. El cliente las recibe y muestra por pantalla.
#### Audio IN y OUT
El micrófono y altavoces se configuran en mono 16 bits y 4800 muestras por segundo. El tamaño del buffer es de 1024 bytes.

## Descripción de hardware
//Crear y adjuntar modelos creados con software especifico. Y que no se te olviden las tablas del pinout de arduino.

### Pinout Arduino Mega mando

| Pin y modo | Uso |
|--|--|
| D 22 IN | Toggle Rueda 1 |
| D 24 IN | Toggle Rueda 2 |
| D 26 IN | Toggle Rueda 3 |
| D 28 IN | Toggle Rueda 4 |
| D 30 IN | Toggle Module Wifi |
| D 32 IN | Toggle Module Controller |
| D 34 IN | Toggle Module Video |
| D 36 IN | Toggle Module Audio IN |
| D 38 IN | Toggle Module Audio OUT |
| D 40 IN | Toggle PAN de tracción |
| D 42 IN | Toggle Safe frenada |
| D 44 IN | Toggle Modo tanque |
| D 46 IN | Toggle Turbo activado |
| D 48 IN | Button Turbo |
| D 31 IN | Button Reverse Turbo |
| D 33 IN | Button Claxon |
| D 35 IN | Button Reset View |
| D 37 IN | Button Activar Audio OUT |
| A 0 IN | Potenciómetro Move X |
| A 1 IN | Potenciómetro Move Y |
| A 2 IN | Potenciómetro View X |
| A 3 IN | Potenciómetro View Y |
| A 4 IN | Potenciómetro PAN de tracción |
| A 5 IN | Potenciómetro Volumen claxon |
| A 6 IN | Potenciómetro Potencia |
| A 7 IN | Potenciómetro Sensibilidad cámara |
| A 8 IN | Potenciómetro Volumen mando |
| A 9 IN | Potenciómetro Volumen vehículo |
| D 39 OUT | LED Verde Rueda 1 |
| D 41 OUT | LED Verde Rueda 2 |
| D 43 OUT | LED Verde Rueda 3 | 
| D 45 OUT | LED Verde Rueda 4 |
| D 47 OUT | LED Rojo Rueda 1 |
| D 49 OUT | LED Rojo Rueda 2 |
| D 12 OUT | LED Rojo Rueda 3 |
| D 11 OUT | LED Rojo Rueda 4 |
| D 10 OUT | LED Verde Modo tanque |
| D 9 OUT | LED Rojo Modo tanque |
| D 8 OUT | LED Verde Module Wifi |
| D 7 OUT | LED Verde Module Controller |
| D 6 OUT | LED Verde Module Video |
| D 5 OUT | LED Verde Module Audio IN |
| D 4 OUT | LED Verde Module Audio OUT |
| D 3 OUT | LED Rojo Module Wifi |
| D 2 OUT | LED Rojo Module Controller |
| D 14 OUT | LED Rojo Module Video |
| D 15 OUT | LED Rojo Module Audio IN |
| D 16 OUT | LED Rojo Module Audio OUT |

### Pinout Arduino Mega vehículo

| Pin y modo | Uso |
|--|--|
| A 15 IN | Tensión Batería 1 (12v) |
| A 14 IN | Tensión Batería 2 (12v) |
| A 13 IN | Tensión Motores (24v) |
| A 12 IN | Tensión Servos (6v) |
| D 7 OUT | Control Servo cámara Z |
| D 6 OUT | Control Servo cámara Y |
| D 26 OUT | LED Azul Module Controller |
| D 28 OUT | LED Azul Module Video |
| D 30 OUT | LED Azul Module Audio IN |
| D 32 OUT | LED Azul Module Audio OUT |
| D 12 OUT | Potencia motor 1 |
| D 11 OUT | Potencia motor 2 |
| D 10 OUT | Potencia motor 3 |
| D 9 OUT | Potencia motor 4 |
| D 31 OUT | Sentido 1 motor 1 |
| D 33 OUT | Sentido 2 motor 1 |
| D 35 OUT | Sentido 1 motor 2 |
| D 37 OUT | Sentido 2 motor 2 |
| D 39 OUT | Sentido 1 motor 3 |
| D 41 OUT | Sentido 2 motor 3 |
| D 43 OUT | Sentido 1 motor 4 |
| D 45 OUT | Sentido 2 motor 4 |

### LEDs
Todos los leds consumen 20 mA, por seguridad se considera 17 mA.

| LED | Voltaje |
|--|--|
| Verde 5mm | 3.0 - 3.4 |
| Rojo 5mm | 2.8 - 3.1 |
| Azul 5mm | 3.0 - 3.4 |
Es por esto que se ha decidido usar dos resistencias en serie de 100 y 22 ohm para los verdes (Calculada entre 130 y 95 ) y 100 ohm para los rojos y azules (Calculada entre 120 y 80).
La potencia disipada por las resistencias no excede los 50 mW, que es inferior a su límite de 1/4 W. La tolerancia es de 1%.

### Chasis
La base del chasis está compuesta por cuatro tablones de madera de pino macizo. El resto de paneles son de cartón pluma de 10mm de espesor.
En estas imágenes puede base que la base está formada por dos tablones de 35 cm de largo, 5 cm de ancho y 3 cm de grosor. Entre medias hay dos tablones de 15 cm de largo e igualmente 5 cm de ancho y grosor de 3 cm.
Sobre esa base se construye una estructura que protege los componentes del viento y, la verdad sea dicha, es lo que le da la forma molona de coche deportivo.
Para más información detallada sobre las medidas y ensamblaje del cartón pluma, consultar la sección "Manual de construcción".

## Consideraciones adicionales
JavaCV es una jodienda para instalar en RPI, 5 horas compilando.
Cómprate un coche RC, te va a salir más barato.
## Instalación de  Software
Descarga la release más reciente de [este repositorio](https://github.com/BorjaLive/RadioNoise).
Si dispones de conocimientos en Java y Arduino, puedes intentar clonarlo y compilarlos tu mismo.
Descarga primero las librerías jSerialComm, LWjGL3 y JavaCV para ARM. IMPORTANTE, es última no se distribuye con soporte para ARM, puedes compilarlo tu mismo; pero tarda 5 horas o puedes descargarlo ya listo desde [aquí](http://www.dalbert.net/?p=433).
### Arduino
Necesitaras descargar su IDE para cargar el código.
Descarga la versión más reciente para tu sistema operativo en [este enlace](https://www.arduino.cc/en/Main/Software). 
Abre los archivos ControllerScript y PlayerScript, cargalos en la placa y listo.
### Raspberyy Pi
Monta la imagen más reciente de Raspbian en tu SD de preferencia, siguiendo los pasos descritos en [esta página](https://www.raspberrypi.org/downloads/raspbian/).
Comprueba que tienes instalado Java 8, de no ser así, instalalo [así](https://raspberrypi.stackexchange.com/questions/4683/how-to-install-the-java-jdk-on-raspberry-pi).
Comprueba que tienes Arduino IDE instalado, si no es así; instálalo como si estubieras en un ordenador.
Comprueba que la tarjeta de red funciona correctamente. Si has decidido usar Alfa AWUS036NH, igual que yo, sigue el siguiente tutoría.
### PC
¿Quieres controlar el coche desde un ordenador? ¿Quieres controlarlo a través de Internet?
Es posible, descarga las versiones de ordenador del controlador y listo.


## Manual de construcción
### Materiales
Los materiales necesarios se listarán en cada sección.  La siguiente lista de herramientas es recomendada, pero siéntete libre de usar cualquier técnica que consideres mejor; yo tampoco soy ingeniero industrial.

 - Pistola de pegamento termo-fusible
 - Cutter de hoja de 18mm, también es posible uno más pequeño, pero se recomienda este.
 - Cutter de precisión. No es estrictamente necesario pero asegura los mejores resultados.
 - Estación de soldadura de estaño. Completamente opcional, es posible terminar el proyecto sin hacer una sola soldadura.
 - PC Linux o Windows x86-64 con conexión a Internet.
 - Destornillador de estrella.
 - Multímetro.

Al final del documento doy una lista de páginas donde compré los componentes, incluyendo los modelos exactos. 

### Chasis
#### Materiales
Vas a necesitar madera, o un material igualmente resistente.
Dos piezas de 25x5x3 cm y otras dos piezas de 15x5x3 cm. También es posible usar grosores diferentes a 3 cm.

De la misma forma, es posible remplazar el cartón pluma con otro material, pero la proporción resistencia / peso lo hacen un candidato perfecto. Necesitarás un pliego de 100x70 cm (B1) o dos pliegos de 50x70 cm (B2), lo que puedas encontrar de 10mm y 4 pliegos A4, o dos 2 A3, o uno A1. Si no consigues las medidas exactas, no importa; puedes intentar encajar las piezas en los pliegos que tengas disponibles.

Cuatro bisagras pequeñas, unos 5 cm de largo.

Cuidado, aunque toda la madera pueda costar menos de 5€, las láminas buenas de cartón pluma de 10mm cuestan unos 15€ cada una.

#### Construcción
Empezamos por crear una base sólida. Une con 8 escuadras y tornillos auto-perforantes los tablones de madera de la siguiente forma.
![Espaciado ideal de los tablones](https://i.imgur.com/RDV5zq3.png)
![Vista en perspectiva](https://i.imgur.com/Su6Q1Is.png)
Mejor decirlo ahora, no soy dibujante técnico (aunque he hecho un curso), las cotas no siguen las normas ISO pero son explicativas.

Para el resto vas a necesitar las siguientes piezas, los nombres se mantendrán hasta el final del documento (medidas en centímetros):

 - Superficie instrumental: 23x27
 - Superficie de carga: 13x27
 - Divisor de compartimentos: 25x15
 - Panel trasero: 25x10
 - Panel delantero: 27x5
 - Techo 25x18
 - Puerta trasera: 25x8
 - Puerta delantera: 25x18
 - Soportes x2:  triangulo de 5x5
 - Paneles laterales x2: Figura compleja, se adjuntan planos.![Panel lateral](https://i.imgur.com/bQaI0e1.png)

Las medidas están ajustadas para encajar las piezas de tal forma que se garantice una buena resistencia a golpes frontales, a la vez que se prioriza la estética en las uniones.

Si optas por comprar un pliego B1 (aprox 100x70 cm) de cartón pluma, puedes seguir esta guía de disposición.

![Disposicion para B1](https://i.imgur.com/4WiCRpl.png)

Si solo dispones de pliegos B2 (aprox 70x50 cm), también he creado la siguiente disposición en dos pliegos.

![Disposicion para B2](https://i.imgur.com/J597puH.png)

Para otras medidas, diviértete jugando al Tetris.

Para empezar la construcción, coloca las superficies. Importante, sobresale  un centímetro de la base de madera y hay que dejar un centímetro de separación entre las superficies. No las pegues aún.
![Colocación de las superficies](https://i.imgur.com/iF83RXA.png)
El divisor de compartimentos se coloca entre las superficies. Ahora puedes pegarlo todo.
![enter image description here](https://i.imgur.com/H8Rzd9e.png)
A continuación coloca los paneles. El trasero se apoya sobre la superficie, pero IMPORTANTE el delantero no. El panel delantero se coloca delante de la superficie.
![Colocación de los paneles](https://i.imgur.com/W9xRbyA.png)
Los paneles laterales deberían encajar si has sido preciso.
![Panel lateral](https://i.imgur.com/fD0GtyU.png)
Finalmente, puedes pegar techo o no. Recomiendo continuar con la instalación del instrumental antes de colocar el resto de piezas.
Cuando coloques el techo, no olvides poner primero los soportes.
![Techo y soporte](https://i.imgur.com/x3aGOtJ.png)
Para rematar, las puertas. Este debería ser el último paso una vez verifiques que toda la electrónica está funcionando. La abertura trasera puede ser útil para realizar reparaciones, pero no es demasiado cómoda.
Al colocar las puertas con las bisagras, puedes cortar los cantos un poco para que encajen mejor. Adicionalmente puedes ponerle cualquier mecanismo de cierre que prefieras.

### Electrónica
#### Materiales
Como ordenador principal usaremos una Raspberry PI 3, si vienes del futuro, deberías poder usar sus sucesores aunque no está probado que funcione. Banana Pi y Orange Pi son alternativas pero no han sido probadas.

Para la interfaz con los componentes electricos dispondremos de un Arduino Mega. Se podría usar un Uno, Leonardo o similares en función de las capacidades que quieras implementar. La limitación para este proyecto es el número de pines.  Manejar los motores puede ser complicado, para ello usaremos el shield L293D, capaz de 4 canales DC 4.5-36v maximo 600 mA y 1.2 A de pico.

Estos son los componentes eléctricos necesarios:

 - Batería de 24v. La puedes conseguir con 2 baterías de litio de 12v o con 16 pilas AA.
 - Bacteria de 6v. Recomendable usar 4 pilas AA.
 - 4 motores de 24v mínimo. Idealmente de 12-36v de tensión nominal y no más de 600 mA a 24v.
 - LEDs de 5mm.
 - Resistencias de 100 ohms o las necesarias para tus LEDs
 - Resistencias de, por lo menos 100 kiloohnms.
 - Interruptor capaz de soportar altas tensiones y corrientes.
 - Ruedas de goma entre 6 y 12 cm de diámetro.
 - Adaptador de 4-6mm a 12mm hexagonal, para conectar motor y redas.
 - Soporte para motor DC, el que encaje con tu motor.
 - Jacks DC.
 - Jumpers de Arduino.
 - Protoboard Half+ (recomendada)
 - Powerbank capaz de dar 2.5A.
 - Servomotores (recomendados MG 996R) con estructura gimbal.
 - Tarjeta SD, V60 para un arranque rápido. El único requisito es que tenga mínimo 8 GB.

Estos accesorios son recomendados para mejorar la experiencia:

 - Cámara web capaz de 10 fps y 640x480.
 - Micrófono omnidireccional.
 - Altavoces jack con carga por USB.
 - Adaptador USB Jack 3.5.
 - Antena de alta ganancia.

Extras:

 - Tacos de madera de 10 cm de largo.
 - Medidor de batería para 24v.
 - Interruptor con llave, queda guay.

Recuerda que puedes dar rienda suelta a tu creatividad y modificar este proyecto a tus gustos. La única limitación es tu presupuesto.

#### Construcción
Comencemos por lo básico. El software necesario ya está desarrollado y es open source. Se encuentra en [este repositorio](http://github.com/BorjaLive/RadioNoise/).  Lo único que tienes que hace es seguir la guía de instalación en la sección 

Si ya has instalado el software, podemos continuar.

Primero organiza las baterías, la RPI, la protoboard y el Arduino. Recuerda que las baterías definirán el centro de masas del vehículo, idealmente debería estar en el medio; aunque es mejor que quede desplazada hacia tras que hacia delante ya que, cuando se coloque la carga el centro de masas se irá hacia delante.
En mi caso, uso dos baterías de 12v que pesan medio quilo cada una, las pondré a los lados y en el fondo colocaré el power bank junto a las pilas AA. En el sitio sobrante caben justas una protoboard, el Arduino y la RPI. También he colocado los altavoces.
Recomiendo no fijar los componentes todavía, puede que se os ocurra una disposición mejor con el tiempo.
Para colocar los tacos de madera, coloca los componentes encima y marca donde deberían estar. Con un lápiz, marca la posición. Coloca los tacos y pégalos con termofusible y escuadras si lo crees necesario. 
![Disposición](https://i.imgur.com/DUomhnb.png)

Cunado estés seguro, atornilla los componentes.
![Componentes colocados here](https://i.imgur.com/g2mVn23.png)

Por último, no olvides la antena wifi de alta ganancia.
![Disposición final](https://i.imgur.com/77JNCzq.png)

Ahora colocaremos las ruedas. Recomiendo sacar todas las piezas que no estén fijas antes.
Elige una distancia a la que quieras atornillar los motores, yo he elegido 5cm de margen con el borde porque mis ruedas son bastante grandes. Utiliza el soporte con tornillos para que no se mueva el motor y coloca el adaptador de 4-6mm a 12mm hexagonal, luego aprieta la rueda con fuerza hasta que notes que ha entrado totalmente. No olvides que los adaptadores hexagonales suelen tener tornillos de seguridad.
![Resultado final de las ruedas](https://i.imgur.com/jr4xURk.png)

Ya debería estar listo para dar una vuelta. Si aún no tienes el mando, el programa de control funciona también en ordenadores.

El paso final es realizar las conexiones. Consulta la tabla de pines de esta documentación para el pinout de arduino. El resto de conexiones son sencillas. Necesitaras cables USB de varios tipos, cables de cobre convencionales.
¿Recuerdas los pequeños huecos que quedaban al ensamblar el chasis? Si cortas un poco la superficie de instrumentación, tienes opción de colocar luces adicionales, o puedes sacar por ahí los cables. En mi caso sacaré los cables de los motores por otros huecos en los laterales y pegaré los cables con termofusible a los lados internos de la madera.

### Mando
#### Materiales
Necesitarás cartón pluma de 3mm de grosor o un material similar. Bastará con un pliego A2, o dos A3. Incluso puedes hacerlo con pliegos A4.
Yo usaré una Rasoberry Pi 4 porque quiero que tenga dos pantallas, pero no es necesario, funcionará también con una RPI 3. Por otro lado, hace falta un Arduino Mega y una protoboard pequeña.

Materiales electrónicos:
 - 13 interruptores.
 - 4 pulsadores.
 - 6 potenciómetros lineales.
 - 2 joysticks.
 - 9 LEDs verdes y otros 9 LEDs rojos.
 - 18 resistencias para los LEDs.
 - Tres resistencias de 10k ohms.
 - Jumpers para arduino.
 - Pantalla para Raspberry Pi 800x480.
 - Dos cable micro USB.
 - Dos cables micro HDMI.
 - Varios alargadores jack 3.5.
 - Un cable USB tipo C.
Material adicional:
 - Altavoces.
 - Micrófono.
 - Adaptador USB jack 3.5.
 - Hub USB.
 - Segunda pantalla Raspberry Pi.
 - Adaptador Wifi de alta ganancia.
 - Antena extra.

#### Construcción
Se supone instalado el software. Puedes obtenerlo en la guía de software.

Para empezar, cortaremos las piezas de cartón pluma.
Usaremos las siguientes piezas. Medidas en centímetros.

 - Frontal y trasero: dos de 25x15
 - Laterales: dos de 25x8
 - Tapas: dos de  15x8

Si dispones de dos pliegos A3, puedes seguir el siguiente esquema; aunque seguramente haya otra forma mejor.
![Disposición 2 A3](https://i.imgur.com/QVDZ0mP.png)

Si tienes un pliego A2, la siguiente es la mejor distribución.

![Disposicion 1 A2](https://i.imgur.com/McOVr2F.png)

Si quieres usar pliegos A4, diviértete jugando al Tetris.

En mi caso, usaré dos pantallas, colocaré unas bisagras para hacer un mecanismo tipo Nintendo DS. Esto es totalmente adicional, el software dispone de una versión que solo utiliza una pantalla.

Antes de pegar nada, es conveniente que imprimas la siguiente plantilla para marcar las perforaciones necesarias. Está en A4 tamaño real, respetando margenes y a 300 ppx.
![Guia taladro ](https://i.imgur.com/rnHZTsq.png)

Las conexiones del arduino están detalladas en su sección. El resto de cables requieren perforar las paredes.

Con los agujeros hechos, inserta cada componente y asegúralo con las tuercas que traen. No olvides los altavoces, los cables de audio, ni el hub USB. Para unir las caras, usa pegamento termo-fusible y escuadras. Pega la protoboard en una lateral y haz los agujeros para el power bank.

No hace falta que colques la tapa inferior; y si lo haces, pon visagras y un mecanismo de cierre. Como todo está pegado, es dificil que se caigan piezas.

Estas imagenes te ayudarán a visualizar el resultado final:
![Vista mando](https://i.imgur.com/wqgfHIa.png)

Aquí puedes ver la disposición de los controles.
![Vista mando top](https://i.imgur.com/1izCW16.png)

En el aldo izquierdo se situan los controles de audio así como salidas auxiliares.
![Vista mando izquierda](https://i.imgur.com/zvYWcgm.png)

En el lado derecho están los controles de velocidad, sensivilidad y volumen. Adicionalmente hay un hub USB y unos altavoces a cada lado.
![Vista mando derecha](https://i.imgur.com/y4NQ0ha.png)

Esta disposición es la más lógica que he encotnrado.
Entre los altavoces y el powerbank se rellena el final (sí, es un mal diseño que el centro de masas esté arriba, pero los joysticks son demasiado grandes) La protoboard está pegada a la pared izquierda y la protoboard al principio, para hacerlas accesibles.
![Disposicion componentes](https://i.imgur.com/HuEq91r.png)

