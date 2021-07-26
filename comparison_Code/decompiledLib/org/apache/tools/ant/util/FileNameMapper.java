package org.apache.tools.ant.util;

public abstract interface FileNameMapper
{
  public abstract void setFrom(String paramString);
  
  public abstract void setTo(String paramString);
  
  public abstract String[] mapFileName(String paramString);
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.FileNameMapper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */