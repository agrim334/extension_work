package org.codehaus.plexus.util.cli;

public class AbstractStreamHandler
  extends Thread
{
  private boolean done;
  private volatile boolean disabled;
  
  public boolean isDone()
  {
    return done;
  }
  
  public synchronized void waitUntilDone()
    throws InterruptedException
  {
    while (!isDone()) {
      wait();
    }
  }
  
  protected boolean isDisabled()
  {
    return disabled;
  }
  
  public void disable()
  {
    disabled = true;
  }
  
  public void setDone()
  {
    done = true;
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.cli.AbstractStreamHandler
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */