package radionoiseplayer.conn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPclient {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    
    private final int MAXNULLRECV = 100;
    
    public boolean connect(String ip, int port, int timeout, int waitTime){
        disconnect();
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            
            socket.setKeepAlive(false);
            
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (IOException ex) {
            //Logger.getLogger(TCPclient.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("CLIENTE TCP: No se pudo conectar");
            timeout--;
            try{Thread.sleep(waitTime);} catch (InterruptedException e) {}
            if(timeout != 0) return connect(ip, port, timeout, waitTime);
        }
        return false;
    }
    public void disconnect(){
        if(socket == null) return;
        try {
            socket.close();
        } catch (IOException ex) {
            //Logger.getLogger(TCPclient.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("CLIENTE TCP: No se pudo desconectar");
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
                System.err.println("CLIENTE TCP: No se pudo recivir, limite de intentos sobrepasado");
                return false;
            }
        } catch (IOException ex) {
            Logger.getLogger(TCPclient.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("CLIENTE TCP: No se pudo recivir");
            return false;
        }
    }
    
    public boolean check(){
        return socket != null && !socket.isClosed() && socket.isConnected();
    }
    
}
