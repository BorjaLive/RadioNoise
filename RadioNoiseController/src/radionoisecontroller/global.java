package radionoisecontroller;

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
    public static final int WLAN_SCANDELAY = 250;
    public static final int WLAN_SCANTIMEOUT = 16;
    public static final int CONTROL_PORT = 4421;
    public static final int VIDEO_PORT = 4422;
    public static final int AUDIOIN_PORT = 4423;
    public static final int AUDIOOUT_PORT = 4424;
    public static final int BYTES_SEND = 15, BYTES_RECIVE = 5;
    public static final int SEND_DELAY = 10;
    public static final int CTRL_DELAY = 10;
    public static final float OS_TIMEOUT = 1000f;
    public static final int IMAGE_BUFFER_SIZE = 60*1024, AUDIO_BUFFER_SIZE = 1024, AUDIO_CHUNK_SIZE = 1024;
    
    public static final int CONNECTION_RETRYS = 20;
    public static final int TICKS_PER_BLINK = 5;
    
    //public static final String DEVICE_AUDIO_IN = "CAMERA [plughw:2,0]";
    //public static final String DEVICE_AUDIO_OUT = "Audio [plughw:1,0]";
    public static final String DEVICE_AUDIO_IN = "Micrófono (Realtek High Definit";
    public static final String DEVICE_AUDIO_OUT = "Altavoces (Realtek High Definition Audio)";
    
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
    
    public static final int[] COLOR_AQUA = new int[]{0, 255, 255};
    public static final int[] COLOR_RED = new int[]{255, 0, 0};
    public static final int[] COLOR_PURPLE = new int[]{255, 0, 255};
    public static final int[] COLOR_WHITE = new int[]{255, 255, 255};
    
    //public static final String RESOURCES = "/home/arlin/Escritorio/RadioNoise/RadioNoiseController/src/radionoisecontroller/graphics/resources/";
    public static final String RESOURCES = "C:\\Users\\Margaret\\Desktop\\RadioNoise\\RadioNoiseController\\src\\radionoisecontroller\\graphics\\resources\\";
    
    public static Texture StreamImage;
    
}
