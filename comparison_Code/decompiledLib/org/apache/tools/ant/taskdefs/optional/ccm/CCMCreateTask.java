package org.apache.tools.ant.taskdefs.optional.ccm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.util.StringUtils;

public class CCMCreateTask
  extends Continuus
  implements ExecuteStreamHandler
{
  public static final String FLAG_COMMENT = "/synopsis";
  public static final String FLAG_PLATFORM = "/plat";
  public static final String FLAG_RESOLVER = "/resolver";
  public static final String FLAG_RELEASE = "/release";
  public static final String FLAG_SUBSYSTEM = "/subsystem";
  public static final String FLAG_TASK = "/task";
  private String comment = null;
  private String platform = null;
  private String resolver = null;
  private String release = null;
  private String subSystem = null;
  private String task = null;
  
  public CCMCreateTask()
  {
    setCcmAction("create_task");
  }
  
  public void execute()
    throws BuildException
  {
    Commandline commandLine = new Commandline();
    
    commandLine.setExecutable(getCcmCommand());
    commandLine.createArgument().setValue(getCcmAction());
    
    checkOptions(commandLine);
    if (Execute.isFailure(run(commandLine, this))) {
      throw new BuildException("Failed executing: " + commandLine, getLocation());
    }
    Commandline commandLine2 = new Commandline();
    commandLine2.setExecutable(getCcmCommand());
    commandLine2.createArgument().setValue("default_task");
    commandLine2.createArgument().setValue(getTask());
    
    log(commandLine.describeCommand(), 4);
    if (run(commandLine2) != 0) {
      throw new BuildException("Failed executing: " + commandLine2, getLocation());
    }
  }
  
  private void checkOptions(Commandline cmd)
  {
    if (getComment() != null)
    {
      cmd.createArgument().setValue("/synopsis");
      cmd.createArgument().setValue("\"" + getComment() + "\"");
    }
    if (getPlatform() != null)
    {
      cmd.createArgument().setValue("/plat");
      cmd.createArgument().setValue(getPlatform());
    }
    if (getResolver() != null)
    {
      cmd.createArgument().setValue("/resolver");
      cmd.createArgument().setValue(getResolver());
    }
    if (getSubSystem() != null)
    {
      cmd.createArgument().setValue("/subsystem");
      cmd.createArgument().setValue("\"" + getSubSystem() + "\"");
    }
    if (getRelease() != null)
    {
      cmd.createArgument().setValue("/release");
      cmd.createArgument().setValue(getRelease());
    }
  }
  
  public String getComment()
  {
    return comment;
  }
  
  public void setComment(String v)
  {
    comment = v;
  }
  
  public String getPlatform()
  {
    return platform;
  }
  
  public void setPlatform(String v)
  {
    platform = v;
  }
  
  public String getResolver()
  {
    return resolver;
  }
  
  public void setResolver(String v)
  {
    resolver = v;
  }
  
  public String getRelease()
  {
    return release;
  }
  
  public void setRelease(String v)
  {
    release = v;
  }
  
  public String getSubSystem()
  {
    return subSystem;
  }
  
  public void setSubSystem(String v)
  {
    subSystem = v;
  }
  
  public String getTask()
  {
    return task;
  }
  
  public void setTask(String v)
  {
    task = v;
  }
  
  public void start()
    throws IOException
  {}
  
  public void stop() {}
  
  public void setProcessInputStream(OutputStream param1)
    throws IOException
  {}
  
  public void setProcessErrorStream(InputStream is)
    throws IOException
  {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    try
    {
      String s = reader.readLine();
      if (s != null) {
        log("err " + s, 4);
      }
      reader.close();
    }
    catch (Throwable localThrowable) {}
    try
    {
      reader.close();
    }
    catch (Throwable localThrowable1)
    {
      localThrowable.addSuppressed(localThrowable1);
    }
    throw localThrowable;
  }
  
  public void setProcessOutputStream(InputStream is)
    throws IOException
  {
    try
    {
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      try
      {
        String buffer = reader.readLine();
        if (buffer != null)
        {
          log("buffer:" + buffer, 4);
          String taskstring = buffer.substring(buffer.indexOf(' ')).trim();
          taskstring = taskstring.substring(0, taskstring.lastIndexOf(' ')).trim();
          setTask(taskstring);
          log("task is " + getTask(), 4);
        }
        reader.close();
      }
      catch (Throwable localThrowable) {}
      try
      {
        reader.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      throw localThrowable;
    }
    catch (NullPointerException npe)
    {
      log("error procession stream, null pointer exception", 0);
      log(StringUtils.getStackTrace(npe), 0);
      throw new BuildException(npe);
    }
    catch (Exception e)
    {
      log("error procession stream " + e.getMessage(), 0);
      throw new BuildException(e.getMessage());
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ccm.CCMCreateTask
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */