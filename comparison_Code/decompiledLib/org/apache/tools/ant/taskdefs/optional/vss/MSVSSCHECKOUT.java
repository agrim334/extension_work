package org.apache.tools.ant.taskdefs.optional.vss;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.Path;

public class MSVSSCHECKOUT
  extends MSVSS
{
  protected Commandline buildCmdLine()
  {
    Commandline commandLine = new Commandline();
    if (getVsspath() == null)
    {
      String msg = "vsspath attribute must be set!";
      throw new BuildException(msg, getLocation());
    }
    commandLine.setExecutable(getSSCommand());
    commandLine.createArgument().setValue("Checkout");
    
    commandLine.createArgument().setValue(getVsspath());
    
    commandLine.createArgument().setValue(getLocalpath());
    
    commandLine.createArgument().setValue(getAutoresponse());
    
    commandLine.createArgument().setValue(getRecursive());
    
    commandLine.createArgument().setValue(getVersionDateLabel());
    
    commandLine.createArgument().setValue(getLogin());
    
    commandLine.createArgument().setValue(getFileTimeStamp());
    
    commandLine.createArgument().setValue(getWritableFiles());
    
    commandLine.createArgument().setValue(getGetLocalCopy());
    
    return commandLine;
  }
  
  public void setLocalpath(Path localPath)
  {
    super.setInternalLocalPath(localPath.toString());
  }
  
  public void setRecursive(boolean recursive)
  {
    super.setInternalRecursive(recursive);
  }
  
  public void setVersion(String version)
  {
    super.setInternalVersion(version);
  }
  
  public void setDate(String date)
  {
    super.setInternalDate(date);
  }
  
  public void setLabel(String label)
  {
    super.setInternalLabel(label);
  }
  
  public void setAutoresponse(String response)
  {
    super.setInternalAutoResponse(response);
  }
  
  public void setFileTimeStamp(MSVSS.CurrentModUpdated timestamp)
  {
    super.setInternalFileTimeStamp(timestamp);
  }
  
  public void setWritableFiles(MSVSS.WritableFiles files)
  {
    super.setInternalWritableFiles(files);
  }
  
  public void setGetLocalCopy(boolean get)
  {
    super.setInternalGetLocalCopy(get);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.vss.MSVSSCHECKOUT
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */