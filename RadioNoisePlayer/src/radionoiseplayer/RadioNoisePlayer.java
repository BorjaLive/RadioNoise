package radionoiseplayer;

import static java.lang.Thread.sleep;
import static radionoiseplayer.global.*;

public class RadioNoisePlayer {

    public static void main(String[] args) throws InterruptedException {
        Controller.initiate();
        if(!(ARDUINO_OVERRIDE || Controller.arduinoTest())){
            System.err.println("No se pudo conectar con Arduino.");
        }else{
            System.out.println("Conexion con Arduino correcta.");
            
            while(Controller.should_continue()){
                Controller.act();
                sleep(CTRL_DELAY);
            }
            System.out.println("Salir");
        }
    }
    
}
