package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class Sleep
  extends Task
{
  private boolean failOnError = true;
  private int seconds = 0;
  private int hours = 0;
  private int minutes = 0;
  private int milliseconds = 0;
  
  public void setSeconds(int seconds)
  {
    this.seconds = seconds;
  }
  
  public void setHours(int hours)
  {
    this.hours = hours;
  }
  
  public void setMinutes(int minutes)
  {
    this.minutes = minutes;
  }
  
  public void setMilliseconds(int milliseconds)
  {
    this.milliseconds = milliseconds;
  }
  
  public void doSleep(long millis)
  {
    try
    {
      Thread.sleep(millis);
    }
    catch (InterruptedException localInterruptedException) {}
  }
  
  public void setFailOnError(boolean failOnError)
  {
    this.failOnError = failOnError;
  }
  
  private long getSleepTime()
  {
    return ((hours * 60L + minutes) * 60L + seconds) * 1000L + milliseconds;
  }
  
  public void validate()
    throws BuildException
  {
    if (getSleepTime() < 0L) {
      throw new BuildException("Negative sleep periods are not supported");
    }
  }
  
  public void execute()
    throws BuildException
  {
    try
    {
      validate();
      long sleepTime = getSleepTime();
      log("sleeping for " + sleepTime + " milliseconds", 3);
      
      doSleep(sleepTime);
    }
    catch (Exception e)
    {
      if (failOnError) {
        throw new BuildException(e);
      }
      log(e.toString(), 0);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Sleep
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */