package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

class IPlanetEjbc$RedirectOutput
  extends Thread
{
  private InputStream stream;
  
  public IPlanetEjbc$RedirectOutput(InputStream stream)
  {
    this.stream = stream;
  }
  
  public void run()
  {
    try
    {
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
      try
      {
        String text;
        while ((text = reader.readLine()) != null) {
          System.out.println(text);
        }
        reader.close();
      }
      catch (Throwable localThrowable) {}
      try
      {
        reader.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      throw localThrowable;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ejb.IPlanetEjbc.RedirectOutput
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */