package org.apache.tools.ant.taskdefs.optional.vss;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.Path;

public class MSVSSADD
  extends MSVSS
{
  private String localPath = null;
  
  protected Commandline buildCmdLine()
  {
    Commandline commandLine = new Commandline();
    if (getLocalpath() == null)
    {
      String msg = "localPath attribute must be set!";
      throw new BuildException(msg, getLocation());
    }
    commandLine.setExecutable(getSSCommand());
    commandLine.createArgument().setValue("Add");
    
    commandLine.createArgument().setValue(getLocalpath());
    
    commandLine.createArgument().setValue(getAutoresponse());
    
    commandLine.createArgument().setValue(getRecursive());
    
    commandLine.createArgument().setValue(getWritable());
    
    commandLine.createArgument().setValue(getLogin());
    
    commandLine.createArgument().setValue(getComment());
    
    return commandLine;
  }
  
  protected String getLocalpath()
  {
    return localPath;
  }
  
  public void setRecursive(boolean recursive)
  {
    super.setInternalRecursive(recursive);
  }
  
  public final void setWritable(boolean writable)
  {
    super.setInternalWritable(writable);
  }
  
  public void setAutoresponse(String response)
  {
    super.setInternalAutoResponse(response);
  }
  
  public void setComment(String comment)
  {
    super.setInternalComment(comment);
  }
  
  public void setLocalpath(Path localPath)
  {
    this.localPath = localPath.toString();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.vss.MSVSSADD
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */