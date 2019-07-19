package radionoisecontroller.modules;

import radionoisecontroller.Controller;
import radionoisecontroller.conn.TCPclient;
import static radionoisecontroller.global.*;

/*
    SATATE
    0 : Desconectado
    1 : Intentando conectar
    2 : Conectado
*/

public class module_controller extends module{

    private TCPclient cliente;
    
    private byte[] send, recv;
    
    /*
        DATOS ENVIADOS
        2 3
        0 1 : Potencia rudeas
        6 7
        4 5 : Sentido rudeas
        8 9 10 : Toggle AudioIN AudioOUT Video
        11 : Claxon
        12 13 : Posicion vista Z Y
        14 15 : Potencia claxon y audio
        16, ..., 17 : No SE USA
    
        DATOS RECIVIDOS
		0, 1 : Voltaje Baterñias 1 y 2
        2 : Voltaje Baterias juntas
		3 : Voltaje Pilas
        4 : No SE USA
        
    */
    
    public module_controller(byte[] sendBuffer, byte[] reciveBuffer){
        cliente = new TCPclient();
        send = sendBuffer;
        recv = reciveBuffer;
    }
    
    @Override
    public void run() {
        //Intentar conectarse
        state = 1;
        
        cliente.connect(SERVER_IP, CONTROL_PORT, CONNECTION_RETRYS);
        
        if(cliente.check())
            state = 2;
        
        while(!interrupted() && cliente.check()){
            if(!cliente.send(send))
                break;
            
            
            if(!cliente.recive(recv))
                break;
                
            //System.out.println("LO QUE RECIVO: "+Arrays.toString(recvTMP));
            try{sleep(SEND_DELAY);}catch(InterruptedException e){break;}
        }
        
        
        
        state = 0;
        cliente.disconnect();
        System.out.println("Me desconecto");
        Controller.reportDie(getClass());
        //System.out.println(Arrays.toString(send));
    }
    
}
