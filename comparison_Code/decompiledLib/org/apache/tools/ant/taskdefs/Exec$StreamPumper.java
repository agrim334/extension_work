package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class Exec$StreamPumper
  extends Thread
{
  private BufferedReader din;
  private int messageLevel;
  private boolean endOfStream = false;
  private static final int SLEEP_TIME = 5;
  
  public Exec$StreamPumper(Exec this$0, InputStream is, int messageLevel)
  {
    din = new BufferedReader(new InputStreamReader(is));
    this.messageLevel = messageLevel;
  }
  
  public void pumpStream()
    throws IOException
  {
    if (!endOfStream)
    {
      String line = din.readLine();
      if (line != null) {
        this$0.outputLog(line, messageLevel);
      } else {
        endOfStream = true;
      }
    }
  }
  
  public void run()
  {
    try
    {
      try
      {
        while (!endOfStream)
        {
          pumpStream();
          sleep(5L);
        }
      }
      catch (InterruptedException localInterruptedException) {}
      din.close();
    }
    catch (IOException localIOException) {}
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Exec.StreamPumper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */