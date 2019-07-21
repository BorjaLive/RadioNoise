package radionoisecontroller;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import radionoisecontroller.graphics.Texture;

public class global {
    public static final int BAUD_SPEED = 9600;
    //static String ARDUINO_PORT = "/dev/ttyACM0";
    public static final String ARDUINO_PORT = "COM4";
    public static final boolean ARDUINO_OVERRIDE = false;
    public static final int BYTES_IN = 35, BYTES_OUT = 22;
    
    //public static final String SERVER_IP = "192.168.1.5";
    public static final String SERVER_IP = "localhost";
    public static final String WLAN_INTERFACE_WIN = "TL-WN727N";
    public static final String WLAN_PROFILE_WIN = "RadioNoise";
    public static final String WLAN_INTERFACE_LINUX = "wlan1";
    public static final int WLAN_SCANDELAY = 250;
    public static final int WLAN_SCANTIMEOUT = 100;
    public static final int CONTROL_PORT = 4421;
    public static final int VIDEO_PORT = 4422;
    public static final int AUDIOIN_PORT = 4423;
    public static final int AUDIOOUT_PORT = 4424;
    public static final int BYTES_SEND = 18, BYTES_RECIVE = 5;
    public static final int SEND_DELAY = 10;
    public static final int CTRL_DELAY = 10;
    public static final float OS_TIMEOUT = 1000f;
    public static final int IMAGE_BUFFER_SIZE = 60*1024, AUDIO_BUFFER_SIZE = 1024, AUDIO_CHUNK_SIZE = 1024;
    
    public static final int CONNECTION_RETRYS = 20, CONNECTION_WAIT_TIME = 100;
    public static final int TICKS_PER_LED_BLINK = 5, TICKS_PER_RECORDING_BLINK = 100;
    public static final int BLINK_DURATION = 10*TICKS_PER_LED_BLINK;
    public static final int STALL_UMBRAL = 2, DIGITAL_HUMBRAL = 63;
    public static final float SENSIBILIDY_CONSTANT = 0.1f;
    public static final int TIME_TO_WAIT_BEFORE_SHOWING_THE_MIC_IS_RECORDING = 1000;
    
    //public static final String DEVICE_AUDIO_IN = "CAMERA [plughw:2,0]";
    //public static final String DEVICE_AUDIO_OUT = "Audio [plughw:1,0]";
    public static boolean CUSTOM_AUDIO_DEVICE = true;
    public static String DEVICE_AUDIO_IN = "Micrófono (Realtek High Definit";
    public static String DEVICE_AUDIO_OUT = "Altavoces (Realtek High Definition Audio)";
    
    public static final int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 480;
    
    
    
    public static final float[][] SHAPE_SQUARE = new float[][] {
        new float[] { 0, 0,},
        new float[] { 0, 1,},
        new float[] { 1, 1,},
        new float[] { 1, 0,},
    };
    public static final float[][] SHAPE_TEXTURE = new float[][] {
        new float[] { 0, 1,},
        new float[] { 1, 1,},
        new float[] { 1, 0,},
        new float[] { 0, 0,},
    };
    
    public static final float[] COLOR_WHITE = new float[]         {255/255f,255/255f,255/255f};
    public static final float[] COLOR_GRAY = new float[]          {150/255f,150/255f,150/255f};
    public static final float[] COLOR_RED = new float[]           {255/255f,  0/255f,  0/255f};
    public static final float[] COLOR_ORANGE = new float[]        {255/255f,150/255f,  0/255f};
    public static final float[] COLOR_YELLOW = new float[]        {255/255f,255/255f,  0/255f};
    public static final float[] COLOR_GREEN = new float[]         {  0/255f,255/255f,  0/255f};
    public static final float[] COLOR_BLUE = new float[]          {  0/255f,  0/255f,255/255f};
    
    public static final String RESOURCES = (RadioNoiseController.class.getResource("graphics/resources").toString()+"\\").substring(6);
    
    //TODO: ponerlo todo en mayusculas
    public static Texture StreamImage, texture_audioOUT, texture_audioIN, texture_video, texture_controller, texture_wifi, texture_audioOUT_active, texture_battery_nonrechargeable, texture_battery_rechargeable,
                            texture_alpha_0, texture_alpha_1, texture_alpha_2, texture_alpha_3, texture_alpha_4, texture_alpha_5, texture_alpha_6, texture_alpha_7, texture_alpha_8, texture_alpha_9, texture_alpha_dBm, texture_alpha_dot, texture_alpha_minus, texture_alpha_percentaje;
    
    public static int LETTER_SPACING = 48;
    public static int LETTER_SHIFT = 32;
    
    
    
    
    
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
