package radionoisecontroller.modules;

import radionoisecontroller.conn.TCPclient;
import static radionoisecontroller.global.*;
import radionoisecontroller.graphics.WindowManager;
import static java.lang.Thread.interrupted;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import radionoisecontroller.Controller;
import radionoisecontroller.conn.UDPagent;

public class module_video extends module{

    private static boolean firstTime = true;
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
        
        if(firstTime){
            firstTime = false;
            try {
                sleep(2000);//Para darle tiempo al servidor a iniciar la camara
            } catch (Exception e) {
            }
        }
        
        cliente.connect(SERVER_IP, VIDEO_PORT, CONNECTION_RETRYS, CONNECTION_WAIT_TIME);
        
        byte[] tmpBuffer = new byte[1];
        tmpBuffer[0] = 0;
        cliente.send(tmpBuffer);
        
        if(cliente.check())
            state = 2;
        
        int size;
        while(!interrupted() && cliente.check()){
            if(!cliente.recive(recvBuffer_size))
                break;
            
            size = ByteBuffer.wrap(recvBuffer_size).getInt();
            //System.out.println("PESA: "+size);
            
            if(!cliente.recive(recvBuffer_img, 0, size, 100))
                break;
            
            WM.setVideoChanged(2);
        }
        
        WM.setVideoChanged(3);
        state = 0;
        cliente.disconnect();
        Controller.reportDie(getClass());
    }
}
