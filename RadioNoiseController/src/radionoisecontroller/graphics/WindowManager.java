package radionoisecontroller.graphics;

import radionoisecontroller.graphics.shapes.*;
import static radionoisecontroller.global.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import static java.lang.Math.abs;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import radionoisecontroller.Controller;

public class WindowManager {
    private final MultiWindow windows;
    
    private Potenciometer volume_self, volume_car, wlan_signal, wlan_calidad, battery_main, battery_servo, power_coarse, power_fine;
    private Image video, icon_audioIN, icon_audioOUT, icon_video, icon_controller, icon_wifi, icon_audioOUT_active, icon_volume_self, icon_volume_car, icon_wlan_signal, icon_wlan_calidad, icon_battery_main, icon_battery_servo;
    private TextLabel text_volume_self, text_volume_car, text_wlan_calidad, text_wlan_signal, text_battery_main, text_battery_servo;
    private byte[] videoBuffer;
    private int videoChanged;
    
    private boolean recording, activating;
    private int recordingBlink;
    
    public WindowManager(MultiWindow windows){
        this.windows = windows;
        
        icon_audioIN = new Image(texture_audioIN, 25, 10, 150, 150);
        icon_audioOUT = new Image(texture_audioOUT, 175, 10, 150, 150);
        icon_audioOUT_active = new Image(texture_audioOUT_active, 175, 10, 150, 150);
        icon_video = new Image(texture_video, 325, 10, 150, 150);
        icon_controller = new Image(texture_controller, 475, 10, 150, 150);
        icon_wifi = new Image(texture_wifi, 625, 10, 150, 150);
        
        //test = new Potenciometer(0, WINDOW_HEIGHT, WINDOW_WIDTH/4, -WINDOW_HEIGHT, COLOR_RED);
        video = new Image(StreamImage, 80, 0, 640, 480);
        videoBuffer = new byte[IMAGE_BUFFER_SIZE];
        videoChanged = 0;
        
        icon_audioIN.setColor(COLOR_GRAY);
        icon_audioOUT.setColor(COLOR_GRAY);
        icon_video.setColor(COLOR_GRAY);
        icon_controller.setColor(COLOR_GRAY);
        icon_wifi.setColor(COLOR_GRAY);
        
        volume_self = new Potenciometer(55, 190, 490, 35, COLOR_BLUE);
        volume_car = new Potenciometer(55, 237, 490, 35, COLOR_BLUE);
        wlan_calidad = new Potenciometer(55, 288, 490, 35, COLOR_GREEN);
        wlan_signal = new Potenciometer(55, 335, 490, 35, COLOR_GREEN);
        battery_main = new Potenciometer(55, 386, 490, 35, COLOR_GREEN);
        battery_servo = new Potenciometer(55, 433, 490, 35, COLOR_GREEN);
        
        power_coarse = new Potenciometer(720, 480, 80, -480, COLOR_BLUE);
        power_fine = new Potenciometer(0, 480, 80, -480, COLOR_BLUE);
        
        icon_volume_self = new Image(texture_audioIN, 5, 185, 45, 45);
        icon_volume_car= new Image(texture_audioOUT, 5, 232, 45, 45);
        icon_wlan_calidad = new Image(texture_wifi, 5, 280, 45, 45);
        icon_wlan_signal = new Image(texture_wifi, 5, 324, 45, 45);
        icon_battery_main = new Image(texture_battery_rechargeable, 5, 378, 45, 45);
        icon_battery_servo = new Image(texture_battery_nonrechargeable, 5, 425, 45, 45);
        
        text_volume_self = new TextLabel(550, 180, 1f, 1f);
        text_volume_car = new TextLabel(550, 217, 1f, 1f);
        text_wlan_calidad = new TextLabel(550, 268, 1f, 1f);
        text_wlan_signal = new TextLabel(550, 315, 1f, 1f);
        text_battery_main = new TextLabel(550, 366, 1f, 1f);
        text_battery_servo = new TextLabel(550, 413, 1f, 1f);
        
        recording = activating = false;
        recordingBlink = 0;
    }
    
    public void setVideoChanged(int v){
        videoChanged = v;
    }
    
    public byte[] getVideoBuffer(){
        return videoBuffer;
    }
    
    public void draw(int window){
        switch(window){
            case 0:
                //test.draw();
                video.draw();
                power_coarse.draw();
                power_fine.draw();
                break;
            case 1:
                icon_audioIN.draw();
                icon_audioOUT.draw();
                if(recording && recordingBlink > TICKS_PER_RECORDING_BLINK/2)
                    icon_audioOUT_active.draw();
                icon_video.draw();
                icon_controller.draw();
                icon_wifi.draw();
                volume_self.draw();
                volume_car.draw();
                wlan_signal.draw();
                wlan_calidad.draw();
                battery_main.draw();
                battery_servo.draw();
                icon_volume_self.draw();
                icon_volume_car.draw();
                icon_wlan_calidad.draw();
                icon_wlan_signal.draw();
                icon_battery_main.draw();
                icon_battery_servo.draw();
                text_volume_self.draw();
                text_volume_car.draw();
                text_wlan_calidad.draw();
                text_wlan_signal.draw();
                text_battery_main.draw();
                text_battery_servo.draw();
                break;
        }
    }
    
    public void act(boolean state_audioIN, boolean state_audioOUT, boolean state_video, boolean state_controller, boolean state_wifi, boolean state_audioOUT_active, int wlan_signal, int wlan_calidad, byte volume_self, byte volume_car, byte battery_main, byte battery_servo, byte coarse, byte fine){
        icon_audioIN.setColor(state_audioIN?COLOR_GREEN:COLOR_GRAY);
        icon_audioOUT.setColor(state_audioOUT?COLOR_GREEN:COLOR_GRAY);
        icon_video.setColor(state_video?COLOR_GREEN:COLOR_GRAY);
        icon_controller.setColor(state_controller?COLOR_GREEN:COLOR_GRAY);
        icon_wifi.setColor(state_wifi?COLOR_GREEN:COLOR_GRAY);
        
        if(state_audioOUT_active){
            if(!activating && !recording){
                activating = true;
                new Thread(){
                    @Override
                    public void run(){
                        try {
                            sleep(TIME_TO_WAIT_BEFORE_SHOWING_THE_MIC_IS_RECORDING);
                        } catch (InterruptedException ex) {
                            //Logger.getLogger(WindowManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        showRecording();
                    }
                }.start();
            }
        }else this.recording = false;
        //Blink del icono de grabacion
        if(recording){
            if(recordingBlink > 0)
                recordingBlink--;
            else
                recordingBlink = TICKS_PER_RECORDING_BLINK;
        }
        
        //Los potenciometros
        power_coarse.setPowerH(coarse);
        power_fine.setPowerH((int) (byte2float(coarse)*(abs((int)fine)/128.0f)*100.0f));
        
        this.volume_self.setPowerW(volume_self);
        this.volume_car.setPowerW(volume_car);
        this.wlan_calidad.setPowerW(wlan_calidad);
        if(wlan_signal == 0)
            this.wlan_signal.setPowerW(0);
        else
            this.wlan_signal.setPowerW((int) (100+(wlan_signal*(10.0f/11.0f))));
        this.battery_main.setPowerW(battery_main);
        this.battery_servo.setPowerW(battery_servo);
        
        this.volume_car.setPowerW(volume_car);
        this.volume_self.setPowerW(volume_self);
        this.wlan_calidad.setPowerW(wlan_calidad);
        this.wlan_signal.setPowerW((int) (100+((wlan_signal)*(10.0f/11.0f))));
        this.battery_main.setPowerW(battery_main);
        this.battery_servo.setPowerW(battery_servo);
        
        if(wlan_calidad < 30)       this.wlan_calidad.setColor(COLOR_RED);
        else if(wlan_calidad < 50)  this.wlan_calidad.setColor(COLOR_ORANGE);
        else if(wlan_calidad < 70)  this.wlan_calidad.setColor(COLOR_YELLOW);
        else                        this.wlan_calidad.setColor(COLOR_GREEN);
        
        if(wlan_signal < -90)       this.wlan_signal.setColor(COLOR_RED);
        else if(wlan_signal < -67)  this.wlan_signal.setColor(COLOR_ORANGE);
        else if(wlan_signal < -40)  this.wlan_signal.setColor(COLOR_YELLOW);
        else                        this.wlan_signal.setColor(COLOR_GREEN);
        
        int battery_main_p = byte2Percentaje(battery_main);
        if(battery_main_p < 30)       this.battery_main.setColor(COLOR_RED);
        else if(battery_main_p < 50)  this.battery_main.setColor(COLOR_ORANGE);
        else if(battery_main_p < 70)  this.battery_main.setColor(COLOR_YELLOW);
        else                        this.battery_main.setColor(COLOR_GREEN);
        
        int battery_servo_p = byte2Percentaje(battery_servo);
        if(battery_servo_p < 30)      this.battery_servo.setColor(COLOR_RED);
        else if(battery_servo_p < 50) this.battery_servo.setColor(COLOR_ORANGE);
        else if(battery_servo_p < 70) this.battery_servo.setColor(COLOR_YELLOW);
        else                        this.battery_servo.setColor(COLOR_GREEN);
        
        text_volume_self.setPercentaje(volume_self);
        text_volume_car.setPercentaje(volume_car);
        text_wlan_calidad.setPercentaje(wlan_calidad);
        text_wlan_signal.setText(wlan_signal+"d");
        text_battery_main.setPercentaje(battery_main);
        text_battery_servo.setPercentaje(battery_servo);
        
        switch (videoChanged) {
            case 1:
                try {
                    windows.queeload(0, new FileInputStream(new File(RESOURCES+"connecting.png")), video.getTexture());
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(WindowManager.class.getName()).log(Level.SEVERE, null, ex);
                }   videoChanged = 0;
            break;
            case 2:
                windows.queeload(0, new ByteArrayInputStream(videoBuffer), video.getTexture());
                videoChanged = 0;
            break;
            case 3:
                try {
                    windows.queeload(0, new FileInputStream(new File(RESOURCES+"offline.png")), video.getTexture());
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(WindowManager.class.getName()).log(Level.SEVERE, null, ex);
                }   videoChanged = 0;
            break;
        }
    }
    
    public void showRecording(){
        System.out.println("Se activa el show recording");
        recording = true;
        activating = false;
        recordingBlink = TICKS_PER_RECORDING_BLINK;
    }
}
