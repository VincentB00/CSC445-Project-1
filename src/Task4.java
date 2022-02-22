import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Task4 
{
    public static void main(String[] args) throws IOException 
    {
        SimpleUDPClient client = new SimpleUDPClient("localhost", 1111);
        // String package1 = "love me!";
        // String package2 = "that is why we feel comfortable to lift this, in effect tomorrow";
        String package3 = "I think I've said over the last few press conferences that I really believe that we ought to be looking seriously at how to ratchet that back,” Pritzker said of his mask mandates at a news conference Tuesday. “I think we're going to be making announcements";
        String package4 = "Long jokes with a twist are best type of jokes.\n1. My friend once called a few house painters to his house for some work. He wanted them to paint his porch. After a few hours, the house painters came back for the payment as their work was complete. Before leaving they told my friend that they had enjoyed painting his car, but it is not really a Porsche.\n2. There is a skeleton in our neighborhood who always knows that something bad might happen way before it actually happens. He has actually become quite famous and when a TV crew interviewed about the reason behind this ability, the skeleton finally disclosed his secret: he could feel the bad vibes in his bones.\n3. I had visited a cafe one day with my friends. The waiter recommended that we try their special coffee. We agreed and soon the coffee arrived. As we drink the coffee, we realized that it tastes like dirt and mud. Disgusted by the fact, all of us complained immediately. The alarmed waiter rushes over and says, \"Well Sir, it was freshly ground coffee!\"";
        try
        {
            for(int count = 1; count <= 100; count ++)
            {
                client.sendData("TN: " + count + "#");
                client.sendData("BUF: 10#");

                System.out.println("package 1: 1024 X 1024 Byte messages");
                sendPackage(client, package4, 1024);
                // System.out.println("----------------------------------------");
                // System.out.println("package 2: 2048 X 512 Byte messages");
                // sendPackage(client, package3 + package3, 2048);
                // System.out.println("----------------------------------------");
                // System.out.println("package 3: 4096 X 256 Byte messages");
                // sendPackage(client, package3, 4096);
            }

            client.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
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

    public static void sendPackage(SimpleUDPClient client, String p, int time) throws Exception
    {
        client.sendData(String.format("BEGIN,%s", time));
        client.sendData("BUF: " + getStringByteLength(p) + "#");
        for(int count = 1; count <= time; count++)
        {
            // sendPackage(client, encryptDecrypt(p));
            client.sendData(encryptDecrypt(p));
        }
        client.sendData("BUF: 10#");
    }

    public static void sendPackage(SimpleUDPClient client, String p) throws Exception
    {
        client.sendData("BUF: " + getStringByteLength(p) + "#");
        client.sendData(p);
        client.sendData("BUF: 10#");
    }

    public static int getStringByteLength(String inputString)
    {
        Charset charset = StandardCharsets.US_ASCII;

        byte[] byteArrray = charset.encode(encryptDecrypt(inputString)).array();

        // System.out.println("byte length: " + byteArrray.length);

        // for (byte b : byteArrray) 
        // {
        //     System.out.println(b);    
        // }

        return byteArrray.length;
    }

    static String encryptDecrypt(String inputString)
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
