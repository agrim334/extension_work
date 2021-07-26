package org.apache.tools.ant;

public abstract interface Executor
{
  public abstract void executeTargets(Project paramProject, String[] paramArrayOfString)
    throws BuildException;
  
  public abstract Executor getSubProjectExecutor();
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.Executor
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */