package org.apache.tools.ant.util;

import org.apache.tools.ant.Task;

public final class TaskLogger
{
  private Task task;
  
  public TaskLogger(Task task)
  {
    this.task = task;
  }
  
  public void info(String message)
  {
    task.log(message, 2);
  }
  
  public void error(String message)
  {
    task.log(message, 0);
  }
  
  public void warning(String message)
  {
    task.log(message, 1);
  }
  
  public void verbose(String message)
  {
    task.log(message, 3);
  }
  
  public void debug(String message)
  {
    task.log(message, 4);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.TaskLogger
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */