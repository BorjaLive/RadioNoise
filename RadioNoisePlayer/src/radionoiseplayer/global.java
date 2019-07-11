package radionoiseplayer;

public class global {
    public static int BAUD_SPEED = 9600;
    //static String PORT_NAME = "/dev/ttyACM0";
    public static String ARDUINO_PORT = "COM4";
    public static int BYTES_IN = 5, BYTES_OUT = 10;
    
    public static String SERVER_IP = "localhost";
    public static final int CONTROL_PORT = 4421;
    public static final int VIDEO_PORT = 4422;
    public static final int AUDIOIN_PORT = 4424;
    public static final int AUDIOOUT_PORT = 4423;
    public static int BYTES_SEND = 5, BYTES_RECIVE = 15;
    public static int SEND_DELAY = 10, CTRL_DELAY = 5;
    public static final int IMAGE_BUFFER_SIZE = 60*1024, AUDIO_BUFFER_SIZE = 1024, AUDIO_CHUNK_SIZE = 1024;
    public static final int VIDEO_FRAMERATE = 10;
    
    public static int CONNECTION_RETRYS = 5;
    public static int TICKS_PER_BLINK = 5;
    
}
