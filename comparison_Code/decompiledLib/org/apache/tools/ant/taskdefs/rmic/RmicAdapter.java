package org.apache.tools.ant.taskdefs.rmic;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Rmic;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileNameMapper;

public abstract interface RmicAdapter
{
  public abstract void setRmic(Rmic paramRmic);
  
  public abstract boolean execute()
    throws BuildException;
  
  public abstract FileNameMapper getMapper();
  
  public abstract Path getClasspath();
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.rmic.RmicAdapter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */