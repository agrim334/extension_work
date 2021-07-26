package org.apache.tools.ant;

import java.util.EventListener;

public abstract interface BuildListener
  extends EventListener
{
  public abstract void buildStarted(BuildEvent paramBuildEvent);
  
  public abstract void buildFinished(BuildEvent paramBuildEvent);
  
  public abstract void targetStarted(BuildEvent paramBuildEvent);
  
  public abstract void targetFinished(BuildEvent paramBuildEvent);
  
  public abstract void taskStarted(BuildEvent paramBuildEvent);
  
  public abstract void taskFinished(BuildEvent paramBuildEvent);
  
  public abstract void messageLogged(BuildEvent paramBuildEvent);
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.BuildListener
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */