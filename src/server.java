import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class server 
{
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean startCounting;
    long nano = -1;
    long mill = -1;
    private int numberOfMessage;
    private int currentNumberOfMessage;


    public void start(int port) throws IOException 
    {
        
        serverSocket = new ServerSocket(port);

        while(true)
        {
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while(true)
            {
                String recieveString = in.readLine();
                // out.println(recieveString);

                if(recieveString == null)
                    break;

                if(startCounting)
                {
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

                        out.println(String.format("Number of message: %s, message Byte: %s, Total nano second: %s, total second: %s", currentNumberOfMessage, getStringByteLength(recieveString), currentNano, currentSecond));

                        startCounting = false;
                    }
                }
                else
                    out.println(recieveString);

                if(recieveString.compareTo("EXIT") == 0)
                    return;

                //BEGIN, NUMBEROFMESSAGE
                if(recieveString.contains("BEGIN") && recieveString.indexOf("BEGIN") < "BEGIN".length() + 1 && !startCounting)
                {
                    nano = -1;
                    mill = -1;
                    numberOfMessage = Integer.parseInt(recieveString.substring(recieveString.indexOf(",") + 1));
                    currentNumberOfMessage = 0;
                    startCounting = true;

                }
            }
        }
    }

    public void stop() throws IOException 
    {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
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
    public static void main(String[] args) throws IOException {
        server server = new server();
        server.start(1111);
    }
}
