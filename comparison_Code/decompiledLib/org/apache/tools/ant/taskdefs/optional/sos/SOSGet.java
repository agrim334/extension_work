package org.apache.tools.ant.taskdefs.optional.sos;

import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;

public class SOSGet
  extends SOS
{
  public final void setFile(String filename)
  {
    super.setInternalFilename(filename);
  }
  
  public void setRecursive(boolean recursive)
  {
    super.setInternalRecursive(recursive);
  }
  
  public void setVersion(String version)
  {
    super.setInternalVersion(version);
  }
  
  public void setLabel(String label)
  {
    super.setInternalLabel(label);
  }
  
  protected Commandline buildCmdLine()
  {
    commandLine = new Commandline();
    if (getFilename() != null)
    {
      commandLine.createArgument().setValue("-command");
      commandLine.createArgument().setValue("GetFile");
      
      commandLine.createArgument().setValue("-file");
      commandLine.createArgument().setValue(getFilename());
      if (getVersion() != null)
      {
        commandLine.createArgument().setValue("-revision");
        commandLine.createArgument().setValue(getVersion());
      }
    }
    else
    {
      commandLine.createArgument().setValue("-command");
      commandLine.createArgument().setValue("GetProject");
      
      commandLine.createArgument().setValue(getRecursive());
      if (getLabel() != null)
      {
        commandLine.createArgument().setValue("-label");
        commandLine.createArgument().setValue(getLabel());
      }
    }
    getRequiredAttributes();
    getOptionalAttributes();
    
    return commandLine;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.sos.SOSGet
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */