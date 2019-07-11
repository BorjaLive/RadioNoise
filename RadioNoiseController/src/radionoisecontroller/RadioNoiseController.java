package radionoisecontroller;

import static java.lang.Thread.sleep;
import static radionoisecontroller.global.*;
import radionoisecontroller.graphics.MultiWindow;

public class RadioNoiseController {

    public static void main(String[] args) throws InterruptedException {
        if(!(Controller.initiate() && Controller.arduinoTest())){
            System.err.println("No se pudo conectar con Arduino.");
        }else{
            System.out.println("Conexion con Arduino correcta.");
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
            System.out.println("Salir");
            windows.interrupt();
        }
    }
    
}
