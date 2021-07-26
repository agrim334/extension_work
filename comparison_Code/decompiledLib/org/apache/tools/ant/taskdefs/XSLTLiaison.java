package org.apache.tools.ant.taskdefs;

import java.io.File;

public abstract interface XSLTLiaison
{
  public static final String FILE_PROTOCOL_PREFIX = "file://";
  
  public abstract void setStylesheet(File paramFile)
    throws Exception;
  
  public abstract void addParam(String paramString1, String paramString2)
    throws Exception;
  
  public abstract void transform(File paramFile1, File paramFile2)
    throws Exception;
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.XSLTLiaison
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */