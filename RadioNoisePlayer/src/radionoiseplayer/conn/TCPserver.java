package radionoiseplayer.conn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPserver {
    private ServerSocket listener;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    
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
    public boolean recive(byte [] data, int timeout){
        return recive(data, 0, data.length, timeout);
    }
    public boolean recive(byte[] data, int pos, int size, int timeout){
        if(input == null) return false;
        try {
            socket.setSoTimeout(timeout);
            int readen = input.read(data, pos, size);
            if(readen != -1){
                return false;
            }else if(readen != size){
                recive(data, pos+readen, size-readen, timeout);
            }
        } catch (IOException ex) {
            //Logger.getLogger(TCPclient.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("CLIENTE TCP: No se pudo recivir");
            return false;
        }
        return true;
    }
    
    public boolean check(){
        return socket != null && !socket.isClosed() && socket.isConnected();
    }
    
}
