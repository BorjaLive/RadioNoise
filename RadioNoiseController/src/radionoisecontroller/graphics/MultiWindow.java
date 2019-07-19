package radionoisecontroller.graphics;

import static radionoisecontroller.global.*;
import java.io.InputStream;
import java.util.ArrayList;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GLCapabilities;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 *
 * @author Margaret
 */
public class MultiWindow extends Thread{
    private final Window[] windows;
    private boolean loaded;
    
    private ArrayList<queueLoad> queue;
    
    private WindowManager WM;
    
    
    public MultiWindow(int nWindows){
        windows = new Window[nWindows];
        loaded = false;
        
        queue = new ArrayList<>();
    }
    
    @Override
    public void run(){
        GLFWErrorCallback errorfun = GLFWErrorCallback.createPrint();
        glfwSetErrorCallback(errorfun);
        if (!glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW.");
        }
        
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_FALSE);

        int[] monitorX = new int[1];
        int[] monitorY = new int[1];
        
        PointerBuffer monitors = glfwGetMonitors();
        
        for (int i = 0; i < windows.length; i++) {
            if(monitors.capacity() > i)
                glfwGetMonitorPos(monitors.get(i), monitorX, monitorY);

            long handle = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "CTRLcontroller", NULL, NULL);
            if (handle == NULL) {
                throw new IllegalStateException("Failed to create GLFW window");
            }

            Window window = new Window(handle);

            glfwMakeContextCurrent(handle);
            window.capabilities = GL.createCapabilities();
            load(i);

            glClearColor((i & 1), (i >> 1), (i == 1) ? 0.f : 1.f, 0.f);

            glfwShowWindow(handle);
            glfwSetWindowPos(handle, monitorX[0], monitorY[0]);

            windows[i] = window;
        }
        
        
        WM = new WindowManager(this);
        loaded = true;
        
        SyncTimer timer = new SyncTimer(30);
        
        while(!interrupted()){

            while(!queue.isEmpty()){
                Window window = windows[queue.get(0).windowNumber];
                if (window == null) {
                    continue;
                }
                glfwMakeContextCurrent(window.handle);
                GL.setCapabilities(window.capabilities);

                queue.get(0).texture.Actualice(queue.get(0).stream);
                queue.remove(0);
            }
            
            for (int i = 0; i < windows.length; i++) {
                Window window = windows[i];
                if (window == null) {
                    continue;
                }
                glfwMakeContextCurrent(window.handle);
                GL.setCapabilities(window.capabilities);
                
                WM.draw(i);
                
                glfwSwapBuffers(window.handle);
                glfwPollEvents();
                
                glClear(GL_COLOR_BUFFER_BIT);
                

                if (glfwWindowShouldClose(window.handle)) {
                    glfwFreeCallbacks(window.handle);
                    glfwDestroyWindow(window.handle);
                    windows[i] = null;
                }
            }

            try{
                timer.sync();
            }catch(Exception e){
                //System.err.println("SYNC ERROR: \n"+e);
                break;
            }
            
        }
        GL.setCapabilities(null);
        glfwTerminate();
    }
    
    public long getHandle(int i){
        if(windows[i] == null) return -1;
        return windows[i].handle;
    }
    
    public WindowManager getWindowManager(){
        return WM;
    }
    
    private void load(int i){
        switch(i){
            case 0:
                StreamImage = new Texture(RESOURCES+"offline.png");
                break;
            case 1:
                break;
        }
    }
    public boolean loaded(){
        return loaded;
    }
    
    public void queeload(int windowNumber, InputStream stream, Texture texture){
        queue.add(new queueLoad(windowNumber, stream, texture));
    }
    
    
    
    
    private static class Window {
        final long handle;

        GLCapabilities capabilities;

        Window(long handle) {
            this.handle = handle;
        }
    }
    private static class queueLoad{
        int windowNumber;
        InputStream stream;
        Texture texture;
        
        queueLoad(int windowNumber, InputStream stream, Texture texture){
            this.windowNumber = windowNumber;
            this.stream = stream;
            this.texture = texture;
        }
    }
}

