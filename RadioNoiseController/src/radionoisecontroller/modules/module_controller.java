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
    
    private byte[] send, recv, recvTMP;
    
    private int timeout_tries;
    
    /*
        DATOS ENVIADOS
        2 3
        0 1 : Potencia rudeas
        4 5 6 : Toggle AudioIN AudioOUT Video
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
        cliente = new TCPclient();
        send = sendBuffer;
        recv = reciveBuffer;
        timeout_tries = 0;
    }
    
    @Override
    public void run() {
        //Intentar conectarse
        state = 1;
        int tryes = CONNECTION_RETRYS;
        while(tryes-- > 0 && !cliente.check() && !interrupted())
            cliente.connect(SERVER_IP, CONTROL_PORT, 5000);
        
        if(cliente.check())
            state = 2;
        
        while(!interrupted() && cliente.check()){
            if(!cliente.send(send))
                break;
            System.out.println("ENVIO");
            
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
