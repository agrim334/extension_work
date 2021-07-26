package org.apache.tools.ant;

import java.util.ArrayList;
import java.util.List;

public class TaskConfigurationChecker
{
  private List<String> errors = new ArrayList();
  private final Task task;
  
  public TaskConfigurationChecker(Task task)
  {
    this.task = task;
  }
  
  public void assertConfig(boolean condition, String errormessage)
  {
    if (!condition) {
      errors.add(errormessage);
    }
  }
  
  public void fail(String errormessage)
  {
    errors.add(errormessage);
  }
  
  public void checkErrors()
    throws BuildException
  {
    if (!errors.isEmpty())
    {
      StringBuilder sb = new StringBuilder(String.format("Configuration error on <%s>:%n", new Object[] {task
        .getTaskName() }));
      for (String msg : errors) {
        sb.append(String.format("- %s%n", new Object[] { msg }));
      }
      throw new BuildException(sb.toString(), task.getLocation());
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.TaskConfigurationChecker
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */