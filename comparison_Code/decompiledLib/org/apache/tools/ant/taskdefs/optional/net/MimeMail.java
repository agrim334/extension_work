package org.apache.tools.ant.taskdefs.optional.net;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.email.EmailTask;

@Deprecated
public class MimeMail
  extends EmailTask
{
  public void execute()
    throws BuildException
  {
    log("DEPRECATED - The " + getTaskName() + " task is deprecated. Use the mail task instead.");
    
    super.execute();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.net.MimeMail
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */