package org.apache.tools.ant.taskdefs.optional.jsp.compilers;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.jsp.JspC;
import org.apache.tools.ant.taskdefs.optional.jsp.JspMangler;

public abstract interface JspCompilerAdapter
{
  public abstract void setJspc(JspC paramJspC);
  
  public abstract boolean execute()
    throws BuildException;
  
  public abstract JspMangler createMangler();
  
  public abstract boolean implementsOwnDependencyChecking();
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.jsp.compilers.JspCompilerAdapter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */