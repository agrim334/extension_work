package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class Available$FileDir
  extends EnumeratedAttribute
{
  private static final String[] VALUES = { "file", "dir" };
  
  public String[] getValues()
  {
    return VALUES;
  }
  
  public boolean isDir()
  {
    return "dir".equalsIgnoreCase(getValue());
  }
  
  public boolean isFile()
  {
    return "file".equalsIgnoreCase(getValue());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Available.FileDir
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */