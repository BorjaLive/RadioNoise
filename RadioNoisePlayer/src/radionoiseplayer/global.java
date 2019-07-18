package radionoiseplayer;

public class global {
    public static int BAUD_SPEED = 9600;
    static String ARDUINO_PORT = "/dev/ttyACM0";
    //public static final String ARDUINO_PORT = "COM4";
    public static final boolean ARDUINO_OVERRIDE = true;
    public static int BYTES_IN = 5, BYTES_OUT = 10;
    
    public static String SERVER_IP = "localhost";
    public static final int CONTROL_PORT = 4421;
    public static final int VIDEO_PORT = 4422;
    public static final int AUDIOIN_PORT = 4424;
    public static final int AUDIOOUT_PORT = 4423;
    public static int BYTES_SEND = 5, BYTES_RECIVE = 15;
    public static int SEND_DELAY = 10, CTRL_DELAY = 5, RECV_DELAY = 5;
    public static final int IMAGE_BUFFER_SIZE = 60*1024, AUDIO_BUFFER_SIZE = 1024, AUDIO_CHUNK_SIZE = 1024;
    public static final int VIDEO_FRAMERATE = 10;
    
    public static final int CONNECTION_RETRYS = 5;
    public static final int TICKS_PER_BLINK = 5;
    
    public static final String DEVICE_AUDIO_IN = "CAMERA [plughw:2,0]";
    public static final String DEVICE_AUDIO_OUT = "Audio [plughw:1,0]";
    //public static final String DEVICE_AUDIO_IN = "";
    //public static final String DEVICE_AUDIO_OUT = "";
    
}
