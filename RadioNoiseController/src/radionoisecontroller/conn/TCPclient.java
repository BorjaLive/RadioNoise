package radionoisecontroller.conn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPclient {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    
    public boolean connect(String ip, int port, int timeout){
        disconnect();
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            
            socket.setKeepAlive(false);
            socket.setSoTimeout(timeout);
            
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (IOException ex) {
            //Logger.getLogger(TCPclient.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("CLIENTE TCP: No se pudo conectar");
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
