package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

public class Nice
  extends Task
{
  private Integer newPriority;
  private String currentPriority;
  
  public void execute()
    throws BuildException
  {
    Thread self = Thread.currentThread();
    int priority = self.getPriority();
    if (currentPriority != null)
    {
      String current = Integer.toString(priority);
      getProject().setNewProperty(currentPriority, current);
    }
    if ((newPriority != null) && (priority != newPriority.intValue())) {
      try
      {
        self.setPriority(newPriority.intValue());
      }
      catch (SecurityException e)
      {
        log("Unable to set new priority -a security manager is in the way", 1);
      }
      catch (IllegalArgumentException iae)
      {
        throw new BuildException("Priority out of range", iae);
      }
    }
  }
  
  public void setCurrentPriority(String currentPriority)
  {
    this.currentPriority = currentPriority;
  }
  
  public void setNewPriority(int newPriority)
  {
    if ((newPriority < 1) || (newPriority > 10)) {
      throw new BuildException("The thread priority is out of the range 1-10");
    }
    this.newPriority = Integer.valueOf(newPriority);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Nice
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */