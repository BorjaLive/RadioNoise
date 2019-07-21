package radionoiseplayer.modules;

import radionoiseplayer.conn.TCPclient;
import static radionoiseplayer.global.*;
import static java.lang.Thread.interrupted;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

public class module_audioIN extends module{
    private TCPclient cliente;
    private byte[] recvBuffer_size, recvBuffer_audio;
    private FloatControl volume;
    private byte[] tone;
    private boolean claxon;
    
    public module_audioIN(){
        cliente = new TCPclient();
        recvBuffer_size = new byte[4];
        recvBuffer_audio = new byte[AUDIO_BUFFER_SIZE];
        volume = null;
        tone = new byte[AUDIO_BUFFER_SIZE];
        claxon = false;
        setFrequency(400);
    }
    
    @Override
    public void run() {
        try {
            Mixer mixer = getDeviceMixer(DEVICE_AUDIO_OUT);
            if(mixer == null) return;
            
            SourceDataLine speakers;

            AudioFormat format = new AudioFormat(44100.0f, 16, 1, true, true);
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            speakers.open(format);
            speakers.start();
            
            try {
                volume = (FloatControl)speakers.getControl(FloatControl.Type.VOLUME);
            } catch (Exception e) {
                System.out.println("No se pudo obtener el control del volumen");
            }

            state = 1;
            
            cliente.connect(SERVER_IP, AUDIOIN_PORT, CONNECTION_RETRYS, CONNECTION_WAIT_TIME);

            if(cliente.check())
                state = 2;

            while(!isInterrupted()&& cliente.check()){
                if(!cliente.recive(recvBuffer_audio, 0, AUDIO_BUFFER_SIZE, 100))
                    break;
                
                if(claxon)
                    speakers.write(tone, 0, AUDIO_BUFFER_SIZE);
                else
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
    }
    
    public void setVolume(float v){
        if(volume != null)
            volume.setValue(v);
    }
    
    public void setClaxon(boolean c){
        claxon = c;
    }
    public void setFrequency(int f){
        for (int i = 0; i < AUDIO_BUFFER_SIZE; i++) {
            tone[i] = (byte)(Math.sin(2 * Math.PI * i * f / 44100.0f)*255.0f);
        }
    }
}
