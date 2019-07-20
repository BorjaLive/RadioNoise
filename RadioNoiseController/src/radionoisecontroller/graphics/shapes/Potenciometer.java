package radionoisecontroller.graphics.shapes;

import static radionoisecontroller.global.*;
import static org.lwjgl.opengl.GL11.GL_POLYGON;

public final class Potenciometer extends Shape{
    
    int width, height;
    
    public Potenciometer(int x, int y, int width, int height, float[] color) {
        super(SHAPE_SQUARE, GL_POLYGON, color, width/(float)WINDOW_WIDTH, height/(float)WINDOW_HEIGHT, x, y);
        
        this.width = width;
        this.height = height;
    }
    
    public void setPowerH(byte power){
        setScale(width/(float)WINDOW_WIDTH, (height*(((float)((power & 0xFF)))/255.0f))/(float)WINDOW_HEIGHT);
    }
    public void setPowerH(int power){
        setScale(width/(float)WINDOW_WIDTH, (height*(power/100.0f))/(float)WINDOW_HEIGHT);
    }
    
    public void setPowerW(byte power){
        setScale((width*(((float)((power & 0xFF)))/255.0f))/(float)WINDOW_WIDTH, height/(float)WINDOW_HEIGHT);
    }
    public void setPowerW(int power){
        setScale((width*(power/100.0f))/(float)WINDOW_WIDTH, height/(float)WINDOW_HEIGHT);
    }
    
}
