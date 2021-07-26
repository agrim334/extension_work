package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

public class Retry
  extends Task
  implements TaskContainer
{
  private Task nestedTask;
  private int retryCount = 1;
  private int retryDelay = 0;
  
  public synchronized void addTask(Task t)
  {
    if (nestedTask != null) {
      throw new BuildException("The retry task container accepts a single nested task (which may be a sequential task container)");
    }
    nestedTask = t;
  }
  
  public void setRetryCount(int n)
  {
    retryCount = n;
  }
  
  public void setRetryDelay(int retryDelay)
  {
    if (retryDelay < 0) {
      throw new BuildException("retryDelay must be a non-negative number");
    }
    this.retryDelay = retryDelay;
  }
  
  public void execute()
    throws BuildException
  {
    StringBuilder errorMessages = new StringBuilder();
    for (int i = 0; i <= retryCount; i++) {
      try
      {
        nestedTask.perform();
      }
      catch (Exception e)
      {
        errorMessages.append(e.getMessage());
        if (i >= retryCount) {
          throw new BuildException(String.format("Task [%s] failed after [%d] attempts; giving up.%nError messages:%n%s", new Object[] {nestedTask.getTaskName(), Integer.valueOf(retryCount), errorMessages }), getLocation());
        }
        String msg;
        String msg;
        if (retryDelay > 0) {
          msg = "Attempt [" + i + "]: error occurred; retrying after " + retryDelay + " ms...";
        } else {
          msg = "Attempt [" + i + "]: error occurred; retrying...";
        }
        log(msg, e, 2);
        errorMessages.append(System.lineSeparator());
        if (retryDelay > 0) {
          try
          {
            Thread.sleep(retryDelay);
          }
          catch (InterruptedException localInterruptedException) {}
        }
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Retry
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */