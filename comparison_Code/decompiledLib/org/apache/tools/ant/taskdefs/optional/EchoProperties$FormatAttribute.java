package org.apache.tools.ant.taskdefs.optional;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class EchoProperties$FormatAttribute
  extends EnumeratedAttribute
{
  private String[] formats = { "xml", "text" };
  
  public String[] getValues()
  {
    return formats;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.EchoProperties.FormatAttribute
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */