package org.apache.tools.ant.taskdefs.compilers;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Javac;

public abstract interface CompilerAdapter
{
  public abstract void setJavac(Javac paramJavac);
  
  public abstract boolean execute()
    throws BuildException;
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.compilers.CompilerAdapter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */