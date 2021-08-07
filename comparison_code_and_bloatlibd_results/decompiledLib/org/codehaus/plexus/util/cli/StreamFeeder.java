package org.codehaus.plexus.util.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamFeeder
  extends AbstractStreamHandler
{
  private InputStream input;
  private OutputStream output;
  private volatile Throwable exception = null;
  
  public StreamFeeder(InputStream input, OutputStream output)
  {
    this.input = input;
    this.output = output;
  }
  
  public void run()
  {
    try
    {
      feed();
    }
    catch (Throwable ex)
    {
      if (exception == null) {
        exception = ex;
      }
    }
    finally
    {
      close();
      synchronized (this)
      {
        setDone();
        
        notifyAll();
      }
    }
  }
  
  public void close()
  {
    if (input != null) {
      synchronized (input)
      {
        try
        {
          input.close();
        }
        catch (IOException ex)
        {
          if (exception == null) {
            exception = ex;
          }
        }
        input = null;
      }
    }
    if (output != null) {
      synchronized (output)
      {
        try
        {
          output.close();
        }
        catch (IOException ex)
        {
          if (exception == null) {
            exception = ex;
          }
        }
        output = null;
      }
    }
  }
  
  public Throwable getException()
  {
    return exception;
  }
  
  private void feed()
    throws IOException
  {
    boolean flush = false;
    int data = input.read();
    while ((!isDone()) && (data != -1)) {
      synchronized (output)
      {
        if (!isDisabled())
        {
          output.write(data);
          flush = true;
        }
        data = input.read();
      }
    }
    if (flush) {
      output.flush();
    }
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.cli.StreamFeeder
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */