package radionoiseplayer;

import com.fazecast.jSerialComm.SerialPort;
import java.util.Arrays;
import radionoiseplayer.modules.*;
import static radionoiseplayer.global.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {
    
    private static byte[] curState, pasState;
    private static module_controller controller;
    private static module_video video;
    private static module_audioIN audioIN;
    private static module_audioOUT audioOUT;
    
    private static SerialPort port;
    
    private static byte[] outBuffer;
    private static byte[] recvData, sendData;
    
    private static boolean claxon_enabled;
    private static byte claxon_frequency;
    
    public static boolean initiate(){
        sendData = new byte[BYTES_SEND];
        recvData = new byte[BYTES_RECIVE];
        outBuffer = new byte[BYTES_OUT];
        curState = new byte[BYTES_IN];
        pasState = new byte[BYTES_IN];
        
        controller = new module_controller(sendData, recvData);
        controller.start();
        
        return true;
    }
    
    public static boolean arduinoTest(){
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
        //data_exchange(outBuffer, curState);
        
        //TODO: Toda la logica
        
        //Iniciar e interrumpir modulos
        if(recvData[9] == 0 && audioOUT != null){
            audioOUT.interrupt();
            audioOUT = null;
        }
        if(recvData[9] == 1 && audioOUT == null){
            audioOUT = new module_audioOUT();
            audioOUT.start();
        }
        if(recvData[8] == 0 && audioIN != null){
            audioIN.interrupt();
            audioIN = null;
        }
        if(recvData[8] == 1 && audioIN == null){
            audioIN = new module_audioIN();
            audioIN.start();
        }
        
        if(recvData[10] == 0 && video != null){
            video.interrupt();
            video = null;
        }
        if(recvData[10] == 1 && video == null){
            video = new module_video();
            video.start();
        }
        
        drive();
        
        //TODO: Actualizar los volumenes
        
        
        //Claxon
        if(recvData[11]==1 != claxon_enabled){
            claxon_enabled = recvData[11] == 1;
            if(audioIN != null)
                audioIN.setClaxon(claxon_enabled);
        }
        if(recvData[14] != claxon_frequency){
            claxon_frequency = recvData[14];
            if(audioIN != null)
                audioIN.setFrequency(byte2int(claxon_frequency)*78+20);
        }
        
        
        outBuffer[0] = (byte)2;
        //port.writeBytes(outBuffer, BYTES_OUT);
        //System.out.println(Arrays.toString(recvData));
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
   }
    
    /*
        curState son los controles y sendData es la salida
    */
    private static void drive(){
        //Enviar los voltajes medidos
        float voltaje_main = byte2float(curState[0])*VOLTAJE_DIVIDER_CONSTANT, voltaje_servo = byte2float(curState[1])*VOLTAJE_DIVIDER_CONSTANT;
        sendData[0] = (byte) (((voltaje_main-VOLTAJE_MAIN_MIN)/(VOLTAJE_MAIN_MAX-VOLTAJE_MAIN_MIN))*255);
        sendData[1] = (byte) (((voltaje_servo-VOLTAJE_SERVO_MIN)/(VOLTAJE_SERVO_MAX-VOLTAJE_SERVO_MIN))*255);
        
        //Movimiento de las ruedas
        outBuffer[1] = recvData[0];
        outBuffer[2] = recvData[1];
        outBuffer[3] = recvData[2];
        outBuffer[4] = recvData[3];
        outBuffer[5] = recvData[4];
        outBuffer[6] = recvData[5];
        outBuffer[7] = recvData[6];
        outBuffer[8] = recvData[7];
        
        //LEDs de los modulos
        outBuffer[9] = (byte) (controller == null?0:1);
        outBuffer[10] = (byte) (video == null?0:1);
        outBuffer[11] = (byte) (audioIN == null?0:1);
        outBuffer[12] = (byte) (audioOUT == null?0:1);
    }
   
    
    private static byte not(byte b){
        return (byte)(b==1?0:1);
    }

}
