package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.FilenameFilter;

public class InnerClassFilenameFilter
  implements FilenameFilter
{
  private String baseClassName;
  
  InnerClassFilenameFilter(String baseclass)
  {
    int extidx = baseclass.lastIndexOf(".class");
    if (extidx == -1) {
      extidx = baseclass.length() - 1;
    }
    baseClassName = baseclass.substring(0, extidx);
  }
  
  public boolean accept(File dir, String filename)
  {
    return (filename.lastIndexOf('.') == filename.lastIndexOf(".class")) && 
      (filename.indexOf(baseClassName + "$") == 0);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ejb.InnerClassFilenameFilter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */