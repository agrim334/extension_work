package org.apache.tools.ant.taskdefs.optional.j2ee;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Commandline.Argument;

public class WebLogicHotDeploymentTool
  extends AbstractHotDeploymentTool
  implements HotDeploymentTool
{
  private static final int STRING_BUFFER_SIZE = 1024;
  private static final String WEBLOGIC_DEPLOY_CLASS_NAME = "weblogic.deploy";
  private static final String[] VALID_ACTIONS = { "delete", "deploy", "list", "undeploy", "update" };
  private boolean debug;
  private String application;
  private String component;
  
  public void deploy()
  {
    Java java = new Java(getTask());
    java.setFork(true);
    java.setFailonerror(true);
    java.setClasspath(getClasspath());
    
    java.setClassname("weblogic.deploy");
    java.createArg().setLine(getArguments());
    java.execute();
  }
  
  public void validateAttributes()
    throws BuildException
  {
    super.validateAttributes();
    
    String action = getTask().getAction();
    if (getPassword() == null) {
      throw new BuildException("The password attribute must be set.");
    }
    if (((action.equals("deploy")) || (action.equals("update"))) && (application == null)) {
      throw new BuildException("The application attribute must be set if action = %s", new Object[] { action });
    }
    if (((action.equals("deploy")) || (action.equals("update"))) && 
      (getTask().getSource() == null)) {
      throw new BuildException("The source attribute must be set if action = %s", new Object[] { action });
    }
    if (((action.equals("delete")) || (action.equals("undeploy"))) && (application == null)) {
      throw new BuildException("The application attribute must be set if action = %s", new Object[] { action });
    }
  }
  
  public String getArguments()
    throws BuildException
  {
    String action = getTask().getAction();
    if ((action.equals("deploy")) || (action.equals("update"))) {
      return buildDeployArgs();
    }
    if ((action.equals("delete")) || (action.equals("undeploy"))) {
      return buildUndeployArgs();
    }
    if (action.equals("list")) {
      return buildListArgs();
    }
    return null;
  }
  
  protected boolean isActionValid()
  {
    String action = getTask().getAction();
    for (String validAction : VALID_ACTIONS) {
      if (action.equals(validAction)) {
        return true;
      }
    }
    return false;
  }
  
  protected StringBuffer buildArgsPrefix()
  {
    ServerDeploy task = getTask();
    
    return new StringBuffer(1024)
      .append(getServer() != null ? 
      "-url " + getServer() : 
      "")
      .append(" ")
      .append(debug ? "-debug " : "")
      .append(getUserName() != null ? 
      "-username " + getUserName() : 
      "")
      .append(" ")
      .append(task.getAction()).append(" ")
      .append(getPassword()).append(" ");
  }
  
  protected String buildDeployArgs()
  {
    String args = application + " " + getTask().getSource();
    if (component != null) {
      args = "-component " + component + " " + args;
    }
    return args;
  }
  
  protected String buildUndeployArgs()
  {
    return 
      application + " ";
  }
  
  protected String buildListArgs()
  {
    return 
      buildArgsPrefix().toString();
  }
  
  public void setDebug(boolean debug)
  {
    this.debug = debug;
  }
  
  public void setApplication(String application)
  {
    this.application = application;
  }
  
  public void setComponent(String component)
  {
    this.component = component;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.j2ee.WebLogicHotDeploymentTool
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */