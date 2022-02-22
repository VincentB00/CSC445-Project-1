import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Task1 
{
    public static void main(String[] args) throws Exception
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter("Result/Task1.csv"));
        writer.write("Test Number,RTT(nano second),packet byte\n");
        String result;
        
        SimpleTCPClient client = new SimpleTCPClient("129.3.20.89", 1111);
        String package1 = "love me!";
        String package2 = "that is why we feel comfortable to lift this, in effect tomorrow";
        String package3 = "I think I've said over the last few press conferences that I really believe that we ought to be looking seriously at how to ratchet that back,” Pritzker said of his mask mandates at a news conference Tuesday. “I think we're going to be making announcements";
        String package4 = "Long jokes with a twist are best type of jokes.\n1. My friend once called a few house painters to his house for some work. He wanted them to paint his porch. After a few hours, the house painters came back for the payment as their work was complete. Before leaving they told my friend that they had enjoyed painting his car, but it is not really a Porsche.\n2. There is a skeleton in our neighborhood who always knows that something bad might happen way before it actually happens. He has actually become quite famous and when a TV crew interviewed about the reason behind this ability, the skeleton finally disclosed his secret: he could feel the bad vibes in his bones.\n3. I had visited a cafe one day with my friends. The waiter recommended that we try their special coffee. We agreed and soon the coffee arrived. As we drink the coffee, we realized that it tastes like dirt and mud. Disgusted by the fact, all of us complained immediately. The alarmed waiter rushes over and says, \"Well Sir, it was freshly ground coffee!\"";
        try
        {   
            client.startThreadOnConnect = false;
            client.connect();

            for(int count = 1; count <= 1000; count++)
            {
                System.out.println("package 1: ");
                result = sendPackage(client, package1);
                System.out.println(result);
                saveResult(writer, count, result, 8);
                System.out.println("----------------------------------------");
                System.out.println("package 2: ");
                result = sendPackage(client, package2);
                System.out.println(result);
                saveResult(writer, count, result, 64);
                System.out.println("----------------------------------------");
                System.out.println("package 3: ");
                result = sendPackage(client, package3);
                System.out.println(result);
                saveResult(writer, count, result, 256);
                System.out.println("----------------------------------------");
                System.out.println("package 4: ");
                result = sendPackage(client, package4);
                System.out.println(result);
                saveResult(writer, count, result, 1024);
            }

            client.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        writer.close();
    }

    private static void saveResult(BufferedWriter writer, int testNumber, String result, int packetByte) throws IOException
    {
        String lines[] = result.split("\n");
        String RTT = null;
        for (String line : lines) {
            if(line.contains("nanos second"))
            {
                RTT = line.substring(line.indexOf("nanos second: ") + "nanos second: ".length() + 1);
                break;
            }
        }

        writer.append(String.format("%s,%s,%s\n", testNumber, RTT, packetByte));
    }

    
    public static String sendPackage(SimpleTCPClient client, String p) throws Exception
    {
        String ep = encryptDecrypt(p);
        long nano = System.nanoTime();
        long mill = System.currentTimeMillis();
        client.SendAndWaitForServer(ep);
        long currentMill = System.currentTimeMillis() - mill;
        long currentNano = System.nanoTime() - nano;
        String currentSecond = String.format("%s", (double)currentMill / 1000);

        return String.format("RTT (%s bytes package): \nnanos second: %s \nmills second: %s \nsecond: %s", getStringByteLength(ep), currentNano, currentMill, currentSecond);
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
