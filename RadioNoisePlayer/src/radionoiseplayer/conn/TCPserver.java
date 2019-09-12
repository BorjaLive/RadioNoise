package radionoiseplayer.conn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPserver {
    private ServerSocket listener;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    
    private final int MAXNULLRECV = 100;
    
    public boolean iniciate(int port){
        disconnect();
        try {
            listener = new ServerSocket(port);
        } catch (IOException ex) {
            //Logger.getLogger(TCPserver.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("SERVIDOR TCP: No se pudo iniciar");
            return false;
        }
        return true;
    }
    public void shutdown(){
        disconnect();
        try {
            listener.close();
        } catch (IOException ex) {
            //Logger.getLogger(TCPserver.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("SERVIDOR TCP: No se pudo cerrar");
        }
        listener = null;
    }
    
    public boolean accept(int timeout){
        if(listener == null) return false;
        disconnect();
        try {
            if(timeout != 0){
                new Thread(){
                    @Override
                    public void run(){
                        try {
                            sleep(timeout);
                            if(!check())
                                listener.close();
                        } catch (InterruptedException | IOException ex) {
                            //Logger.getLogger(TCPserver.class.getName()).log(Level.SEVERE, null, ex);
                            System.err.println("SERVIDOR TCP: Error de timeout");
                        }
                    }
                }.start();
            }
            
            socket = listener.accept();
            
            socket.setKeepAlive(true);
            
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (IOException ex) {
            //Logger.getLogger(TCPserver.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("SERVIDOR TCP: No se pudo aceptar");
        }
        return false;
    }
    public void disconnect(){
        if(socket == null) return;
        try {
            socket.close();
        } catch (IOException ex) {
            //Logger.getLogger(TCPserver.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("SERVIDOR TCP: No se pudo desconectar");
        }
        socket = null;
        input = null;
        output = null;
    }
    
    public boolean send(byte[] data){
        return send(data, 0, data.length);
    }
    public boolean send(byte[] data, int pos, int size){
        if(output == null) return false;
        try {
            //output.writeUTF(new String(data));
            output.write(data, pos, size);
            return true;
        } catch (IOException ex) {
            //Logger.getLogger(TCPserver.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("SERVIDOR TCP: No se pudo enviar");
        }
        return false;
    }
    public boolean recive(byte [] data){
        return recive(data, 0, data.length, MAXNULLRECV);
    }
    public boolean recive(byte[] data, int pos, int size, int nullRest){
        if(input == null) return false;
        try {
            int readen = input.read(data, pos, size);
            if(readen == -1){
                readen = 0;
                nullRest--;
            }
            if(readen == size){
                return true;
            }else if(nullRest != 0){
                    try{Thread.sleep(1);} catch (InterruptedException ex) {}
                    return recive(data, pos+readen, size-readen, nullRest);
            }else{
                System.err.println("SERVIDOR TCP: No se pudo recivir, limite de intentos sobrepasado");
                return false;
            }
        } catch (IOException ex) {
            //Logger.getLogger(TCPclient.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("SERVIDOR TCP: No se pudo recivir");
            return false;
        }
    }
    
    public boolean check(){
        return socket != null && !socket.isClosed() && socket.isConnected();
    }
    
}
