package radionoisecontroller;

import com.fazecast.jSerialComm.SerialPort;
import radionoisecontroller.modules.*;
import static radionoisecontroller.global.*;
import radionoisecontroller.graphics.WindowManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {
    /*
        POSICONES DE LOS DATOS DE ENTRADA (35 bytes de datos)
        2 3
        0 1 : Toggle Ruedas
        
        4, 5, 6, 7, 8 : Toggle AudioIN AudioOUT Video Controller Wifi
        9, 10, 11, 12 : Toggle Pan Save Tank Turbo
        13, 14, 15, 16 : Toggle Turbo TurboReverso Claxon ResetView
        
        17, 18, 19, 20 : Potenciometer MoveX MoveY ViewX ViewY
        21, 22, 23, 24, 25 : Potenciometer Pan Claxon Power AudioSelf AudioCar
        26 : Salida
        27, ..., 29 ; NO SE USA
    
        VALORES DE SALIDA (1 byte de control + 19 bytes de datos)
        1 : Test (se espera recibir un patron 1 0 1 0... como confirmacion)
        2 : Transferencia
            POSICIONES DE LOS DATOS
            5-6 7-8
            1-2 3-4 : LEDS Reuedas
            9-10 11-12 13-14 15-16 : LEDS Conexiones
            17-18 : LED Control de tanque
            19 : NO SE USA
        3 : Pedir estado
        
    */
    private static byte[] curState = new byte[BYTES_IN], pasState = new byte[BYTES_IN];
    private static module_controller controller;
    private static module_video video;
    private static module_audioIN audioIN;
    private static module_audioOUT audioOUT;
    private static module_wifi wifi;
    
    private static SerialPort port;
    
    private static byte[] outBuffer = new byte[BYTES_OUT];
    private static byte[] recvData, sendData;
    
    private static int blink_conn;
    
    private static WindowManager WM;
    
    public static boolean initiate(){
        port = SerialPort.getCommPort(ARDUINO_PORT);
        port.setBaudRate(BAUD_SPEED);
        port.setComPortParameters(BAUD_SPEED, 8, 1, 0);
        port.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 100, 0);
        
        if(!port.openPort())
            return false;
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        sendData = new byte[BYTES_SEND];
        recvData = new byte[BYTES_RECIVE];
        
        blink_conn = 0;
        
        WM = null;
        return true;
    }
    
    public static boolean arduinoTest(){
        outBuffer[0] = (byte)1;
        byte[] recv = new byte[BYTES_IN];
        data_exchange(outBuffer, recv);
        
        //System.out.println("RECIVIDO: "+Arrays.toString(recv));
        
        boolean last = false, good = true;
        int i = 0;
        while(i < recv.length && good){
            good = recv[i] == (byte)(last?0:1);
            last = !last;
            i++;
        }
        return good;
    }
    
    public static void act(){
        System.arraycopy(curState, 0, pasState, 0, curState.length);
        outBuffer[0] = (byte)3;
        data_exchange(outBuffer, curState);
        
        //TODO: Toda la logica
        
        //Iniciar e interrumpir modulos
        if(curState[2] == 0 && controller != null){
            controller.interrupt();
            controller = null;
        }
        if(curState[2] == 1 && pasState[2] == 0 && (controller == null || !controller.isAlive())){
            controller = new module_controller(sendData, recvData);
            controller.start();
        }
        if(curState[3] == 0 && video != null){
            video.interrupt();
            video = null;
        }
        if(curState[3] == 1 && pasState[3] == 0 && (video == null || !video.isAlive())){
            video = new module_video(WM.getVideoBuffer(), WM);
            video.start();
        }
        
        if(curState[4] == 0 && audioIN != null){
            System.out.println("DEBERIA CERRAR CLIENTE");
            audioIN.interrupt();
            audioIN = null;
        }
        if(curState[4] == 1 && pasState[4] == 0 && (audioIN == null || !audioIN.isAlive())){
            audioIN = new module_audioIN();
            audioIN.start();
        }
        if(curState[5] == 0 && audioOUT != null){
            System.out.println("DEBERIA CERRAR SERVIDOR");
            audioOUT.interrupt();
            audioOUT = null;
        }
        if(curState[5] == 1 && pasState[5] == 0 && (audioOUT == null || !audioOUT.isAlive())){
            audioOUT = new module_audioOUT();
            audioOUT.start();
        }
        
        if(curState[6] == 0 && pasState[6] == 1 && audioOUT != null){
            audioOUT.setEnabled(false);
        }
        if(curState[6] == 1 && pasState[6] == 0 && audioOUT != null){
            audioOUT.setEnabled(true);
        }
        //RECIVIDO DESDE ARDUINO    4, 5, 6, 7, 8 : Toggle AudioIN AudioOUT Video Controller Wifi
        //ENVIO AL PLAYER           4 5 6 : Toggle AudioOUT AudioIN Video
        sendData[4] = curState[4];
        sendData[5] = curState[5];
        sendData[6] = curState[6];
        
        if(controller != null){
            switch(controller.check()){
                case 0:
                    outBuffer[1] = (byte)0;
                    break;
                case 1:
                    if(blink_conn++ == TICKS_PER_BLINK){
                        outBuffer[1] = not(outBuffer[1]);
                        blink_conn = 0;
                    }
                    break;
                case 2:
                    outBuffer[1] = (byte)1;
                    break;
            }
        }else outBuffer[1] = (byte)0;
        
        
        //Actualizaciones de controles
        drive();
        
        
        //Actualizar pantallas
        WM.act(curState, pasState, recvData);
        
        
        
        outBuffer[0] = (byte)2;
        port.writeBytes(outBuffer, BYTES_OUT);
        //System.out.println(Arrays.toString(curState));
    }
    
    public static boolean should_continue(){
        return curState[BYTES_IN-1] == 0;
    }
    
    public static void stop(){
        port.closePort();
        
        if(controller != null){
            controller.interrupt();
            controller = null;
        }
        
        if(video != null){
            video.interrupt();
            video = null;
        }
        if(audioIN != null){
            audioIN.interrupt();
            audioIN = null;
        }
        if(audioOUT != null){
            audioOUT.interrupt();
            audioOUT = null;
        }
        if(wifi != null){
            wifi.interrupt();
            wifi = null;
        }
    }
    
    public static void setWindowManager(WindowManager WM){
        Controller.WM = WM;
    }
    
    
    private static void data_exchange(byte[] outBuffer, byte[] inBuffer){
        port.writeBytes(outBuffer, outBuffer.length);
        
        while(port.bytesAvailable() != inBuffer.length){
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        port.readBytes(inBuffer, inBuffer.length);
    }
     
   public static void reportDie(Class c){
        if(c == module_controller.class) controller = null;
        else if(c == module_video.class) video = null;
        else if(c == module_audioIN.class) audioIN = null;
        else if(c == module_audioOUT.class) audioOUT = null;
        else if(c == module_wifi.class) wifi = null;
   }
    
    /*
        curState son los controles y sendData es la salida
    */
    private static void drive(){
       sendData[0] = not(curState[1]);
    }
    
    
    private static byte not(byte b){
        return (byte)(b==1?0:1);
    }

}
