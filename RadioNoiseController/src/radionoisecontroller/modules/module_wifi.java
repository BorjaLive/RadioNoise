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
        int consecutiveFails = 0;
        state = 1;
        if(System.getProperty("os.name").contains("Windows")){
            //Seguro que Windows (o quizas ReactOS)
            if(!(executeWait("netsh wlan disconnect interface=\""+WLAN_INTERFACE_WIN+"\"") &&
                    executeWait("netsh wlan connect ssid=RadioNoise interface=\""+WLAN_INTERFACE_WIN+"\" name="+WLAN_PROFILE_WIN))){
                state = 0;
                return;
            }
            
            int calidad;
            while(!interrupted() && consecutiveFails < WLAN_SCANTIMEOUT){
                String data = executeGet("netsh wlan show interfaces");
                if(data.equals("EXIT")) break;
                
                if(data == null || data.isEmpty() || data.contains("No disponible"))
                    calidad = -1;
                else{
                    //Venga ese parser
                    System.out.println(data);
                    data = data.substring(data.indexOf(WLAN_INTERFACE_WIN));
                    if(data.contains("Nombre"))
                        data = data.substring(0, data.indexOf("Nombre"));
                    if(data.contains("desconectado"))
                        calidad = -1;
                    else{
                        //System.out.println("ESTO ES LO QUE TENGO: "+data+ " de "+WLAN_INTERFACE_WIN);
                        try{
                            data = data.substring(data.indexOf("Se�al"));
                            data = data.substring(0,data.indexOf("Perfil"));
                            data = data.replace(" ","").replace("Se�al", "").replace(":", "").replace("%", "");
                            calidad = Integer.parseInt(data);
                        }catch(NumberFormatException e){
                            calidad = -1;
                        }
                    }
                }
                
                if(calidad == -1){
                    consecutiveFails++;
                    state = 1;
                }else{
                    consecutiveFails = 0;
                    state = 2;
                }
                Controller.wlan_quality = calidad;
                if(calidad == -1)
                    Controller.wlan_signal = 0;
                else
                    Controller.wlan_signal = (int) ((calidad*(float)(7.0/10.0))-110);
                try {sleep(WLAN_SCANDELAY);} catch (InterruptedException ex) {break;}
            }
        }else{
            //Seguramente Linux
            if(!(executeWait("sudo ifconfig "+WLAN_INTERFACE_LINUX+" up") &&
                    executeWait("sudo ifconfig "+WLAN_INTERFACE_LINUX+" up") &&
                    executeWait("sudo iw dev "+WLAN_INTERFACE_LINUX+" connect RadioNoise"))){
                state = 0;
                return;
            }
            
            int signal;
            while(!interrupted() && consecutiveFails < WLAN_SCANTIMEOUT){
                String data = executeGet("iw dev "+WLAN_INTERFACE_LINUX+" station dump | grep \"signal:\"");
                if(data.equals("EXIT")) break;
                
                if(data == null || data.isEmpty()){
                    signal = 0;
                }else{
                    //Venga ese parser
                    try{
                        data = data.substring(data.indexOf('-'));
                        data = data.substring(0, data.indexOf(' '));
                        signal = Integer.parseInt(data);
                    }catch(NumberFormatException e){
                        signal = -1;
                    }
                }
                
                if(signal == 0){
                    consecutiveFails++;
                    state = 1;
                }else{
                    consecutiveFails = 0;
                    state = 2;
                }
                Controller.wlan_signal = signal;
                if(signal == 0)
                    Controller.wlan_quality = -1;
                else{
                    Controller.wlan_quality = (int)((signal+110)*(float)(10.0/7.0));
                    if(Controller.wlan_quality > 100) Controller.wlan_quality = 100;
                }
                //System.out.println("Signal: "+Controller.wlan_signal+"    Calidad: "+Controller.wlan_quality);
                try {sleep(WLAN_SCANDELAY);} catch (InterruptedException ex) {break;}
            }
            
            executeWait("sudo iw dev "+WLAN_INTERFACE_LINUX+" disconnect");
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
        }catch (InterruptedException ie){
            return "EXIT";
        } catch (IOException ex) {
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
