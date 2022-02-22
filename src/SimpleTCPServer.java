import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Scanner;

public class SimpleTCPServer implements Runnable
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
        Scanner scan = new Scanner(System.in);
        String input = "";
        while(input.compareTo("EXIT") != 0)
        {
            System.out.print("send data: ");
            input = scan.nextLine();
            if(input.compareTo("EXIT") == 0)
            {
                simpleTCPServer.shutdownServer();
                break;
            }
            simpleTCPServer.sendToAllClient(input);
        }
    }


    int port = 277000;
    ServerSocket serverSocket;
    Thread mainThead;
    LinkedList<ClientHandler> clientList;
    int numbeOfConnection = 1;
    public boolean encrypt = true;
    public boolean running;

    public SimpleTCPServer()
    {
        clientList = new LinkedList<ClientHandler>();
    }

    public SimpleTCPServer(int port)
    {
        clientList = new LinkedList<ClientHandler>();
        this.port = port;
    }

    public SimpleTCPServer(int port, int numberOfConnection)
    {
        this.port = port;
        clientList = new LinkedList<ClientHandler>();
        this.numbeOfConnection = numberOfConnection;
    }

    @Override
    public void run() 
    {
        try
        {
            this.running = true;
            while(!mainThead.isInterrupted() && running)
            {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, this);
                clientList.add(clientHandler);

                if(clientList.size() > numbeOfConnection)
                {
                    clientHandler.sendToClient("Sorry server have already reach MAX connection, socket will now close");
                    clientHandler.closeConnection();
                    clientList.removeLast();
                }
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
            mainThead.interrupt();
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
            mainThead = new Thread(this);
            serverSocket = new ServerSocket(port);
            System.out.println(String.format("server start at --> ip:%s | port:%s", serverSocket.getInetAddress(), serverSocket.getLocalPort()));
            mainThead.start();
        } 
        catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
        }
    }
}

class ClientHandler extends Thread
{
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private SimpleTCPServer simpleTCPServer;
    private boolean running;

    public ClientHandler(Socket clientSocket, SimpleTCPServer simpleTCPServer)
    {
        try 
        {
            this.clientSocket = clientSocket;
            System.out.println(String.format("A new Client connected --> Address:%s | Port:%s", clientSocket.getInetAddress(), clientSocket.getPort()));
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            this.simpleTCPServer = simpleTCPServer;
            this.start();
        } 
        catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() 
    {
        try
        {
            this.running = true;
            while(!this.isInterrupted() && clientSocket.isConnected() && reader.ready() && simpleTCPServer.running && this.running)
            {
                String receiveString = reader.readLine();
                if(receiveString == null)
                    break;
                handleReceiveFromClient(receiveString);
            }
            closeConnection();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            // closeConnection();
        }
    }

    private void handleReceiveFromClient(String receiveString)
    {
        String decryptString = SimpleTCPServer.encryptDecrypt(receiveString);
        
        // if(decryptString.contains("Begin"))
        // {

        // }

        System.out.println(String.format("Receive from client (ip:%s, port:%s): \n(raw):%s \n(decrypt):%s", clientSocket.getInetAddress(), clientSocket.getPort(), receiveString, decryptString));
        sendToClient(receiveString);
    }

    public void sendToClient(String data)
    {
        writer.println(data);
    }

    public void closeConnection()
    {
        try 
        {
            this.running = false;
            this.interrupt();
            reader.close();
            writer.close();
            clientSocket.close();
            simpleTCPServer.clientList.remove(this);
            System.out.println(String.format("Disconnect from client (ip:%s, port:%s)", clientSocket.getInetAddress(), clientSocket.getPort()));
        } 
        catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}
