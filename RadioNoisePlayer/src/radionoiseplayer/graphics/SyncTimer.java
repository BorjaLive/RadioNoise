package radionoiseplayer.graphics;

public class SyncTimer {
     
    private double timeThen;
    private int targetFPS;
 
    public SyncTimer(int fps){
        timeThen = System.nanoTime();
        targetFPS = fps;
    }
     
    public void sync() throws Exception {
        double resolution = 1000000000.0D;
        double timeNow =  System.nanoTime();
         
        double gapTo = resolution / targetFPS + timeThen;

        while (gapTo < timeNow) 
            gapTo = resolution / targetFPS + gapTo;
        
        while (gapTo > timeNow) {
            Thread.sleep(1);
            timeNow = System.nanoTime();
        }

        timeThen = gapTo; 
    }
}
