package radionoiseplayer.modules;

import radionoiseplayer.conn.TCPserver;
import static radionoiseplayer.global.*;
import java.io.ByteArrayOutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class module_audioOUT extends module{
    
    private TCPserver servidor;
    private byte[] sendBuffer;
    
    public module_audioOUT(){
        servidor = new TCPserver();
    }
    
    @Override
    public void run() {
        try {
            Mixer mixer = getDeviceMixer(DEVICE_AUDIO_IN);
            if(mixer == null) return;
            
            servidor.iniciate(AUDIOOUT_PORT);
            System.out.println("Servidor iniciado");
            
            TargetDataLine mic;
            AudioFormat format = new AudioFormat(44100.0f, 16, 1, true, true);
            
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            mic = (TargetDataLine) AudioSystem.getLine(info);
            mic.open(format);
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            sendBuffer = new byte[AUDIO_BUFFER_SIZE];
            mic.start();
            
            while(!isInterrupted()){
                //Intentar aceptar la conexion
                servidor.accept(2000);
                if(!servidor.check())
                    break;
                System.out.println("Peticion aceptada, conexion realizada");
                
                
                while(servidor.check() && !interrupted()){
                    mic.read(sendBuffer, 0, AUDIO_CHUNK_SIZE);

                    if(!servidor.send(sendBuffer))
                        break;
                }
                try {sleep(5);} catch (InterruptedException ex) {break;}
                System.out.println("El servidor se ha desconectado");                
                servidor.disconnect();
            }
            mic.close();
            servidor.shutdown();
            System.out.println("MUERE EL SERVIDOR");
        } catch (LineUnavailableException ex) {
            //Logger.getLogger(audio_sender.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("ERROR AL OPTENER EL DISPOSITIVO DE GRABACION");
        }
    }
}
