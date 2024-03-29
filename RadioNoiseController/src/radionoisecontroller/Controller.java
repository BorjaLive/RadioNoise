package radionoisecontroller;

import com.fazecast.jSerialComm.SerialPort;
import static java.awt.Component.CENTER_ALIGNMENT;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import radionoisecontroller.modules.*;
import static radionoisecontroller.global.*;
import radionoisecontroller.graphics.WindowManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

public class Controller {
    private static byte[] curState, pasState;
    private static module_controller controller;
    private static module_video video;
    private static module_audioIN audioIN;
    private static module_audioOUT audioOUT;
    private static module_wifi wifi;
    
    private static SerialPort port;
    public static int wlan_quality, wlan_signal;
    private static float[] voltajes;
    private static byte[][] voltajesBuffer;
    private static int voltajeBufferCounter;
    private static float[] batteryValues, voltajeBufferMean;
    
    private static byte[] outBuffer;
    private static byte[] recvData, sendData;
    
    private static WindowManager WM;
    
    //Variables de conduccion
    private static boolean tanque, activeW1, activeW2, activeW3, activeW4;
    private static int[] blink;
    private static int servoZ, servoY;
    
    public static void initiate(){
        outBuffer = new byte[BYTES_OUT];
        curState = new byte[BYTES_IN];
        pasState = new byte[BYTES_IN];
        sendData = new byte[BYTES_SEND];
        recvData = new byte[BYTES_RECIVE];
        voltajes = new float[5];
        voltajesBuffer = new byte[5][VOLTAJE_FILTER];
        voltajeBufferMean = new float[5];
        voltajeBufferCounter = 0;
        
        WM = null;
        
        wlan_quality = 0;
        wlan_signal = 0;
        
        //Variables de conduccion
        blink = new int[BYTES_OUT];
        for(int i = 0; i < BYTES_OUT; i++){
            blink[i] = -1;
        }
        servoZ = servoY = 90;
        tanque = false;
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
        
        port.writeBytes(outBuffer, outBuffer.length);
        
        while(port.bytesAvailable() != recv.length){
            try {
                Thread.sleep(CTRL_DELAY);
            } catch (InterruptedException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        port.readBytes(recv, recv.length);
        
        //System.out.println("RECIVIDO: "+Arrays.toString(recv));
        
        boolean last = false, good = true;
        int i = 0;
        while(i < recv.length && good){
            good = recv[i] == (byte)(last?0:1);
            last = !last;
            i++;
        }
        
        demo();
        return good;
    }
    
    public static void act(){
        System.arraycopy(curState, 0, pasState, 0, curState.length);
        data_exchange(outBuffer, curState);
        
        //Suavizar potenciometros
        for(int i = 22; i<= 27; i++){
            curState[i] = (byte) ((byte2int(curState[i])+byte2int(pasState[i]))/2);
            if(byte2int(curState[i]) < POTENCIOMETER_LOW_MARGIN)
                curState[i] = 0;
            else if(byte2int(curState[i]) > POTENCIOMETER_HIGH_MARGIN)
                curState[i] = (byte)255;
        }
        //Filtro para voltajes TODO: Hacerlo funcionar
        /*
        for(int i = 0; i < 5; i++){
            voltajeBufferMean[i] -= byte2int(voltajesBuffer[i][voltajeBufferCounter])/(float)VOLTAJE_FILTER;
            voltajeBufferMean[i] += byte2int(recvData[i])/(float)VOLTAJE_FILTER;
            voltajesBuffer[i][voltajeBufferCounter] = recvData[i];
            
            voltajeBufferCounter++;
            if(voltajeBufferCounter == VOLTAJE_FILTER)
                voltajeBufferCounter = 0;
            
            if(i == 2){
                System.out.println(voltajeBufferMean[i]+"    "+byte2int(recvData[i]));
            }
            recvData[i] = (byte)voltajeBufferMean[i];
        }
        */
        
        //Iniciar e interrumpir modulos
        if(curState[7] == 0 && controller != null){
            controller.interrupt();
        }
        if(curState[7] == 1 && pasState[7] == 0 && controller == null){
            controller = new module_controller(sendData, recvData);
            controller.start();
        }
        
        if(curState[6] == 0 && video != null){
            sendData[10] = 0;
            video.interrupt();
        }
        if(curState[6] == 1 && pasState[6] == 0 && video == null){
            sendData[10] = 1;
            video = new module_video(WM.getVideoBuffer(), WM);
            video.start();
        }
        
        if(curState[4] == 0 && audioIN != null){
            sendData[9] = 0;
            audioIN.interrupt();
        }
        if(curState[4] == 1 && pasState[4] == 0 && audioIN == null){
            sendData[9] = 1;
            audioIN = new module_audioIN();
            audioIN.start();
        };
        
        if(curState[5] == 0 && audioOUT != null){
            sendData[8] = 0;
            audioOUT.interrupt();
        }
        if(curState[5] == 1 && pasState[5] == 0 && audioOUT == null){
            sendData[8] = 1;
            audioOUT = new module_audioOUT();
            audioOUT.start();
        }
        if(curState[8] == 0 && wifi != null){
            wifi.interrupt();
        }
        if(curState[8] == 1 && pasState[8] == 0 && wifi == null){
            wifi = new module_wifi();
            wifi.start();
        }
        
        if(curState[13] == 0 && pasState[13] == 1 && audioOUT != null){
            audioOUT.setEnabled(false);
        }
        if(curState[13] == 1 && pasState[13] == 0 && audioOUT != null){
            audioOUT.setEnabled(true);
        }
        //RECIVIDO DESDE ARDUINO    4, 5, 6, 7, 8 : Toggle AudioIN AudioOUT Video Controller Wifi
        //ENVIO AL PLAYER           4 5 6 : Toggle AudioOUT AudioIN Video
        sendData[4] = curState[4];
        sendData[5] = curState[5];
        sendData[6] = curState[3];
        
        
        //Actualizaciones de controles
        drive();
        
        //Actualizar voltajes
        voltajes[0] = byte2float(recvData[0])*VOLTAJE_DIVIDER_CONSTANT_1;
        voltajes[1] = byte2float(recvData[1])*VOLTAJE_DIVIDER_CONSTANT_2;
        voltajes[2] = byte2float(recvData[2])*VOLTAJE_DIVIDER_CONSTANT_3;
        voltajes[3] = byte2float(recvData[3])*VOLTAJE_DIVIDER_CONSTANT_4;
        voltajes[4] = byte2float(recvData[4])*VOLTAJE_DIVIDER_CONSTANT_5;
        //System.out.println(voltajes[0]+"  "+voltajes[1]+"  "+voltajes[2]+"  "+voltajes[3]+"  "+voltajes[4]+"    "+VOLTAJE_DIVIDER_CONSTANT_5);
        //Actualizar pantallas
        WM.act(audioIN!=null&&audioIN.check()==2, audioOUT!=null&&audioOUT.check()==2, video!=null&&video.check()==2, controller!=null&&controller.check()==2, wifi!=null&&wifi.check()==2, audioOUT!=null&&audioOUT.getEnabled(), wlan_signal, wlan_quality, curState[26], curState[27], voltajes, curState[24], curState[18], curState[10]==1);
        
        //System.out.println(Arrays.toString(curState));
    }
    
    public static boolean should_continue(){
        return curState[28] == 0;
    }
    
    public static void stop(){
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
        
        for(int i = 1; i <= 20; i++){
            outBuffer[i] = (byte)0;
        }
        outBuffer[0] = (byte)3;
        port.writeBytes(outBuffer, outBuffer.length);
        
        try {
            sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        port.closePort();
    }
    
    public static void setWindowManager(WindowManager WM){
        Controller.WM = WM;
        batteryValues = WM.getBatteryValues();
    }
    
    
    private static void data_exchange(byte[] outBuffer, byte[] inBuffer){
        outBuffer[0] = (byte)3;
        port.writeBytes(outBuffer, outBuffer.length);
        
        while(port.bytesAvailable() != inBuffer.length){
            try {
                Thread.sleep(CTRL_DELAY);
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
        //Activacion de ruedas
        if(batteryValues[0] <= 0){
            if(activeW1){
                activeW1 = false;
                blink[5] = 0;
            }
            if(pasState[0] == 0  && curState[0] != 0){
                blink[5] = 0;
            }
        }else activeW1 = curState[0] != 0;
        if(batteryValues[1] <= 0){
            if(activeW2){
                activeW2 = false;
                blink[6] = 0;
            }
            if(pasState[1] == 0  && curState[1] != 0){
                blink[6] = 0;
            }
        }else activeW2 = curState[1] != 0;
        if(batteryValues[2] <= 0){
            if(activeW3){
                activeW3 = false;
                blink[7] = 0;
            }
            if(pasState[2] == 0  && curState[2] != 0){
                blink[7] = 0;
            }
        }else activeW3 = curState[2] != 0;
        if(batteryValues[3] <= 0){
            if(activeW4){
                activeW4 = false;
                blink[8] = 0;
            }
            if(pasState[3] == 0  && curState[3] != 0){
                blink[8] = 0;
            }
        }else activeW4 = curState[3] != 0;
        
        //Modo tanque
        if((pasState[11] == 0 && curState[11] != 0) || tanque){ //Se ha activado el modo tanque o ya está activado
            if(activeW1 && activeW2 && activeW1 && activeW1)    // Activar si todas las ruedas estan bien
                tanque = true;
            else{   //Si no, desactivar y parpadear en rojo las desactivadas
                if(!activeW1) blink[5] = 0;
                if(!activeW2) blink[6] = 0;
                if(!activeW3) blink[7] = 0;
                if(!activeW4) blink[8] = 0;
                tanque = false;
            }
        }
        
        //Potencia de las ruedas
        float powerW1, powerW2, powerW3, powerW4; //Potencia coarse
        powerW1 = powerW2 = powerW3 = powerW4 = 0.0f;
        if(activeW1)
            powerW1 = byte2float(curState[24]);
        if(activeW2)
            powerW2 = byte2float(curState[24]);
        if(activeW3)
            powerW3 = byte2float(curState[24]);
        if(activeW4)
            powerW4 = byte2float(curState[24]);
        if(tanque){ //Conduccion modo tanque, ocho direcciones
            boolean powerD, powerI, dirD, dirI;
            int x, y;
            //Un pequeño filtro
            if((curState[18] >= 0 && curState[18] > 127 - STALL_UMBRAL) || (curState[18] < 0 && curState[18] < STALL_UMBRAL - 128))
                y = 0;
            else{
                y = (int)curState[18];
                if(y < 0){
                    y += 127;
                }else{
                    y -= 128;
                }
            }
            if((curState[19] >= 0 && curState[19] > 127 - STALL_UMBRAL*2) || (curState[19] < 0 && curState[19] < STALL_UMBRAL*2 - 128))
                x = 0;
            else{
                x = (int)curState[19];
                if(x < 0){
                    x += 127;
                }else{
                    x -= 128;
                }
            }
            
            if(x == 0 && y == 0){ //Quieto parado
                powerI = false;
                powerD = false;
                dirI = true;
                dirD = true;
            }else if(y > -2*x && y > 2*x){  //Delante
                powerI = true;
                powerD = true;
                dirI = true;
                dirD = true;
            }else if(y < -2*x && y < 2*x){  //Atras
                powerI = true;
                powerD = true;
                dirI = false;
                dirD = false;
            }else if(x > -2*y && x > 2*y){  //Derecha
                powerI = true;
                powerD = true;
                dirI = true;
                dirD = false;
            }else if(x < -2*y && x < 2*y){  //Izquierda
                powerI = true;
                powerD = true;
                dirI = false;
                dirD = true;
            }else if(y < 2*x && x < 2*y){   //Delante Derecha
                powerI = true;
                powerD = false;
                dirI = true;
                dirD = true;
            }else if(y > 2*x && x > 2*y){   //Atras Izquierda
                powerI = false;
                powerD = true;
                dirI = true;
                dirD = false;
            }else if(y < -2*x && x > -2*y){ //Delante Izquierda
                powerI = false;
                powerD = true;
                dirI = true;
                dirD = true;
            }else if(y > -2*x && x < -2*y){ //Atras Derecha
                powerI = true;
                powerD = false;
                dirI = false;
                dirD = true;
            }else{  //Parado
                powerD = false;
                powerI = false;
                dirD = true;
                dirI = true;
            }
            
            powerW1 *= powerI?1.0f:0.0f; //Aplicar
            powerW2 *= powerD?1.0f:0.0f;
            powerW3 *= powerI?1.0f:0.0f;
            powerW4 *= powerD?1.0f:0.0f;
            //System.out.println("W1: "+(powerW1*(dirI?1:-1))+" W2: "+(powerW2*(dirD?1:-1))+" W3: "+(powerW3*(dirI?1:-1))+" W4: "+(powerW4*(dirD?1:-1))+"     X: "+curState[18]+" Y: "+curState[19]+"    x: "+x+" y: "+y);
            sendData[4] = (byte)(dirI?1:0);
            sendData[5] = (byte)(dirD?1:0);
            sendData[6] = (byte)(dirI?1:0);
            sendData[7] = (byte)(dirD?1:0);
            
        }else{  //Conduccion con PAN de direccion
            float fineTune;
            if(curState[18] >= 0 && curState[18] < 127 - STALL_UMBRAL){// Hacia atras
                fineTune = 1.0f+(((float)-curState[18])/120.0f);   //Potencia fine tune
                sendData[4] = 0;    //Todo hacia atras
                sendData[5] = 0;
                sendData[6] = 0;
                sendData[7] = 0;
            }else if(curState[18] < 0 && curState[18] > STALL_UMBRAL - 128){// Hacia delante
                fineTune = 1.0f+(((float)curState[18])/120.0f);    //Potencia fine tune
                sendData[4] = 1;    //Todo hacia delante
                sendData[5] = 1;
                sendData[6] = 1;
                sendData[7] = 1;
            }else{ //Quieto
                fineTune = 0.0f;
                //El sentido da igual
            }
            if(fineTune > 1.0f) fineTune = 1.0f;
            powerW1 *= fineTune;    //Aplicar el fineTune
            powerW2 *= fineTune;
            powerW3 *= fineTune;
            powerW4 *= fineTune;
            
            float steerTuneI;
            float steerTuneD;
            if(curState[19] >= 0 && curState[19] < 127 - STALL_UMBRAL*2){// Hacia la izquierda
                steerTuneI = ((float)curState[19])/128.0f;
                steerTuneD = 1.0f;
            }else if(curState[19] < 0 && curState[19] > STALL_UMBRAL*2 - 128){// Hacia la derecha
                steerTuneI = 1.0f;
                steerTuneD = ((float)-curState[19])/127.0f;
            }else{ //Quieto
                steerTuneI = 1.0f;
                steerTuneD = 1.0f;
                //El sentido da igual
            }
            powerW1 *= steerTuneI;    //Aplicar el steerTune
            powerW2 *= steerTuneD;
            powerW3 *= steerTuneI;
            powerW4 *= steerTuneD;
        }
        
        if(curState[9] != 0){ //Si el PAN de traccion esta activado
            if(curState[22] < 0){ //Atenuar la delantera
                powerW3 *= ((float)-curState[22])/128.0f;
                powerW4 *= ((float)-curState[22])/128.0f;
            }else{  //Atenuar la trasera
                powerW1 *= ((float)curState[22])/127.0f;
                powerW2 *= ((float)curState[22])/127.0f;
            }
        }
        //System.out.println(powerW1+" "+powerW2+" "+powerW3+" "+powerW4);
        sendData[0] = float2byte(powerW1);  //Asignar las potencias
        sendData[1] = float2byte(powerW2);
        sendData[2] = float2byte(powerW3);
        sendData[3] = float2byte(powerW4);
        
        if(curState[12] != 0){  //Si el turbo esta activado, puede olvidarse de todo lo demas
            if(curState[14] != 0){  //Turvo hacia delante
                sendData[0] = (byte)255;
                sendData[1] = (byte)255;
                sendData[2] = (byte)255;
                sendData[3] = (byte)255;
                sendData[4] = (byte)1;
                sendData[5] = (byte)1;
                sendData[6] = (byte)1;
                sendData[7] = (byte)1;
            }else if(curState[15] != 0){    //Turvo hacia detras
                sendData[0] = (byte)255;
                sendData[1] = (byte)255;
                sendData[2] = (byte)255;
                sendData[3] = (byte)255;
                sendData[4] = (byte)0;
                sendData[5] = (byte)0;
                sendData[6] = (byte)0;
                sendData[7] = (byte)0;
            }
        }
        
        //Servomotores de la camara
        if(curState[20] >= 0 && curState[20] < 127 - STALL_UMBRAL*2)
            servoY -= byte2float(curState[25])*SENSIBILIDY_CONSTANT;
        else if(curState[20] < 0 && curState[20] > STALL_UMBRAL*2 - 128)
            servoY += byte2float(curState[25])*SENSIBILIDY_CONSTANT;
        
        if(curState[21] >= 0 && curState[21] < 127 - STALL_UMBRAL*2)
            servoZ += byte2float(curState[25])*SENSIBILIDY_CONSTANT;
        else if(curState[21] < 0 && curState[21] > STALL_UMBRAL*2 - 128)
            servoZ -= byte2float(curState[25])*SENSIBILIDY_CONSTANT;
        
        if(servoZ > 180) servoZ = 180; else if(servoZ < 0) servoZ = 0;
        if(servoY > 180) servoY = 180; else if(servoY < 0) servoY = 0;
        
        if(curState[17] != 0){
            servoZ = 90;
            servoY = 90;
        }
        
        //System.out.println("ServoZ: "+servoZ+"  ServoY: "+servoY);
        
        sendData[12] = (byte)servoZ;
        sendData[13] = (byte)servoY;
        
        
        //LEDs
        outBuffer[1] = (byte)(activeW1?1:0);
        outBuffer[2] = (byte)(activeW2?1:0);
        outBuffer[3] = (byte)(activeW3?1:0);
        outBuffer[4] = (byte)(activeW4?1:0);
        outBuffer[5] = (byte)(activeW1?0:1);
        outBuffer[6] = (byte)(activeW2?0:1);
        outBuffer[7] = (byte)(activeW3?0:1);
        outBuffer[8] = (byte)(activeW4?0:1);
        
        if(tanque){
            outBuffer[19] = 1;
            outBuffer[20] = 0;
        }else{
            outBuffer[19] = 0;
            outBuffer[20] = 1;
        }
        //Modulos. Si es null o el estado es 0 se enciente en rojo, si el estado es 1 parpadea el rojo, si el estado es 2 se queda fijo el verde
        if(wifi == null || wifi.check() == 0){
            outBuffer[9] = 0;
            outBuffer[14] = 1;
        }else if(wifi.check() == 1){
            if(blink[9] == -1) blink[9] = 0;
            outBuffer[14] = 0;
        }else if(wifi.check() == 2){ //No deberia hacer falta pero, por si casaso
            outBuffer[9] = 1;
            outBuffer[14] = 0;
        }
        if(controller == null || controller.check() == 0){
            outBuffer[10] = 0;
            outBuffer[15] = 1;
        }else if(controller.check() == 1){
            if(blink[10] == -1) blink[10] = 0;
            outBuffer[15] = 0;
        }else if(controller.check() == 2){
            outBuffer[10] = 1;
            outBuffer[15] = 0;
        }
        if(video == null || video.check() == 0){
            outBuffer[11] = 0;
            outBuffer[16] = 1;
        }else if(video.check() == 1){
            if(blink[11] == -1) blink[11] = 0;
            outBuffer[16] = 0;
        }else if(video.check() == 2){
            outBuffer[11] = 1;
            outBuffer[16] = 0;
        }
        if(audioIN == null || audioIN.check() == 0){
            outBuffer[12] = 0;
            outBuffer[17] = 1;
        }else if(audioIN.check() == 1){
            if(blink[12] == -1) blink[12] = 0;
            outBuffer[17] = 0;
        }else if(audioIN.check() == 2){
            outBuffer[12] = 1;
            outBuffer[17] = 0;
        }
        if(audioOUT == null || audioOUT.check() == 0){
            outBuffer[13] = 0;
            outBuffer[18] = 1;
        }else if(audioOUT.check() == 1){
            if(blink[13] == -1) blink[13] = 0;
            outBuffer[18] = 0;
        }else if(audioOUT.check() == 2){
            outBuffer[13] = 1;
            outBuffer[18] = 0;
        }
        
        
        //Mecanismo de blink
        for(int i = 0; i < BYTES_OUT; i++){
            if(blink[i] != -1){
                if(blink[i] % TICKS_PER_LED_BLINK == 0)
                    outBuffer[i] = not(outBuffer[i]);
                if(blink[i] == BLINK_DURATION)
                    blink[i] = -1;
                else blink[i]++;
            }
        }
        
        //Claxon
        sendData[11] = (byte) (curState[16]==0?1:0); //Activo
        sendData[14] = curState[23]; //Frecuencia
        
        //Volumen
        sendData[15] = curState[27]; //El volumen del vehículo
        if(audioIN != null && pasState[26] != curState[26])
            audioIN.setVolume(byte2float(curState[26]));    //Volumen propio
        
    }
    
    public static void setAudioInterface(){
        String[] devices;
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        if(mixerInfos == null){
            CUSTOM_AUDIO_DEVICE = false;
            return;
        }else{
            devices = new String[mixerInfos.length];
            for (int i = 0; i < mixerInfos.length; i++)
                devices[i] = mixerInfos[i].getName();
        }
                
        
        
        JFrame frame = new JFrame("frame");
        JPanel panel =new JPanel(); 
        panel.setLayout(null);
        
        JLabel label1 = new JLabel("Salida", (int) CENTER_ALIGNMENT);
        label1.setLocation(10, 10);
        label1.setSize(280, 20);
        label1.setFont(new Font("Courier", 0, 26));
        JLabel label2 = new JLabel("Entrada", (int) CENTER_ALIGNMENT);
        label2.setLocation(310, 10);
        label2.setSize(280, 20);
        label2.setFont(new Font("Courier", 0, 26));
        
        JList list1 = new JList(devices);
        list1.setLocation(15, 45);
        list1.setSize(270, 250);
        list1.setFont(new Font("Verdana", 0, 18));
        JList list2 = new JList(devices);
        list2.setLocation(315, 45);
        list2.setSize(270, 250);
        list2.setFont(new Font("Verdana", 0, 18));
        
        JButton button = new JButton("Seleccionar");
        button.setLocation(200, 310);
        button.setSize(200, 50);
        
        panel.add(label1);
        panel.add(label2);
        panel.add(list1);
        panel.add(list2);
        panel.add(button);
   
        frame.add(panel); 
        frame.setSize(600,400); 
        
        button.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                DEVICE_AUDIO_OUT = (String)list1.getSelectedValue();
                DEVICE_AUDIO_IN = (String)list2.getSelectedValue();
                CUSTOM_AUDIO_DEVICE = false;
                
                frame.setVisible(false);
                frame.dispose();
            }
         });
        
        frame.setVisible(true);
    }
    
    private static byte not(byte b){
        return (byte)(b==1?0:1);
    }

    public static void demo(){
        outBuffer[0] = (byte)2;
        
        for(int i = 14; i <= 18; i++){
            for(int j = 14; j <= 18; j++)
                outBuffer[j] = (byte) (i>=j?1:0);
            port.writeBytes(outBuffer, BYTES_OUT);
            try {Thread.sleep(100);} catch (Exception e) {}
        }
        for(int i = 18; i >= 14; i--){
            for(int j = 14; j <= 18; j++)
                outBuffer[j] = (byte) (i>j?1:0);
            port.writeBytes(outBuffer, BYTES_OUT);
            try {Thread.sleep(100);} catch (Exception e) {}
        }
    }
}
