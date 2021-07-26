package org.apache.tools.ant.taskdefs.optional.testing;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.WaitFor;

public class BlockFor
  extends WaitFor
{
  private String text;
  
  public BlockFor()
  {
    super("blockfor");
    text = (getTaskName() + " timed out");
  }
  
  public BlockFor(String taskName)
  {
    super(taskName);
  }
  
  protected void processTimeout()
    throws BuildTimeoutException
  {
    super.processTimeout();
    throw new BuildTimeoutException(text, getLocation());
  }
  
  public void addText(String message)
  {
    text = getProject().replaceProperties(message);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.testing.BlockFor
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */