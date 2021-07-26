package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.taskdefs.email.EmailTask;

public class SendEmail
  extends EmailTask
{
  @Deprecated
  public void setMailport(Integer value)
  {
    setMailport(value.intValue());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.SendEmail
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */