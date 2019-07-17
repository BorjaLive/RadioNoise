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
        0, 1 : Voltaje Baterñias 1 y 2
        2 : Voltaje Baterias juntas
		3 : Voltaje Pilas
        4 : No SE USA
    
        DATOS RECIVIDOS
        2 3
        0 1 : Potencia rudeas
        6 7
        4 5 : Sentido rudeas
        8 9 10 : Toggle AudioIN AudioOUT Video
        11 : Claxon
        12 13 : Posicion vista Z Y
        14 15 : Potencia claxon y audio
        16, ..., 17 : No SE USA
        
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
