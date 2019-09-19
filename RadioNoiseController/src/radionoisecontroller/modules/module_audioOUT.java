package radionoisecontroller.modules;

import radionoisecontroller.conn.TCPserver;
import static radionoisecontroller.global.*;
import java.io.ByteArrayOutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import radionoisecontroller.Controller;
import radionoisecontroller.conn.TCPclient;

public class module_audioOUT extends module{
    
    private TCPclient cliente;
    private byte[] sendBuffer, silence;
    
    private boolean enabled;
    
    public module_audioOUT(){
        cliente = new TCPclient();
        enabled = false;
        silence = new byte[AUDIO_BUFFER_SIZE];
        for(int i = 0; i < AUDIO_BUFFER_SIZE; i++)
            silence[i] = 0;
    }
    
    @Override
    public void run() {
        try {
            Mixer mixer = getDeviceMixer(DEVICE_AUDIO_IN);
            if(mixer == null){
                Controller.reportDie(getClass());
                return;
            }
            
            
            TargetDataLine mic;
            AudioFormat format = new AudioFormat(44100.0f, 16, 1, true, true);
            
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            mic = (TargetDataLine) mixer.getLine(info);
            mic.open(format);
            
            sendBuffer = new byte[AUDIO_BUFFER_SIZE];
            mic.start();

            state = 1;
            
            //Intentar conectar
            cliente.connect(SERVER_IP, AUDIOOUT_PORT, CONNECTION_RETRYS, CONNECTION_WAIT_TIME);
            System.out.println("Cliente conectado");
            
            state = 2;

            while(cliente.check() && !interrupted()){
                mic.read(sendBuffer, 0, AUDIO_CHUNK_SIZE);
                
                if(enabled)
                    if(!cliente.send(sendBuffer))
                        break;
            }
            try {sleep(5);} catch (InterruptedException ex) {
                Controller.reportDie(getClass());
                return;
            }
            System.out.println("El cliente se ha desconectado");                
            cliente.disconnect();
            state = 1;
            
            mic.close();
            System.out.println("MUERE EL CLIENTE");
            state = 0;
        } catch (LineUnavailableException ex) {
            //Logger.getLogger(audio_sender.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("ERROR AL OPTENER EL DISPOSITIVO DE GRABACION");
        }
        
        Controller.reportDie(getClass());
    }
    
    public void setEnabled(boolean e){
        enabled = e;
    }
    public boolean getEnabled(){
        return enabled;
    }
}
