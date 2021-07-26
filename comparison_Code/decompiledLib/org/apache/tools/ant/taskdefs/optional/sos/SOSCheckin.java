package org.apache.tools.ant.taskdefs.optional.sos;

import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;

public class SOSCheckin
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
  
  public void setComment(String comment)
  {
    super.setInternalComment(comment);
  }
  
  protected Commandline buildCmdLine()
  {
    commandLine = new Commandline();
    if (getFilename() != null)
    {
      commandLine.createArgument().setValue("-command");
      commandLine.createArgument().setValue("CheckInFile");
      
      commandLine.createArgument().setValue("-file");
      commandLine.createArgument().setValue(getFilename());
    }
    else
    {
      commandLine.createArgument().setValue("-command");
      commandLine.createArgument().setValue("CheckInProject");
      
      commandLine.createArgument().setValue(getRecursive());
    }
    getRequiredAttributes();
    getOptionalAttributes();
    if (getComment() != null)
    {
      commandLine.createArgument().setValue("-log");
      commandLine.createArgument().setValue(getComment());
    }
    return commandLine;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.sos.SOSCheckin
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */