import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class SimpleTCPClient extends TCPClient implements Runnable
{
    public static void main(String[] args) 
    {
        // SimpleTCPClient simpleTCPClient = new SimpleTCPClient("localhost", 1111);
        // Scanner scan = new Scanner(System.in);
        // String input = "";
        // try
        // {
        //     simpleTCPClient.connect();
        //     while(input.compareTo("EXIT") != 0)
        //     {
        //         System.out.print("send data: ");
        //         input = scan.nextLine();
        //         if(input.compareTo("EXIT") == 0)
        //         {
        //             simpleTCPClient.close();
        //             break;
        //         }
        //         simpleTCPClient.SendToServer(input);
        //     }
        // }
        // catch(Exception ex)
        // {
        //     ex.printStackTrace();
        // }
    }

    private Thread mainThread;
    public boolean startThreadOnConnect = true;
    @Override
    public void run() 
    {
        try
        {
            while(!mainThread.isInterrupted())
            {
                handleReceiveFromServer(RecieveFromServer());
            }
        }
        catch(SocketException ex)
        {
            
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void handleReceiveFromServer(String receiveString)
    {
        System.out.println(String.format("Receive from server: \n(raw):%s \n(decrypt):%s", receiveString, encryptDecrypt(receiveString)));
    }

    public SimpleTCPClient()
    {
        super();
    }

    public SimpleTCPClient(String ip, int port)
    {
        super(ip, port);
    }

    @Override
    void connect() throws Exception 
    {
        // TODO Auto-generated method stub
        super.connect();

        mainThread = new Thread(this, "TCP client");

        if(startThreadOnConnect)
            mainThread.start();

        System.out.println(String.format("Connected to: ip:%s, port:%s", super.ip, super.port));
    }

    @Override
    void close() 
    {
        // TODO Auto-generated method stub
        mainThread.interrupt();
        super.close();
    }

    public void killThread()
    {
        try
        {
            mainThread.interrupt();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static String encryptDecrypt(String inputString)
    {
        // Define XOR key
        // Any character value will work
        char xorKey = '.';
 
        // Define String to store encrypted/decrypted String
        String outputString = "";
 
        // calculate length of input string
        int len = inputString.length();
 
        // perform XOR operation of key
        // with every character in string
        for (int i = 0; i < len; i++)
        {
            outputString = outputString +
            Character.toString((char) (inputString.charAt(i) ^ xorKey));
        }
 
        // System.out.println(outputString);
        return outputString;
    }
}

class TCPClient
{
    //String name="";
    String ip = "localhost";
    int port = 1111;
    private Socket socket = null;

    private PrintWriter outToServer;
    private BufferedReader inFromServer;

    TCPClient()
    {
        
    }

    TCPClient(String ip, int port)
    {
        this.ip = ip;
        this.port = port;
    }

    /**
     * this function will send data to server
     * @param data data to be send
     */
    void SendToServer(String data)
    {
        // System.out.println("send data: " + data);
        //send msg to server
        // outToServer.print(data + '\n');
        outToServer.println(data);
        // outToServer.flush();
    }
    
    /**
     * this function will return data recive from server
     * @return data recive from server
     * @throws Exception throw exception when unable to recive data
     */
    String RecieveFromServer() throws Exception
    {
        return inFromServer.readLine();
    }

    String SendAndWaitForServer(String data) throws IOException
    {
        outToServer.print(data + '\n');
        outToServer.flush();
        return inFromServer.readLine();
    }

    /**
     * this function will connect socket to server
     * @throws Exception throw exception when unable to connect to server
     */
    void connect() throws Exception
    {
        if(socket == null || !socket.isConnected() || socket.isClosed())
        {
            socket = new Socket(ip, port);
            outToServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            inFromServer = new BufferedReader(new InputStreamReader (socket.getInputStream()));
        }
        else
            throw new Exception("Server is already connected");
    }
    
    /**
     * this function will close all connection from current server
     * @throws Exception exception throw when server is not connected or there something wrong with establising read data from server
     */
    void close()
    {
        try
        {
            if(socket.isConnected())
            {
                outToServer.close();
                inFromServer.close();
                socket.close();
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
