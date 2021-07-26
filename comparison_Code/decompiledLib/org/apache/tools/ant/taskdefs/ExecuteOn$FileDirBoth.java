package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class ExecuteOn$FileDirBoth
  extends EnumeratedAttribute
{
  public static final String FILE = "file";
  public static final String DIR = "dir";
  
  public String[] getValues()
  {
    return new String[] { "file", "dir", "both" };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.ExecuteOn.FileDirBoth
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */