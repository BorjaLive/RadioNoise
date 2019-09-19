package radionoisecontroller.modules;

import radionoisecontroller.conn.TCPclient;
import static radionoisecontroller.global.*;
import static java.lang.Thread.interrupted;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import radionoisecontroller.Controller;

public class module_audioIN extends module{
    private TCPclient cliente;
    private byte[] recvBuffer_size, recvBuffer_audio;
    FloatControl volume;
    private float volume_range, volume_min;
    
    public module_audioIN(){
        cliente = new TCPclient();
        recvBuffer_size = new byte[4];
        recvBuffer_audio = new byte[AUDIO_BUFFER_SIZE];
        volume = null;
    }
    
    @Override
    public void run() {
        try {
            Mixer mixer = getDeviceMixer(DEVICE_AUDIO_OUT);
            if(mixer == null){
                Controller.reportDie(getClass());
                return;
            }
            
            SourceDataLine speakers;

            AudioFormat format = new AudioFormat(44100.0f, 16, 1, true, true);
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) mixer.getLine(dataLineInfo);
            speakers.open(format);
            speakers.start();
            
            try {
                volume = (FloatControl)speakers.getControl(FloatControl.Type.MASTER_GAIN);
                volume_range = volume.getMaximum() - volume.getMinimum();
                volume_min = volume.getMinimum();
            } catch (Exception e) {
                System.out.println("No se pudo obtener el control del volumen");
            }

            state = 1;
            
            cliente.connect(SERVER_IP, AUDIOIN_PORT, CONNECTION_RETRYS, CONNECTION_WAIT_TIME);
            System.out.println("Cliente conectado");

            if(cliente.check())
                state = 2;

            while(!isInterrupted()&& cliente.check()){
                if(!cliente.recive(recvBuffer_audio, 0, AUDIO_BUFFER_SIZE, 100))
                    break;
                speakers.write(recvBuffer_audio, 0, AUDIO_BUFFER_SIZE);
            }
            speakers.drain();
            speakers.close();
            state = 0;
            cliente.disconnect();
            System.out.println("MUERE EL CLIENTE");
        } catch (LineUnavailableException ex) {
            //Logger.getLogger(audio_reciver.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("ERROR AL OPTENER EL DISPOSITIVO DE REPRODUCCION");
        }
        
        Controller.reportDie(getClass());
    }
    
    public void setVolume(float v){
        if(volume != null)
            volume.setValue((volume_range * v) + volume_min);
    }
}
