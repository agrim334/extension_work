package org.apache.tools.ant.util;

public class IdentityMapper
  implements FileNameMapper
{
  public void setFrom(String from) {}
  
  public void setTo(String to) {}
  
  public String[] mapFileName(String sourceFileName)
  {
    if (sourceFileName == null) {
      return null;
    }
    return new String[] { sourceFileName };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.IdentityMapper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */