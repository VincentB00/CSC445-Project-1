import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SimpleUDPClient 
{
    private DatagramSocket socket;
    private InetAddress address;
    private byte[] buf;
    int port;

    public SimpleUDPClient(String ip, int port) 
    {
        this.port = port;
        try
        {
            socket = new DatagramSocket();
            address = InetAddress.getByName(ip);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void sendData(String data)
    {
        try
        {
            buf = data.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public String sendDataAndReceive(String msg) 
    {
        try
        {
            buf = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            return received;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return "can't send";
        }
    }

    public void close() 
    {
        socket.close();
    }
}
