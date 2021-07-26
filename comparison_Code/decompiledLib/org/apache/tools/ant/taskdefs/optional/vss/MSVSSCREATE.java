package org.apache.tools.ant.taskdefs.optional.vss;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;

public class MSVSSCREATE
  extends MSVSS
{
  Commandline buildCmdLine()
  {
    Commandline commandLine = new Commandline();
    if (getVsspath() == null)
    {
      String msg = "vsspath attribute must be set!";
      throw new BuildException(msg, getLocation());
    }
    commandLine.setExecutable(getSSCommand());
    commandLine.createArgument().setValue("Create");
    
    commandLine.createArgument().setValue(getVsspath());
    
    commandLine.createArgument().setValue(getComment());
    
    commandLine.createArgument().setValue(getAutoresponse());
    
    commandLine.createArgument().setValue(getQuiet());
    
    commandLine.createArgument().setValue(getLogin());
    
    return commandLine;
  }
  
  public void setComment(String comment)
  {
    super.setInternalComment(comment);
  }
  
  public final void setQuiet(boolean quiet)
  {
    super.setInternalQuiet(quiet);
  }
  
  public void setAutoresponse(String response)
  {
    super.setInternalAutoResponse(response);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.vss.MSVSSCREATE
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */