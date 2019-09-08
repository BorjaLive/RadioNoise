package radionoiseplayer;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

public class global {
    public static int BAUD_SPEED = 9600;
    static String ARDUINO_PORT = "/dev/ttyACM0";
    //public static final String ARDUINO_PORT = "COM4";
    public static final boolean ARDUINO_OVERRIDE = true;
    public static int BYTES_IN = 6, BYTES_OUT = 15;
    
    //public static String SERVER_IP = "192.168.0.5";
    public static String SERVER_IP = "localhost";
    public static final int CONTROL_PORT = 4421;
    public static final int VIDEO_PORT = 4422;
    public static final int AUDIOIN_PORT = 4424;
    public static final int AUDIOOUT_PORT = 4423;
    public static int BYTES_SEND = 6, BYTES_RECIVE = 18;
    public static int SEND_DELAY = 10, CTRL_DELAY = 5, RECV_DELAY = 5;
    public static final int IMAGE_BUFFER_SIZE = 60*1024, AUDIO_BUFFER_SIZE = 1024, AUDIO_CHUNK_SIZE = 1024;
    public static final int VIDEO_FRAMERATE = 10;
    
    public static final int CONNECTION_RETRYS = 20, CONNECTION_WAIT_TIME = 25;
    public static final int TICKS_PER_BLINK = 5;
    
    public static final String DEVICE_AUDIO_IN = "Device [plughw:2,0]";
    public static final String DEVICE_AUDIO_OUT = "Device [plughw:2,0]";
    //public static final String DEVICE_AUDIO_IN = "Micrófono (Realtek High Definit";
    //public static final String DEVICE_AUDIO_OUT = "Altavoces (Realtek High Definition Audio)";
    
    
    
    
    public static byte float2byte(float f){
        return (byte)(f*256.0);
    }
    public static int byte2int(byte b){
        return (int)(b & 0xFF);
    }
    public static float byte2float(byte b){
        return ((float)byte2int(b)/255.0f);
    }
    
    public static Mixer getDeviceMixer(String name){
        Mixer mixer = null;
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        if(mixerInfos == null){
            return null;
        }else{
            int i = 0;
            while(mixer == null && i <  mixerInfos.length){
                if(mixerInfos[i].getName().equals(name))
                    mixer = AudioSystem.getMixer(mixerInfos[i]);
                i++;
            }
        }
        return mixer;
    }
}
