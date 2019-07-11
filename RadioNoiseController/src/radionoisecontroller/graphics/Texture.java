package radionoisecontroller.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;

public class Texture {
    
    private int id;
    
    public Texture(InputStream stream){
        BufferedImage bi;
        try {
            bi = ImageIO.read(stream);
            int width = bi.getWidth();
            int height = bi.getHeight();
            
            int[] pixels_raw = new int[width * height];
            bi.getRGB(0, 0, width, height, pixels_raw, 0, width);
            
            ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
            
            for(int i = 0; i < height; i++){
                for(int j = 0; j < width; j++){
                    int pixel = pixels_raw[i*width + j];
                    pixels.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                    pixels.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                    pixels.put((byte) (pixel & 0xFF));               // Blue component
                    pixels.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
                }
            }
            pixels.flip();
            
            id = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, id);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Texture(String file){
        //this(new FileInputStream(new File(file)));
        
        BufferedImage bi;
        try {
            InputStream stream;
            stream = new FileInputStream(new File(file));
            bi = ImageIO.read(stream);
            int width = bi.getWidth();
            int height = bi.getHeight();
            
            int[] pixels_raw = new int[width * height];
            bi.getRGB(0, 0, width, height, pixels_raw, 0, width);
            
            ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
            
            for(int i = 0; i < height; i++){
                for(int j = 0; j < width; j++){
                    int pixel = pixels_raw[i*width + j];
                    pixels.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                    pixels.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                    pixels.put((byte) (pixel & 0xFF));               // Blue component
                    pixels.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
                }
            }
            pixels.flip();
            
            id = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, id);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void Actualice(InputStream stream){
        BufferedImage bi;
        try {
            bi = ImageIO.read(stream);
            if(bi == null) return;
            int width = bi.getWidth();
            int height = bi.getHeight();
            
            int[] pixels_raw = new int[width * height];
            bi.getRGB(0, 0, width, height, pixels_raw, 0, width);
            
            ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
            
            for(int i = 0; i < height; i++){
                for(int j = 0; j < width; j++){
                    int pixel = pixels_raw[i*width + j];
                    pixels.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                    pixels.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                    pixels.put((byte) (pixel & 0xFF));               // Blue component
                    pixels.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
                }
            }
            pixels.flip();
            
            glBindTexture(GL_TEXTURE_2D, id);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void Actualice(String file) throws FileNotFoundException{
        Actualice(new FileInputStream(new File(file)));
    }
    
    public void Delete(){
        glDeleteTextures(id);
    }
    
    public void bind(){
        glBindTexture(GL_TEXTURE_2D, id);
    }
}
