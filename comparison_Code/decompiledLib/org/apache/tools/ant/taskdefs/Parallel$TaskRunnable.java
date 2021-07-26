package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.property.LocalProperties;

class Parallel$TaskRunnable
  implements Runnable
{
  private Throwable exception;
  private Task task;
  private boolean finished;
  private volatile Thread thread;
  
  Parallel$TaskRunnable(Parallel paramParallel, Task task)
  {
    this.task = task;
  }
  
  public void run()
  {
    try
    {
      LocalProperties.get(this$0.getProject()).copy();
      thread = Thread.currentThread();
      task.perform();
    }
    catch (Throwable t)
    {
      exception = t;
      if (Parallel.access$500(this$0)) {
        Parallel.access$302(this$0, false);
      }
    }
    finally
    {
      synchronized (Parallel.access$200(this$0))
      {
        finished = true;
        Parallel.access$200(this$0).notifyAll();
      }
    }
  }
  
  public Throwable getException()
  {
    return exception;
  }
  
  boolean isFinished()
  {
    return finished;
  }
  
  void interrupt()
  {
    thread.interrupt();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Parallel.TaskRunnable
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */