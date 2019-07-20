package radionoisecontroller.modules;

import radionoisecontroller.graphics.WindowManager;

public abstract class module extends Thread{
    
    protected int state;
    
    public module(){
        state = 0;
    }
    
    @Override
    public abstract void run();
    
    public int check(){
        return state;
    }
    
}
