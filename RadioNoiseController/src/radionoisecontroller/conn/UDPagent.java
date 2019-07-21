package radionoisecontroller.conn;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPagent {
    private DatagramSocket socket;
    private int port;
    
    public boolean iniciate(int port){
        this.port = port;
        try {
            socket = new DatagramSocket(port);
            return true;
        } catch (SocketException ex) {
            //Logger.getLogger(UDPagent.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("AGENTE UDP: No se pudo iniciar");
        }
        return false;
    }
    public void shutdown(){
        if(socket == null) return;
        socket.close();
        socket = null;
    }

    public boolean send(byte[] data, String ip){
        return send(data, data.length, ip);
    }
    public boolean send(byte[] data, int size, String ip){
        try {
            DatagramPacket packet = new DatagramPacket(data, size, InetAddress.getByName(ip), port);
            socket.send(packet);
        } catch (IOException ex) {
            //Logger.getLogger(UDPagent.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("AGENTE UDP: No se pudo enviar");
        }

        return false;
    }
    public boolean recive(byte[] data, int timeout, int timeWait){
        return recive(data, data.length, timeWait, timeWait);
    }
    public boolean recive(byte[] data, int size, int timeout, int timeWait){
        try {
            socket.setSoTimeout(timeout);
            DatagramPacket packet = new DatagramPacket(data, data.length);
            socket.receive(packet);
            return true;
        } catch (IOException ex) {
            //Logger.getLogger(UDPagent.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("AGENTE UDP: No se pudo recivir");
        }
        return false;
    }

    public boolean check(){
        return socket != null;
    }
    
}
