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

public class module_audioOUT extends module{
    
    private TCPserver servidor;
    private byte[] sendBuffer, silence;
    
    private boolean enabled;
    
    public module_audioOUT(){
        servidor = new TCPserver();
        enabled = false;
        silence = new byte[AUDIO_BUFFER_SIZE];
        for(int i = 0; i < AUDIO_BUFFER_SIZE; i++)
            silence[i] = 0;
    }
    
    @Override
    public void run() {
        try {
            Mixer mixer = null;
            Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
            if(mixerInfos == null){
                Controller.reportDie(getClass());
                return;
            }else{
                int i = 0;
                while(mixer == null && i <  mixerInfos.length){
                    if(mixerInfos[i].getName().equals(DEVICE_AUDIO_IN))
                        mixer = AudioSystem.getMixer(mixerInfos[i]);
                    i++;
                }
            }
            if(mixer == null){
                Controller.reportDie(getClass());
                return;
            }
            
            
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
                    if(enabled){
                        mic.read(sendBuffer, 0, AUDIO_CHUNK_SIZE);
                    
                        if(!servidor.send(sendBuffer))
                            break;
                    }else{
                        if(!servidor.send(silence))
                            break;
                    }
                }
                try {sleep(5);} catch (InterruptedException ex) {
                    Controller.reportDie(getClass());
                    return;
                }
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
        
        Controller.reportDie(getClass());
    }
    
    public void setEnabled(boolean e){
        enabled = e;
    }
    public boolean getEnabled(){
        return enabled;
    }
}
