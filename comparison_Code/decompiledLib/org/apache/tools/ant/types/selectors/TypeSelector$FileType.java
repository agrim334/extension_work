package org.apache.tools.ant.types.selectors;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class TypeSelector$FileType
  extends EnumeratedAttribute
{
  public static final String FILE = "file";
  public static final String DIR = "dir";
  
  public String[] getValues()
  {
    return new String[] { "file", "dir" };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.TypeSelector.FileType
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */