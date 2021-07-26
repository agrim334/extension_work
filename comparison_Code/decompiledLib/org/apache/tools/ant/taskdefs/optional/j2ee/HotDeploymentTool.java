package org.apache.tools.ant.taskdefs.optional.j2ee;

import org.apache.tools.ant.BuildException;

public abstract interface HotDeploymentTool
{
  public static final String ACTION_DELETE = "delete";
  public static final String ACTION_DEPLOY = "deploy";
  public static final String ACTION_LIST = "list";
  public static final String ACTION_UNDEPLOY = "undeploy";
  public static final String ACTION_UPDATE = "update";
  
  public abstract void validateAttributes()
    throws BuildException;
  
  public abstract void deploy()
    throws BuildException;
  
  public abstract void setTask(ServerDeploy paramServerDeploy);
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.j2ee.HotDeploymentTool
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */