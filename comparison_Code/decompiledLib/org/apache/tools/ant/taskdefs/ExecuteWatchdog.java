package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.TimeoutObserver;
import org.apache.tools.ant.util.Watchdog;

public class ExecuteWatchdog
  implements TimeoutObserver
{
  private Process process;
  private volatile boolean watch = false;
  private Exception caught = null;
  private volatile boolean killedProcess = false;
  private Watchdog watchdog;
  
  public ExecuteWatchdog(long timeout)
  {
    watchdog = new Watchdog(timeout);
    watchdog.addTimeoutObserver(this);
  }
  
  @Deprecated
  public ExecuteWatchdog(int timeout)
  {
    this(timeout);
  }
  
  public synchronized void start(Process process)
  {
    if (process == null) {
      throw new NullPointerException("process is null.");
    }
    if (this.process != null) {
      throw new IllegalStateException("Already running.");
    }
    caught = null;
    killedProcess = false;
    watch = true;
    this.process = process;
    watchdog.start();
  }
  
  public synchronized void stop()
  {
    watchdog.stop();
    cleanUp();
  }
  
  public synchronized void timeoutOccured(Watchdog w)
  {
    try
    {
      try
      {
        process.exitValue();
      }
      catch (IllegalThreadStateException itse)
      {
        if (watch)
        {
          killedProcess = true;
          process.destroy();
        }
      }
    }
    catch (Exception e)
    {
      caught = e;
    }
    finally
    {
      cleanUp();
    }
  }
  
  protected synchronized void cleanUp()
  {
    watch = false;
    process = null;
  }
  
  public synchronized void checkException()
    throws BuildException
  {
    if (caught != null) {
      throw new BuildException("Exception in ExecuteWatchdog.run: " + caught.getMessage(), caught);
    }
  }
  
  public boolean isWatching()
  {
    return watch;
  }
  
  public boolean killedProcess()
  {
    return killedProcess;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.ExecuteWatchdog
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */