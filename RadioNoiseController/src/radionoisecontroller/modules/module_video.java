package radionoisecontroller.modules;

import radionoisecontroller.conn.TCPclient;
import static radionoisecontroller.global.*;
import radionoisecontroller.graphics.WindowManager;
import static java.lang.Thread.interrupted;
import java.nio.ByteBuffer;
import radionoisecontroller.Controller;

public class module_video extends module{

    private TCPclient cliente;
    private WindowManager WM;
    private byte[] recvBuffer_size, recvBuffer_img;
    
    public module_video(byte[] videoBuffer, WindowManager WM){
        recvBuffer_size = new byte[4];
        cliente = new TCPclient();
        recvBuffer_img = videoBuffer;
        this.WM = WM;
    }
    
    @Override
    public void run() {
        WM.setVideoChanged(1);
        state = 1;
        int tryes = CONNECTION_RETRYS;
        while(tryes-- > 0 && !cliente.check() && !interrupted())
            cliente.connect(SERVER_IP, VIDEO_PORT, 5000);
        
        if(cliente.check())
            state = 2;
        
        int size;
        while(!interrupted() && cliente.check()){
            if(!cliente.recive(recvBuffer_size, 2000))
                break;
            
            size = ByteBuffer.wrap(recvBuffer_size).getInt();
            //System.out.println("PESA: "+size);
            
            if(!cliente.recive(recvBuffer_img, 0, size, 2000))
                break;
            
            WM.setVideoChanged(2);
        }
        
        WM.setVideoChanged(3);
        state = 0;
        cliente.disconnect();
        Controller.reportDie(getClass());
    }
}
