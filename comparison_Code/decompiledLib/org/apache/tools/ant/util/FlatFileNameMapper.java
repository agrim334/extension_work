package org.apache.tools.ant.util;

import java.io.File;

public class FlatFileNameMapper
  implements FileNameMapper
{
  public void setFrom(String from) {}
  
  public void setTo(String to) {}
  
  public String[] mapFileName(String sourceFileName)
  {
    return 
      new String[] { sourceFileName == null ? null : new File(sourceFileName).getName() };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.FlatFileNameMapper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */