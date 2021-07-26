package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.tools.ant.util.FileUtils;

public class StreamPumper
  implements Runnable
{
  private static final int SMALL_BUFFER_SIZE = 128;
  private final InputStream is;
  private final OutputStream os;
  private volatile boolean askedToStop;
  private volatile boolean finished;
  private final boolean closeWhenExhausted;
  private boolean autoflush = false;
  private Exception exception = null;
  private int bufferSize = 128;
  private boolean started = false;
  private final boolean useAvailable;
  private PostStopHandle postStopHandle;
  private static final long POLL_INTERVAL = 100L;
  
  public StreamPumper(InputStream is, OutputStream os, boolean closeWhenExhausted)
  {
    this(is, os, closeWhenExhausted, false);
  }
  
  public StreamPumper(InputStream is, OutputStream os, boolean closeWhenExhausted, boolean useAvailable)
  {
    this.is = is;
    this.os = os;
    this.closeWhenExhausted = closeWhenExhausted;
    this.useAvailable = useAvailable;
  }
  
  public StreamPumper(InputStream is, OutputStream os)
  {
    this(is, os, false);
  }
  
  void setAutoflush(boolean autoflush)
  {
    this.autoflush = autoflush;
  }
  
  public void run()
  {
    synchronized (this)
    {
      started = true;
    }
    finished = false;
    
    byte[] buf = new byte[bufferSize];
    try
    {
      while ((!askedToStop) && (!Thread.interrupted()))
      {
        waitForInput(is);
        if ((askedToStop) || (Thread.interrupted())) {
          break;
        }
        int length = is.read(buf);
        if (length < 0) {
          break;
        }
        if (length > 0)
        {
          os.write(buf, 0, length);
          if (autoflush) {
            os.flush();
          }
        }
      }
      doPostStop();
    }
    catch (InterruptedException localInterruptedException1) {}catch (Exception e)
    {
      synchronized (this)
      {
        exception = e;
      }
    }
    finally
    {
      if (closeWhenExhausted) {
        FileUtils.close(os);
      }
      finished = true;
      askedToStop = false;
      synchronized (this)
      {
        notifyAll();
      }
    }
  }
  
  public boolean isFinished()
  {
    return finished;
  }
  
  public synchronized void waitFor()
    throws InterruptedException
  {
    while (!isFinished()) {
      wait();
    }
  }
  
  public synchronized void setBufferSize(int bufferSize)
  {
    if (started) {
      throw new IllegalStateException("Cannot set buffer size on a running StreamPumper");
    }
    this.bufferSize = bufferSize;
  }
  
  public synchronized int getBufferSize()
  {
    return bufferSize;
  }
  
  public synchronized Exception getException()
  {
    return exception;
  }
  
  synchronized PostStopHandle stop()
  {
    askedToStop = true;
    postStopHandle = new PostStopHandle();
    notifyAll();
    return postStopHandle;
  }
  
  private void waitForInput(InputStream is)
    throws IOException, InterruptedException
  {
    if (useAvailable) {
      while ((!askedToStop) && (is.available() == 0))
      {
        if (Thread.interrupted()) {
          throw new InterruptedException();
        }
        synchronized (this)
        {
          wait(100L);
        }
      }
    }
  }
  
  private void doPostStop()
    throws IOException
  {
    try
    {
      byte[] buf = new byte[bufferSize];
      if (askedToStop)
      {
        int bytesReadableWithoutBlocking;
        while ((bytesReadableWithoutBlocking = is.available()) > 0)
        {
          int length = is.read(buf, 0, Math.min(bytesReadableWithoutBlocking, buf.length));
          if (length <= 0) {
            break;
          }
          os.write(buf, 0, length);
        }
      }
      os.flush();
    }
    finally
    {
      if (postStopHandle != null)
      {
        postStopHandle.latch.countDown();
        postStopHandle.inPostStopTasks = false;
      }
    }
  }
  
  final class PostStopHandle
  {
    private boolean inPostStopTasks = true;
    private final CountDownLatch latch = new CountDownLatch(1);
    
    PostStopHandle() {}
    
    boolean isInPostStopTasks()
    {
      return inPostStopTasks;
    }
    
    boolean awaitPostStopCompletion(long timeout, TimeUnit timeUnit)
      throws InterruptedException
    {
      return latch.await(timeout, timeUnit);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.StreamPumper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */