package radionoisecontroller;

import static java.lang.Thread.sleep;
import static radionoisecontroller.global.*;
import radionoisecontroller.graphics.MultiWindow;
import radionoisecontroller.modules.module_wifi;

public class RadioNoiseController {

    public static void main(String[] args) throws InterruptedException {
        startConstants();
        Controller.initiate();
        if(!(ARDUINO_OVERRIDE || Controller.arduinoTest())){
            System.err.println("No se pudo conectar con Arduino.");
        }else{
            System.out.println("Conexion con Arduino correcta.");
            if(CUSTOM_AUDIO_DEVICE){
                Controller.setAudioInterface();
                while(CUSTOM_AUDIO_DEVICE) sleep(CTRL_DELAY);
            }
            MultiWindow windows = new MultiWindow(2);
            windows.start();
            
            while(!windows.loaded()){
                sleep(CTRL_DELAY);
            }
            Controller.setWindowManager(windows.getWindowManager());
            
            while(Controller.should_continue()){
                Controller.act();
                sleep(CTRL_DELAY);
            }
            Controller.stop();
            System.out.println("Salir");
            windows.interrupt();
        }
    }
    
}
