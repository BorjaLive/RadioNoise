package radionoisecontroller.graphics.shapes;

import static radionoisecontroller.global.*;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

public abstract class Shape {
    
    protected final float[][] vertex;
    protected final int mode;
    protected int[] color;
    protected int x, y;
    protected float xR, yR;
    protected float scaleX, scaleY;
    
    public Shape(float[][] vertex, int mode, int[] color, float scaleX, float scaleY, int x, int y){
        this.color = color;
        this.mode = mode;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        
        this.vertex = vertex;
        
        moveAbsolute(x, y);
    }
    
    public final void move(int xx, int yy){
        moveAbsolute(xx+x,yy+y);
    }
    public final void moveAbsolute(int xx, int yy){
        x = xx;
        xR = (float)(x-(WINDOW_WIDTH/2))/(WINDOW_WIDTH/2);
        y = yy;
        yR = -((y-(WINDOW_HEIGHT/2))/(float)(WINDOW_HEIGHT/2));
    }
    
    public final void setColor(int[] c){
        color = c;
    }
    
    public void setScale(float scaleX, float scaleY){
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
    
    public void draw(){
        glBegin(mode);
        GL11.glColor3f(color[0], color[1], color[2]);
        for (float[] verte : vertex)
            glVertex2f(xR + verte[0]*scaleX*2f, yR - verte[1]*scaleY*2f);
        //System.out.println("X: "+xR+ "  Y: "+yR);
        glEnd();
    }
}
