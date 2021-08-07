package org.codehaus.plexus.util.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class StreamPumper
  extends AbstractStreamHandler
{
  private final BufferedReader in;
  private final StreamConsumer consumer;
  private final PrintWriter out;
  private volatile Exception exception = null;
  private static final int SIZE = 1024;
  
  public StreamPumper(InputStream in)
  {
    this(in, (StreamConsumer)null);
  }
  
  public StreamPumper(InputStream in, StreamConsumer consumer)
  {
    this(in, null, consumer);
  }
  
  public StreamPumper(InputStream in, PrintWriter writer)
  {
    this(in, writer, null);
  }
  
  public StreamPumper(InputStream in, PrintWriter writer, StreamConsumer consumer)
  {
    this.in = new BufferedReader(new InputStreamReader(in), 1024);
    out = writer;
    this.consumer = consumer;
  }
  
  public void run()
  {
    boolean outError = out != null ? out.checkError() : false;
    try
    {
      for (String line = in.readLine(); line != null; line = in.readLine())
      {
        try
        {
          if ((exception == null) && (consumer != null) && (!isDisabled())) {
            consumer.consumeLine(line);
          }
        }
        catch (Exception t)
        {
          exception = t;
        }
        if ((out != null) && (!outError))
        {
          out.println(line);
          
          out.flush();
          if (out.checkError())
          {
            outError = true;
            try
            {
              throw new IOException(String.format("Failure printing line '%s'.", new Object[] { line }));
            }
            catch (IOException e)
            {
              exception = e;
            }
          }
        }
      }
    }
    catch (IOException e)
    {
      exception = e;
    }
    finally
    {
      try
      {
        in.close();
      }
      catch (IOException e2)
      {
        if (exception == null) {
          exception = e2;
        }
      }
      synchronized (this)
      {
        setDone();
        
        notifyAll();
      }
    }
  }
  
  public void flush()
  {
    if (out != null)
    {
      out.flush();
      if ((out.checkError()) && (exception == null)) {
        try
        {
          throw new IOException("Failure flushing output.");
        }
        catch (IOException e)
        {
          exception = e;
        }
      }
    }
  }
  
  public void close()
  {
    if (out != null)
    {
      out.close();
      if ((out.checkError()) && (exception == null)) {
        try
        {
          throw new IOException("Failure closing output.");
        }
        catch (IOException e)
        {
          exception = e;
        }
      }
    }
  }
  
  public Exception getException()
  {
    return exception;
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.cli.StreamPumper
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */