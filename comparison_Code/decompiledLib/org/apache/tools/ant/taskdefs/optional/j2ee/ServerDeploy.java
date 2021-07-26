package org.apache.tools.ant.taskdefs.optional.j2ee;

import java.io.File;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class ServerDeploy
  extends Task
{
  private String action;
  private File source;
  private List<AbstractHotDeploymentTool> vendorTools = new Vector();
  
  public void addGeneric(GenericHotDeploymentTool tool)
  {
    tool.setTask(this);
    vendorTools.add(tool);
  }
  
  public void addWeblogic(WebLogicHotDeploymentTool tool)
  {
    tool.setTask(this);
    vendorTools.add(tool);
  }
  
  public void addJonas(JonasHotDeploymentTool tool)
  {
    tool.setTask(this);
    vendorTools.add(tool);
  }
  
  public void execute()
    throws BuildException
  {
    for (HotDeploymentTool tool : vendorTools)
    {
      tool.validateAttributes();
      tool.deploy();
    }
  }
  
  public String getAction()
  {
    return action;
  }
  
  public void setAction(String action)
  {
    this.action = action;
  }
  
  public File getSource()
  {
    return source;
  }
  
  public void setSource(File source)
  {
    this.source = source;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.j2ee.ServerDeploy
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */