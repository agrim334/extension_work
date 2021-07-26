package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import org.apache.tools.ant.util.FileUtils;

public class PumpStreamHandler
  implements ExecuteStreamHandler
{
  private Thread outputThread;
  private Thread errorThread;
  private Thread inputThread;
  private OutputStream out;
  private OutputStream err;
  private InputStream input;
  private final boolean nonBlockingRead;
  private static final long JOIN_TIMEOUT = 200L;
  
  public PumpStreamHandler(OutputStream out, OutputStream err, InputStream input, boolean nonBlockingRead)
  {
    if (out == null) {
      throw new NullPointerException("out must not be null");
    }
    if (err == null) {
      throw new NullPointerException("err must not be null");
    }
    this.out = out;
    this.err = err;
    this.input = input;
    this.nonBlockingRead = nonBlockingRead;
  }
  
  public PumpStreamHandler(OutputStream out, OutputStream err, InputStream input)
  {
    this(out, err, input, false);
  }
  
  public PumpStreamHandler(OutputStream out, OutputStream err)
  {
    this(out, err, null);
  }
  
  public PumpStreamHandler(OutputStream outAndErr)
  {
    this(outAndErr, outAndErr);
  }
  
  public PumpStreamHandler()
  {
    this(System.out, System.err);
  }
  
  public void setProcessOutputStream(InputStream is)
  {
    createProcessOutputPump(is, out);
  }
  
  public void setProcessErrorStream(InputStream is)
  {
    createProcessErrorPump(is, err);
  }
  
  public void setProcessInputStream(OutputStream os)
  {
    if (input != null) {
      inputThread = createPump(input, os, true, nonBlockingRead);
    } else {
      FileUtils.close(os);
    }
  }
  
  public void start()
  {
    start(outputThread);
    start(errorThread);
    start(inputThread);
  }
  
  public void stop()
  {
    finish(inputThread);
    try
    {
      err.flush();
    }
    catch (IOException localIOException) {}
    try
    {
      out.flush();
    }
    catch (IOException localIOException1) {}
    finish(outputThread);
    finish(errorThread);
  }
  
  private void start(Thread t)
  {
    if (t != null) {
      t.start();
    }
  }
  
  protected final void finish(Thread t)
  {
    if (t == null) {
      return;
    }
    try
    {
      StreamPumper s = null;
      if ((t instanceof ThreadWithPumper)) {
        s = ((ThreadWithPumper)t).getPumper();
      }
      if ((s != null) && (s.isFinished())) {
        return;
      }
      if (!t.isAlive()) {
        return;
      }
      StreamPumper.PostStopHandle postStopHandle = null;
      if ((s != null) && (!s.isFinished())) {
        postStopHandle = s.stop();
      }
      if ((postStopHandle != null) && (postStopHandle.isInPostStopTasks())) {
        postStopHandle.awaitPostStopCompletion(2L, TimeUnit.SECONDS);
      }
      while (((s == null) || (!s.isFinished())) && (t.isAlive()))
      {
        t.interrupt();
        t.join(200L);
      }
    }
    catch (InterruptedException localInterruptedException) {}
  }
  
  protected OutputStream getErr()
  {
    return err;
  }
  
  protected OutputStream getOut()
  {
    return out;
  }
  
  protected void createProcessOutputPump(InputStream is, OutputStream os)
  {
    outputThread = createPump(is, os);
  }
  
  protected void createProcessErrorPump(InputStream is, OutputStream os)
  {
    errorThread = createPump(is, os);
  }
  
  protected Thread createPump(InputStream is, OutputStream os)
  {
    return createPump(is, os, false);
  }
  
  protected Thread createPump(InputStream is, OutputStream os, boolean closeWhenExhausted)
  {
    return createPump(is, os, closeWhenExhausted, true);
  }
  
  protected Thread createPump(InputStream is, OutputStream os, boolean closeWhenExhausted, boolean nonBlockingIO)
  {
    StreamPumper pumper = new StreamPumper(is, os, closeWhenExhausted, nonBlockingIO);
    pumper.setAutoflush(true);
    Thread result = new ThreadWithPumper(pumper);
    result.setDaemon(true);
    return result;
  }
  
  protected static class ThreadWithPumper
    extends Thread
  {
    private final StreamPumper pumper;
    
    public ThreadWithPumper(StreamPumper p)
    {
      super();
      pumper = p;
    }
    
    protected StreamPumper getPumper()
    {
      return pumper;
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.PumpStreamHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */