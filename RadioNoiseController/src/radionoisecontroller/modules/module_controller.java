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
    
    public module_controller(byte[] sendBuffer, byte[] reciveBuffer){
        cliente = new TCPclient();
        send = sendBuffer;
        recv = reciveBuffer;
    }
    
    @Override
    public void run() {
        //Intentar conectarse
        state = 1;
        
        cliente.connect(SERVER_IP, CONTROL_PORT, CONNECTION_RETRYS, CONNECTION_WAIT_TIME);
        
        if(cliente.check())
            state = 2;
        
        System.out.println("Controles conectados");
        
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
