package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class Type$FileDir
  extends EnumeratedAttribute
{
  private static final String[] VALUES = { "file", "dir", "any" };
  
  public Type$FileDir() {}
  
  public Type$FileDir(String value)
  {
    setValue(value);
  }
  
  public String[] getValues()
  {
    return VALUES;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.selectors.Type.FileDir
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */