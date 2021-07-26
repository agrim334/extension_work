package org.apache.tools.ant.taskdefs.optional.clearcase;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;

public class CCUpdate
  extends ClearCase
{
  public static final String FLAG_GRAPHICAL = "-graphical";
  public static final String FLAG_LOG = "-log";
  public static final String FLAG_OVERWRITE = "-overwrite";
  public static final String FLAG_NOVERWRITE = "-noverwrite";
  public static final String FLAG_RENAME = "-rename";
  public static final String FLAG_CURRENTTIME = "-ctime";
  public static final String FLAG_PRESERVETIME = "-ptime";
  private boolean mGraphical = false;
  private boolean mOverwrite = false;
  private boolean mRename = false;
  private boolean mCtime = false;
  private boolean mPtime = false;
  private String mLog = null;
  
  public void execute()
    throws BuildException
  {
    Commandline commandLine = new Commandline();
    Project aProj = getProject();
    if (getViewPath() == null) {
      setViewPath(aProj.getBaseDir().getPath());
    }
    commandLine.setExecutable(getClearToolCommand());
    commandLine.createArgument().setValue("update");
    
    checkOptions(commandLine);
    
    getProject().log(commandLine.toString(), 4);
    if (!getFailOnErr()) {
      getProject().log("Ignoring any errors that occur for: " + 
        getViewPathBasename(), 3);
    }
    int result = run(commandLine);
    if ((Execute.isFailure(result)) && (getFailOnErr())) {
      throw new BuildException("Failed executing: " + commandLine, getLocation());
    }
  }
  
  private void checkOptions(Commandline cmd)
  {
    if (getGraphical())
    {
      cmd.createArgument().setValue("-graphical");
    }
    else
    {
      if (getOverwrite()) {
        cmd.createArgument().setValue("-overwrite");
      } else if (getRename()) {
        cmd.createArgument().setValue("-rename");
      } else {
        cmd.createArgument().setValue("-noverwrite");
      }
      if (getCurrentTime()) {
        cmd.createArgument().setValue("-ctime");
      } else if (getPreserveTime()) {
        cmd.createArgument().setValue("-ptime");
      }
      getLogCommand(cmd);
    }
    cmd.createArgument().setValue(getViewPath());
  }
  
  public void setGraphical(boolean graphical)
  {
    mGraphical = graphical;
  }
  
  public boolean getGraphical()
  {
    return mGraphical;
  }
  
  public void setOverwrite(boolean ow)
  {
    mOverwrite = ow;
  }
  
  public boolean getOverwrite()
  {
    return mOverwrite;
  }
  
  public void setRename(boolean ren)
  {
    mRename = ren;
  }
  
  public boolean getRename()
  {
    return mRename;
  }
  
  public void setCurrentTime(boolean ct)
  {
    mCtime = ct;
  }
  
  public boolean getCurrentTime()
  {
    return mCtime;
  }
  
  public void setPreserveTime(boolean pt)
  {
    mPtime = pt;
  }
  
  public boolean getPreserveTime()
  {
    return mPtime;
  }
  
  public void setLog(String log)
  {
    mLog = log;
  }
  
  public String getLog()
  {
    return mLog;
  }
  
  private void getLogCommand(Commandline cmd)
  {
    if (getLog() == null) {
      return;
    }
    cmd.createArgument().setValue("-log");
    cmd.createArgument().setValue(getLog());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.clearcase.CCUpdate
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */