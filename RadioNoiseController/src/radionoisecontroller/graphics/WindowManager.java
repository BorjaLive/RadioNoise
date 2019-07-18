package radionoisecontroller.graphics;

import radionoisecontroller.graphics.shapes.*;
import static radionoisecontroller.global.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import radionoisecontroller.Controller;

public class WindowManager {
    private final MultiWindow windows;
    
    private Potenciometer test;
    private Image video;
    private byte[] videoBuffer;
    private int videoChanged;
    
    public WindowManager(MultiWindow windows){
        this.windows = windows;
        
        test = new Potenciometer(0, WINDOW_HEIGHT, WINDOW_WIDTH/4, -WINDOW_HEIGHT, COLOR_PURPLE);
        video = new Image(StreamImage, 80, 0, 640, 480);
        videoBuffer = new byte[IMAGE_BUFFER_SIZE];
        videoChanged = 0;
    }
    
    public void act(byte[] cutState, byte[] pastState, byte[] recive){
        //test.setPower((byte)(cutState[1]==1?255:128));
        test.setPower(Controller.wlan_quality);
        
        switch (videoChanged) {
            case 1:
                try {
                    windows.queeload(0, new FileInputStream(new File(RESOURCES+"connecting.png")), video.getTexture());
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(WindowManager.class.getName()).log(Level.SEVERE, null, ex);
                }   videoChanged = 0;
            break;
            case 2:
                windows.queeload(0, new ByteArrayInputStream(videoBuffer), video.getTexture());
                videoChanged = 0;
            break;
            case 3:
                try {
                    windows.queeload(0, new FileInputStream(new File(RESOURCES+"offline.png")), video.getTexture());
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(WindowManager.class.getName()).log(Level.SEVERE, null, ex);
                }   videoChanged = 0;
            break;
        }
    }
    
    public void setVideoChanged(int v){
        videoChanged = v;
    }
    
    public byte[] getVideoBuffer(){
        return videoBuffer;
    }
    
    public void draw(int window){
        switch(window){
            case 0:
                test.draw();
                video.draw();
                break;
            case 1:
                break;
        }
    }
}
