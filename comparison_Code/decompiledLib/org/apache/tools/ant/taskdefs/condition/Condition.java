package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;

public abstract interface Condition
{
  public abstract boolean eval()
    throws BuildException;
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.condition.Condition
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */