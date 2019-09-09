package radionoiseplayer.modules;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import static java.lang.Thread.interrupted;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
//import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import radionoiseplayer.conn.TCPserver;
import static radionoiseplayer.global.*;
import radionoiseplayer.graphics.SyncTimer;

public class module_video extends module{

    private TCPserver servidor;
    private byte[] sendBuffer;
    
    private FrameGrabber grabber;
    private OpenCVFrameConverter.ToIplImage converter;
    
    public module_video(){
        servidor = new TCPserver();
        
        grabber = new OpenCVFrameGrabber(0);
        converter = new OpenCVFrameConverter.ToIplImage();
    }
    
    @Override
    public void run() {
        servidor.iniciate(VIDEO_PORT);
        System.out.println("Servidor iniciado");
        
        IplImage img;
        try {
            grabber.start();
        } catch (FrameGrabber.Exception ex) {
            //Logger.getLogger(module_video.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("NO SE ENCONTRO LA CAMARA");
        }
        
        //Intentar aceptar la conexion
        System.out.println("Ahora acepto conexiones");
        servidor.accept(0);
        System.out.println("Peticion aceptada, conexion realizada");

        byte[] tmpBuffer = new byte[1];
        servidor.recive(tmpBuffer);
        String format;
        System.out.println("He recivido: "+tmpBuffer[0]);
        switch(tmpBuffer[0]){
            case 0:
                format = "jpg";
            break;
            case 1:
                format = "raw";
            break;
            case 2:
                format = "bmp";
            break;
            default:
                format = "jpg";
            break;
        }
        
        SyncTimer timer = new SyncTimer(VIDEO_FRAMERATE);

        Frame frame;
        while(servidor.check() && !interrupted()){
            try {
                frame = grabber.grab();
                img = converter.convert(frame);
                BufferedImage t = toBufferedImage(img);
                if(format.equals("raw")){
                    sendBuffer = ((DataBufferByte) t.getData().getDataBuffer()).getData();
                }else{
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(t, format, baos);
                    baos.flush();
                    sendBuffer = baos.toByteArray();
                }
                
                if(!servidor.send(ByteBuffer.allocate(4).putInt(sendBuffer.length).array()))
                    break;
                System.out.println("PESA: "+sendBuffer.length);
                if(!servidor.send(sendBuffer))
                    break;

                //En este caso los envios se deben reintentar


            } catch (FrameGrabber.Exception ex) {
                //Logger.getLogger(module_video.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                //Logger.getLogger(module_video.class.getName()).log(Level.SEVERE, null, ex);
            }


            try {timer.sync();} catch (Exception ex) {break;}
        }
        try {
            grabber.close();
        } catch (FrameGrabber.Exception ex) {
            //Logger.getLogger(module_video.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("El servidor de video se ha desconectado");
        servidor.disconnect();
        
        servidor.shutdown();
        System.out.println("Servidor apagado");
    }
    
    private static BufferedImage toBufferedImage(IplImage src) {
        OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter paintConverter = new Java2DFrameConverter();
        Frame frame = grabberConverter.convert(src);
        return paintConverter.getBufferedImage(frame,1);
    }

}
