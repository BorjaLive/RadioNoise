package radionoisecontroller.graphics.shapes;

import static radionoisecontroller.global.*;
import static org.lwjgl.opengl.GL11.GL_POLYGON;

public final class Potenciometer extends Shape{
    
    int width, height;
    
    public Potenciometer(int x, int y, int width, int height, int[] color) {
        super(SHAPE_SQUARE, GL_POLYGON, color, width/(float)WINDOW_WIDTH, height/(float)WINDOW_HEIGHT, x, y);
        
        this.width = width;
        this.height = height;
        setPower((byte)64);
    }
    
    public void setPower(byte power){
        setScale(width/(float)WINDOW_WIDTH, (height*(((float)((power & 0xFF)))/255))/(float)WINDOW_HEIGHT);
    }
    
}
