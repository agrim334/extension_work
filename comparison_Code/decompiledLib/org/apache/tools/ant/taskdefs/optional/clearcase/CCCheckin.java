package org.apache.tools.ant.taskdefs.optional.clearcase;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;

public class CCCheckin
  extends ClearCase
{
  public static final String FLAG_COMMENT = "-c";
  public static final String FLAG_COMMENTFILE = "-cfile";
  public static final String FLAG_NOCOMMENT = "-nc";
  public static final String FLAG_NOWARN = "-nwarn";
  public static final String FLAG_PRESERVETIME = "-ptime";
  public static final String FLAG_KEEPCOPY = "-keep";
  public static final String FLAG_IDENTICAL = "-identical";
  private String mComment = null;
  private String mCfile = null;
  private boolean mNwarn = false;
  private boolean mPtime = false;
  private boolean mKeep = false;
  private boolean mIdentical = true;
  
  public void execute()
    throws BuildException
  {
    Commandline commandLine = new Commandline();
    Project aProj = getProject();
    if (getViewPath() == null) {
      setViewPath(aProj.getBaseDir().getPath());
    }
    commandLine.setExecutable(getClearToolCommand());
    commandLine.createArgument().setValue("checkin");
    
    checkOptions(commandLine);
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
    if (getComment() != null) {
      getCommentCommand(cmd);
    } else if (getCommentFile() != null) {
      getCommentFileCommand(cmd);
    } else {
      cmd.createArgument().setValue("-nc");
    }
    if (getNoWarn()) {
      cmd.createArgument().setValue("-nwarn");
    }
    if (getPreserveTime()) {
      cmd.createArgument().setValue("-ptime");
    }
    if (getKeepCopy()) {
      cmd.createArgument().setValue("-keep");
    }
    if (getIdentical()) {
      cmd.createArgument().setValue("-identical");
    }
    cmd.createArgument().setValue(getViewPath());
  }
  
  public void setComment(String comment)
  {
    mComment = comment;
  }
  
  public String getComment()
  {
    return mComment;
  }
  
  public void setCommentFile(String cfile)
  {
    mCfile = cfile;
  }
  
  public String getCommentFile()
  {
    return mCfile;
  }
  
  public void setNoWarn(boolean nwarn)
  {
    mNwarn = nwarn;
  }
  
  public boolean getNoWarn()
  {
    return mNwarn;
  }
  
  public void setPreserveTime(boolean ptime)
  {
    mPtime = ptime;
  }
  
  public boolean getPreserveTime()
  {
    return mPtime;
  }
  
  public void setKeepCopy(boolean keep)
  {
    mKeep = keep;
  }
  
  public boolean getKeepCopy()
  {
    return mKeep;
  }
  
  public void setIdentical(boolean identical)
  {
    mIdentical = identical;
  }
  
  public boolean getIdentical()
  {
    return mIdentical;
  }
  
  private void getCommentCommand(Commandline cmd)
  {
    if (getComment() != null)
    {
      cmd.createArgument().setValue("-c");
      cmd.createArgument().setValue(getComment());
    }
  }
  
  private void getCommentFileCommand(Commandline cmd)
  {
    if (getCommentFile() != null)
    {
      cmd.createArgument().setValue("-cfile");
      cmd.createArgument().setValue(getCommentFile());
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.clearcase.CCCheckin
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */