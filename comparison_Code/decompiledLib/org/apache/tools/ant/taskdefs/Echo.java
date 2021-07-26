package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.LogLevel;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.LogOutputResource;
import org.apache.tools.ant.types.resources.StringResource;
import org.apache.tools.ant.util.ResourceUtils;

public class Echo
  extends Task
{
  protected String message = "";
  protected File file = null;
  protected boolean append = false;
  private String encoding = "";
  private boolean force = false;
  protected int logLevel = 1;
  private Resource output;
  
  public void execute()
    throws BuildException
  {
    try
    {
      ResourceUtils.copyResource(new StringResource(
        message.isEmpty() ? System.lineSeparator() : message), 
        output == null ? new LogOutputResource(this, logLevel) : output, null, null, false, false, append, null, 
        
        encoding.isEmpty() ? null : encoding, getProject(), force);
    }
    catch (IOException ioe)
    {
      throw new BuildException(ioe, getLocation());
    }
  }
  
  public void setMessage(String msg)
  {
    message = (msg == null ? "" : msg);
  }
  
  public void setFile(File file)
  {
    setOutput(new FileResource(getProject(), file));
  }
  
  public void setOutput(Resource output)
  {
    if (this.output != null) {
      throw new BuildException("Cannot set > 1 output target");
    }
    this.output = output;
    FileProvider fp = (FileProvider)output.as(FileProvider.class);
    file = (fp != null ? fp.getFile() : null);
  }
  
  public void setAppend(boolean append)
  {
    this.append = append;
  }
  
  public void addText(String msg)
  {
    message += getProject().replaceProperties(msg);
  }
  
  public void setLevel(EchoLevel echoLevel)
  {
    logLevel = echoLevel.getLevel();
  }
  
  public void setEncoding(String encoding)
  {
    this.encoding = encoding;
  }
  
  public void setForce(boolean f)
  {
    force = f;
  }
  
  public static class EchoLevel
    extends LogLevel
  {}
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Echo
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */