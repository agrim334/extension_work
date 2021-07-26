package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class Javadoc$AccessType
  extends EnumeratedAttribute
{
  public String[] getValues()
  {
    return new String[] { "protected", "public", "package", "private" };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Javadoc.AccessType
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */