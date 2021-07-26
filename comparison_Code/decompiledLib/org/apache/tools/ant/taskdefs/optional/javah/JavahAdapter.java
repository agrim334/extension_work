package org.apache.tools.ant.taskdefs.optional.javah;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.Javah;

public abstract interface JavahAdapter
{
  public abstract boolean compile(Javah paramJavah)
    throws BuildException;
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.javah.JavahAdapter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */