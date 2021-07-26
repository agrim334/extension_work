package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class Definer$Format
  extends EnumeratedAttribute
{
  public static final int PROPERTIES = 0;
  public static final int XML = 1;
  
  public String[] getValues()
  {
    return new String[] { "properties", "xml" };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Definer.Format
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */