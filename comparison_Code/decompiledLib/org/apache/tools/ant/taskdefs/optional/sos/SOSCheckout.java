package org.apache.tools.ant.taskdefs.optional.sos;

import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;

public class SOSCheckout
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
  
  protected Commandline buildCmdLine()
  {
    commandLine = new Commandline();
    if (getFilename() != null)
    {
      commandLine.createArgument().setValue("-command");
      commandLine.createArgument().setValue("CheckOutFile");
      
      commandLine.createArgument().setValue("-file");
      commandLine.createArgument().setValue(getFilename());
    }
    else
    {
      commandLine.createArgument().setValue("-command");
      commandLine.createArgument().setValue("CheckOutProject");
      
      commandLine.createArgument().setValue(getRecursive());
    }
    getRequiredAttributes();
    getOptionalAttributes();
    
    return commandLine;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.sos.SOSCheckout
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */