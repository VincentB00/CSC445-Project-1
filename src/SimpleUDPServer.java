import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SimpleUDPServer implements Runnable
{
    private boolean startCounting;
    long nano = -1;
    long mill = -1;
    private int numberOfMessage;
    private int currentNumberOfMessage;
    public static void main(String[] args) throws IOException 
    {
        SimpleUDPServer simpleUDPServer = new SimpleUDPServer(1111);
        simpleUDPServer.start();
    }


    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[10];
    Thread mainThread;
    public int port;
    BufferedWriter writer;
    int testNumber;

    public SimpleUDPServer(int port) throws IOException 
    {
        this.port = port;

        writer = new BufferedWriter(new FileWriter("Result/Task4.csv"));
        writer.write("Test Number,Time need to send(nano second),number of package,packet byte\n");
        writer.close();
    }

    public void start()
    {
        try 
        {
            
            socket = new DatagramSocket(port);
            mainThread = new Thread(this);
            mainThread.start();
        } 
        catch (Exception ex) 
        {
            ex.printStackTrace();
            running = false;
        }
    }

    public void stop()
    {
        running = false;
    }

    public void run() 
    {
        running = true;

        while (running) 
        {
            try
            {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                String recieveString = new String(packet.getData(), 0, packet.getLength());

                // System.out.printf("Received: %s\n", received);

                if(recieveString.compareTo("EXIT") == 0)
                {
                    running = false;
                    continue;
                }
                
                if(recieveString.contains("BUF: ") && recieveString.indexOf("BUF: ") < 4)
                {
                    // System.out.println(recieveString);
                    String buffer = recieveString.substring(recieveString.indexOf("BUF: ") + "BUF: ".length(), recieveString.indexOf("#")).trim();
                    // System.out.println("buffer " + buffer);
                    buf = new byte[Integer.parseInt(buffer)];
                    continue; 
                }

                if(recieveString.contains("TN: ") && recieveString.indexOf("TN: ") < 10)
                {
                    String tn = recieveString.substring(recieveString.indexOf("TN: ") + "TN: ".length(), recieveString.indexOf("#")).trim();
                    testNumber = Integer.parseInt(tn);
                    continue; 
                }

                //BEGIN, NUMBEROFMESSAGE
                if(recieveString.contains("BEGIN") && recieveString.indexOf("BEGIN") < "BEGIN".length() + 1 && !startCounting)
                {
                    // System.out.println(recieveString);
                    nano = -1;
                    mill = -1;
                    String time = recieveString.substring(recieveString.indexOf(",") + 1).trim();
                    // System.out.println(time);
                    numberOfMessage = Integer.parseInt(time);
                    currentNumberOfMessage = 0;
                    startCounting = true;
                    continue;
                }

                // System.out.println(getStringByteLength(recieveString));

                if(startCounting)
                {
                    // System.out.println(currentNumberOfMessage);
                    // System.out.println(getStringByteLength(recieveString));

                    if(nano < 0|| mill < 0)
                    {
                        nano = System.nanoTime();
                        mill = System.currentTimeMillis();
                    }
                    
                    currentNumberOfMessage++;

                    if(currentNumberOfMessage == numberOfMessage)
                    {
                        long currentMill = System.currentTimeMillis() - mill;
                        long currentNano = System.nanoTime() - nano;
                        String currentSecond = String.format("%s", (double)currentMill / 1000);

                        // out.println(String.format("Number of message: %s, message Byte: %s, Total nano second: %s, total second: %s", currentNumberOfMessage, getStringByteLength(recieveString), currentNano, currentSecond));
                        
                        String result = String.format("Number of message: %s, message Byte: %s, Total nano second: %s, total second: %s", currentNumberOfMessage, getStringByteLength(recieveString), currentNano, currentSecond);

                        byte[] reBuf = result.getBytes();
                        DatagramPacket resultPacket = new DatagramPacket(reBuf, reBuf.length, address, port);

                        writer = new BufferedWriter(new FileWriter("Result/Task4.csv", true));
                        saveResult(writer, testNumber, result, numberOfMessage, getStringByteLength(recieveString));
                        writer.close();

                        System.out.println(result);

                        socket.send(resultPacket);

                        startCounting = false;

                        buf = new byte[10];
                    }
                    // else
                    //     socket.send(packet);
                }
                else
                    socket.send(packet);

                

                

                
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            
        }
        socket.close();
    }

    private static void saveResult(BufferedWriter writer, int testNumber, String result, int numberOfMessage, int packetByte) throws IOException
    {
        String lines[] = result.split(",");
        String sendedTime = null;
        for (String line : lines) {
            if(line.contains("Total nano second:"))
            {
                sendedTime = line.substring(line.indexOf(" Total nano second: ") + " Total nano second: ".length() + 1);
                break;
            }
        }

        writer.append(String.format("%s,%s,%s,%s\n", testNumber, sendedTime, numberOfMessage, packetByte));
    }

    public int getStringByteLength(String inputString)
    {
        Charset charset = StandardCharsets.US_ASCII;

        byte[] byteArrray = charset.encode(inputString).array();

        // System.out.println("byte length: " + byteArrray.length);

        // for (byte b : byteArrray) 
        // {
        //     System.out.println(b);    
        // }

        return byteArrray.length;
    }
}
