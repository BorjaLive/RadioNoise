package radionoisecontroller.graphics.shapes;

import static radionoisecontroller.global.*;
import radionoisecontroller.graphics.Texture;
import static org.lwjgl.opengl.GL11.*;

public class Image extends Shape{
    
    private final Texture texture;
    
    public Image(Texture texture, int x, int y, int width, int height) {
        super(SHAPE_TEXTURE, GL_QUADS, COLOR_WHITE, 2f*width/(float)WINDOW_WIDTH, 2f*height/(float)WINDOW_HEIGHT, x, y);
        
        this.texture = texture;
    }
    
    public Texture getTexture(){
        return texture;
    }
    
    
    @Override
    public void draw(){
        texture.bind();
        glBegin(GL_QUADS);
        glColor3f(color[0], color[1], color[2]);
        for(int i = 0; i < vertex.length; i++){
            glTexCoord2f(vertex[i][0], vertex[i][1]);
            glVertex2f(xR + vertex[i][0]*scaleX, yR - vertex[i][1]*scaleY);
        }
        glEnd();
        texture.unbind();
    }
    
    
}
