package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class Zip$WhenEmpty
  extends EnumeratedAttribute
{
  public String[] getValues()
  {
    return new String[] { "fail", "skip", "create" };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Zip.WhenEmpty
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */