package radionoiseplayer;

import com.fazecast.jSerialComm.SerialPort;
import radionoiseplayer.modules.*;
import static radionoiseplayer.global.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {
    /*
        POSICONES DE LOS DATOS DE ENTRADA (5 bytes de datos)
        2 3
        0 1 : Voltaje baterias ruedas
        4 : Salida
    
        VALORES DE SALIDA (1 byte de control + 9 bytes de datos)
        1 : Test (se espera recibir un patron 1 0 1 0... como confirmacion)
        2 : Transferencia
            POSICIONES DE LOS DATOS
            2 3
            0 1 : Potencia ruedas
            4 5 : Angulo vision camara
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
        
        controller = new module_controller(sendData, recvData);
        
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
        if(sendData[4] == 0 && audioOUT != null){
            audioOUT.interrupt();
            audioOUT = null;
        }
        if(sendData[4] == 1 && sendData[4] == 0 && (audioOUT == null || !audioOUT.isAlive())){
            audioOUT = new module_audioOUT();
            audioOUT.start();
        }
        if(sendData[4] == 0 && audioIN != null){
            audioIN.interrupt();
            audioIN = null;
        }
        if(sendData[4] == 1 && sendData[4] == 0 && (audioIN == null || !audioIN.isAlive())){
            audioIN = new module_audioIN();
            audioIN.start();
        }
        
        if(sendData[6] == 0 && video != null){
            video.interrupt();
            video = null;
        }
        if(sendData[6] == 1 && sendData[6] == 0 && (video == null || !video.isAlive())){
            video = new module_video();
            video.start();
        }
        
        
        
        
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
    
    
    private static byte not(byte b){
        return (byte)(b==1?0:1);
    }

}
