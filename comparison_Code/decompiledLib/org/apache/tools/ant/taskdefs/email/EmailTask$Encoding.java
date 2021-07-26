package org.apache.tools.ant.taskdefs.email;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class EmailTask$Encoding
  extends EnumeratedAttribute
{
  public String[] getValues()
  {
    return new String[] { "auto", "mime", "uu", "plain" };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.email.EmailTask.Encoding
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */