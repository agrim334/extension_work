package org.apache.tools.ant;

public abstract interface SubBuildListener
  extends BuildListener
{
  public abstract void subBuildStarted(BuildEvent paramBuildEvent);
  
  public abstract void subBuildFinished(BuildEvent paramBuildEvent);
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.SubBuildListener
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */