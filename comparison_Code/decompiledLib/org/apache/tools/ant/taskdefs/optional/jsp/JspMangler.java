package org.apache.tools.ant.taskdefs.optional.jsp;

import java.io.File;

public abstract interface JspMangler
{
  public abstract String mapJspToJavaName(File paramFile);
  
  public abstract String mapPath(String paramString);
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.jsp.JspMangler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */