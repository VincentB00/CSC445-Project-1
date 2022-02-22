import java.util.concurrent.TimeUnit;

public class Timer implements Runnable
{
    private double second = 0f;
    private boolean stopTimer;
    Thread mainThread;

    public Timer()
    {
        
    }

    @Override
    public void run()
    {
        while(!mainThread.isInterrupted())
        {
            try
            {
                TimeUnit.MILLISECONDS.sleep(10);
                // Thread.sleep(10);
                second += 0.01;
            }
            catch (Exception e) 
            {
                e.printStackTrace();
            }

            if(stopTimer)
                break;
        }
    }

    /**
     * this function will start the timer
     */
    public void startTimer()
    {
        this.second = 0;
        stopTimer = false;
        mainThread = new Thread(this);
        mainThread.start();
    }

    /**
     * this function will stop the timer
     */
    public double stopTimer()
    {
        stopTimer = true;
        mainThread.interrupt();
        return second;
    }

    public double reStartTimer()
    {
        double temp = stopTimer();
        startTimer();

        return temp;
    }
}
