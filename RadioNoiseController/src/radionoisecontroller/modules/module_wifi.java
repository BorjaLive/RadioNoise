package radionoisecontroller.modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import radionoisecontroller.Controller;
import static radionoisecontroller.global.*;

public class module_wifi extends module{
    
    public module_wifi(){
    }
    
    @Override
    public void run() {
        int signal, consecutiveFails = 0;
        if(System.getProperty("os.name").contains("Windows")){
            //Seguro que Windows (o quizas ReactOS)
            state = 1;
            executeWait("netsh wlan disconnect interface=\""+WLAN_INTERFACE_WIN+"\"");
            executeWait("netsh wlan connect ssid=RadioNoise interface=\""+WLAN_INTERFACE_WIN+"\" name="+WLAN_PROFILE_WIN);
            
            while(!interrupted() && consecutiveFails < WLAN_SCANTIMEOUT){
                String data = executeGet("netsh wlan show interfaces");
                
                if(data == null)
                    signal = -1;
                else{
                    //Venga ese parser
                    data = data.substring(data.indexOf(WLAN_INTERFACE_WIN));
                    if(data.contains("Nombre"))
                        data = data.substring(0, data.indexOf("Nombre"));
                    if(data.contains("desconectado"))
                        signal = -1;
                    else{
                        data = data.substring(data.indexOf("Se�al"));
                        data = data.substring(0,data.indexOf("Perfil"));
                        data = data.replace(" ","").replace("Se�al", "").replace(":", "").replace("%", "");
                        System.out.println("ESTO ES LO QUE TENGO: "+data+ " de "+WLAN_INTERFACE_WIN);
                        try{
                            signal = Integer.parseInt(data);
                        }catch(NumberFormatException e){
                            signal = -1;
                        }
                    }
                }
                System.out.println("signal: "+signal);
                if(signal == -1){
                    consecutiveFails++;
                    state = 1;
                }else{
                    consecutiveFails = 0;
                    state = 2;
                }
                Controller.wlan_strength = signal;
                try {sleep(WLAN_SCANDELAY);} catch (InterruptedException ex) {break;}
            }
        }else{
            //Seguramente Linux
        }
        
        
        
        
        
        System.out.println("ME las piro");
        
        state = 0;
        Controller.reportDie(getClass());
    }
    
    private String executeGet(String command){
        Runtime rt = Runtime.getRuntime();
        Process proc;
        String returned = "";
        long time = System.currentTimeMillis();
        
        try {
            proc = rt.exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String s;
            while ((s = stdInput.readLine()) != null){
                returned += s;
                Thread.sleep(10);
                if((System.currentTimeMillis()-time)/OS_TIMEOUT > 1){
                    proc.destroy();
                    return null;
                }
            }
            proc.destroy();
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            return null;
        }
        
        return returned;
    }
    private boolean executeWait(String command){
        Runtime rt = Runtime.getRuntime();
        Process proc;
        long time = System.currentTimeMillis();
        
        try {
            proc = rt.exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String s;
            while ((s = stdInput.readLine()) != null){
                Thread.sleep(10);
                if((System.currentTimeMillis()-time)/OS_TIMEOUT > 1){
                    proc.destroy();
                    return false;
                }
            }
            proc.destroy();
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    private boolean execute(String command){
        Runtime rt = Runtime.getRuntime();
        
        try {
            rt.exec(command);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        
        return true;
    }
}
