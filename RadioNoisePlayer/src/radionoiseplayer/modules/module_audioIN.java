package radionoiseplayer.modules;

import radionoiseplayer.conn.TCPclient;
import static radionoiseplayer.global.*;
import static java.lang.Thread.interrupted;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class module_audioIN extends module{
    private TCPclient cliente;
    private byte[] recvBuffer_size, recvBuffer_audio;
    
    public module_audioIN(){
        cliente = new TCPclient();
        recvBuffer_size = new byte[4];
        recvBuffer_audio = new byte[AUDIO_BUFFER_SIZE];
    }
    
    @Override
    public void run() {
            try {
                SourceDataLine speakers;
                
                AudioFormat format = new AudioFormat(44100.0f, 16, 1, true, true);
                DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
                speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                speakers.open(format);
                speakers.start();
                
                state = 1;
                int tryes = CONNECTION_RETRYS;
                while(tryes-- > 0 && !cliente.check() && !interrupted())
                    cliente.connect(SERVER_IP, AUDIOIN_PORT, 100);
                
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
    }
}
