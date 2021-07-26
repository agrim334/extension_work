package org.apache.tools.ant.taskdefs.optional.vss;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;

public class MSVSSLABEL
  extends MSVSS
{
  Commandline buildCmdLine()
  {
    Commandline commandLine = new Commandline();
    if (getVsspath() == null) {
      throw new BuildException("vsspath attribute must be set!", getLocation());
    }
    String label = getLabel();
    if (label.isEmpty())
    {
      String msg = "label attribute must be set!";
      throw new BuildException(msg, getLocation());
    }
    commandLine.setExecutable(getSSCommand());
    commandLine.createArgument().setValue("Label");
    
    commandLine.createArgument().setValue(getVsspath());
    
    commandLine.createArgument().setValue(getComment());
    
    commandLine.createArgument().setValue(getAutoresponse());
    
    commandLine.createArgument().setValue(label);
    
    commandLine.createArgument().setValue(getVersion());
    
    commandLine.createArgument().setValue(getLogin());
    
    return commandLine;
  }
  
  public void setLabel(String label)
  {
    super.setInternalLabel(label);
  }
  
  public void setVersion(String version)
  {
    super.setInternalVersion(version);
  }
  
  public void setComment(String comment)
  {
    super.setInternalComment(comment);
  }
  
  public void setAutoresponse(String response)
  {
    super.setInternalAutoResponse(response);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.vss.MSVSSLABEL
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */