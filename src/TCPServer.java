import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;


public class TCPServer 
{
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
    public static void main(String[] args) 
    {
        SimpleTCPServer simpleTCPServer = new SimpleTCPServer(1111);
        simpleTCPServer.startServer();
        // Scanner scan = new Scanner(System.in);
        // String input = "";
        // while(input.compareTo("EXIT") != 0)
        // {
        //     System.out.print("send data: ");
        //     input = scan.nextLine();
        //     if(input.compareTo("EXIT") == 0)
        //     {
        //         simpleTCPServer.shutdownServer();
        //         break;
        //     }
        //     simpleTCPServer.sendToAllClient(input);
        // }
    }


    int port = 277000;
    ServerSocket serverSocket;
    LinkedList<ClientHandler> clientList;
    int numbeOfConnection = 1;
    public boolean encrypt = true;
    public boolean running;
    private boolean beginTimer;
    private int numberOfMessage;
    private int currentNumberOfMessage;

    public TCPServer()
    {
        clientList = new LinkedList<ClientHandler>();
    }

    public TCPServer(int port)
    {
        clientList = new LinkedList<ClientHandler>();
        this.port = port;
    }

    public TCPServer(int port, int numberOfConnection)
    {
        this.port = port;
        clientList = new LinkedList<ClientHandler>();
        this.numbeOfConnection = numberOfConnection;
    }

    public void sendToClient(String data, int clientNumber)
    {
        if(clientNumber > clientList.size())
            return;

        if(encrypt)
            data = encryptDecrypt(data);

        clientList.get(clientNumber).sendToClient(data);
    }

    public void sendToAllClient(String data)
    {
        if(clientList.isEmpty())
            return;

        if(encrypt)
            data = encryptDecrypt(data);

        for (ClientHandler clientHandler : clientList) 
        {
            clientHandler.sendToClient(data);
        }
    }

    public void closeAllConnection()
    {
        for (ClientHandler clientHandler : clientList) 
        {
            clientHandler.closeConnection();
        }

        clientList.clear();
    }

    public void shutdownServer()
    {
        try 
        {
            this.running = false;
            closeAllConnection();
            serverSocket.close();
        } 
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void startServer()
    {
        try 
        {
            serverSocket = new ServerSocket(port);
            System.out.println(String.format("server start at --> ip:%s | port:%s", serverSocket.getInetAddress(), serverSocket.getLocalPort()));

            Socket clientSocket = serverSocket.accept();

            System.out.println(String.format("A new Client connected --> Address:%s | Port:%s", clientSocket.getInetAddress(), clientSocket.getPort()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

            this.running = true;
            while(running)
            {
                String receiveString = reader.readLine();
                String decryptString = SimpleTCPServer.encryptDecrypt(receiveString);
        
                // if(decryptString.contains("Begin"))
                // {

                // }

                System.out.println(String.format("Receive from client (ip:%s, port:%s): \n(raw):%s \n(decrypt):%s", clientSocket.getInetAddress(), clientSocket.getPort(), receiveString, decryptString));
                writer.println(receiveString);
            }
        } 
        catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
        }
    }
}
