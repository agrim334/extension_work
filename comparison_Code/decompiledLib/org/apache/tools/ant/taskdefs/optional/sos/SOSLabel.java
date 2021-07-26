package org.apache.tools.ant.taskdefs.optional.sos;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;

public class SOSLabel
  extends SOS
{
  public void setVersion(String version)
  {
    super.setInternalVersion(version);
  }
  
  public void setLabel(String label)
  {
    super.setInternalLabel(label);
  }
  
  public void setComment(String comment)
  {
    super.setInternalComment(comment);
  }
  
  protected Commandline buildCmdLine()
  {
    commandLine = new Commandline();
    
    commandLine.createArgument().setValue("-command");
    commandLine.createArgument().setValue("AddLabel");
    
    getRequiredAttributes();
    if (getLabel() == null) {
      throw new BuildException("label attribute must be set!", getLocation());
    }
    commandLine.createArgument().setValue("-label");
    commandLine.createArgument().setValue(getLabel());
    
    commandLine.createArgument().setValue(getVerbose());
    if (getComment() != null)
    {
      commandLine.createArgument().setValue("-log");
      commandLine.createArgument().setValue(getComment());
    }
    return commandLine;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.sos.SOSLabel
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */