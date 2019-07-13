package radionoiseplayer.modules;

import radionoiseplayer.conn.TCPserver;
import static radionoiseplayer.global.*;
import java.util.Arrays;

/*
    SATATE
    0 : Desconectado
    1 : Intentando conectar
    2 : Conectado
*/

public class module_controller extends module{

    private TCPserver servidor;
    
    private byte[] send, recv;
    
    /*
        DATOS ENVIADOS
        2 3
        0 1 : Potencia rudeas
        4 5 6 : Toggle AudioOUT AudioIN Video
        7 : Claxon
        8 9 : Posicion vista X Y
        10 11 : Potencia claxon y audio
        12, ..., 14 : No SE USA
    
        DATOS RECIVIDOS
        2 3
        0 1 : Voltaje bateria ruedas
        4 : No SE USA
        
    */
    
    public module_controller(byte[] sendBuffer, byte[] reciveBuffer){
        servidor = new TCPserver();
        send = sendBuffer;
        recv = reciveBuffer;
    }
    
    @Override
    public void run() {
        servidor.iniciate(CONTROL_PORT);
        System.out.println("Servidor iniciado");
        
        while(!interrupted()){
            //Intentar aceptar la conexion
            servidor.accept(0);
            System.out.println("Peticion aceptada, conexion realizada");
            
            //El servidor comienza reciviendo
            while(servidor.check()){
                if(!servidor.recive(recv))
                    break;
                
                if(!servidor.send(send))
                    break;
                
                try{sleep(SEND_DELAY);}catch(InterruptedException e){break;}
            }
            System.out.println("El servidor se ha desconectado");
            servidor.disconnect();
            try {sleep(SEND_DELAY*10);} catch (InterruptedException ex) {break;}
        }
        
        servidor.shutdown();
        System.out.println("Servidor apagado");
    }
    
}
