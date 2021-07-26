package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class Length$FileMode
  extends EnumeratedAttribute
{
  static final String[] MODES = { "each", "all" };
  
  public String[] getValues()
  {
    return MODES;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Length.FileMode
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */