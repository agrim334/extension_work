package org.apache.tools.ant.taskdefs.optional.clearcase;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;

public class CCMkbl
  extends ClearCase
{
  public static final String FLAG_COMMENT = "-c";
  public static final String FLAG_COMMENTFILE = "-cfile";
  public static final String FLAG_NOCOMMENT = "-nc";
  public static final String FLAG_IDENTICAL = "-identical";
  public static final String FLAG_INCREMENTAL = "-incremental";
  public static final String FLAG_FULL = "-full";
  public static final String FLAG_NLABEL = "-nlabel";
  private String mComment = null;
  private String mCfile = null;
  private String mBaselineRootName = null;
  private boolean mNwarn = false;
  private boolean mIdentical = true;
  private boolean mFull = false;
  private boolean mNlabel = false;
  
  public void execute()
    throws BuildException
  {
    Commandline commandLine = new Commandline();
    Project aProj = getProject();
    if (getViewPath() == null) {
      setViewPath(aProj.getBaseDir().getPath());
    }
    commandLine.setExecutable(getClearToolCommand());
    commandLine.createArgument().setValue("mkbl");
    
    checkOptions(commandLine);
    if (!getFailOnErr()) {
      getProject().log("Ignoring any errors that occur for: " + 
        getBaselineRootName(), 3);
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
    if (getIdentical()) {
      cmd.createArgument().setValue("-identical");
    }
    if (getFull()) {
      cmd.createArgument().setValue("-full");
    } else {
      cmd.createArgument().setValue("-incremental");
    }
    if (getNlabel()) {
      cmd.createArgument().setValue("-nlabel");
    }
    cmd.createArgument().setValue(getBaselineRootName());
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
  
  public void setBaselineRootName(String baselineRootName)
  {
    mBaselineRootName = baselineRootName;
  }
  
  public String getBaselineRootName()
  {
    return mBaselineRootName;
  }
  
  public void setNoWarn(boolean nwarn)
  {
    mNwarn = nwarn;
  }
  
  public boolean getNoWarn()
  {
    return mNwarn;
  }
  
  public void setIdentical(boolean identical)
  {
    mIdentical = identical;
  }
  
  public boolean getIdentical()
  {
    return mIdentical;
  }
  
  public void setFull(boolean full)
  {
    mFull = full;
  }
  
  public boolean getFull()
  {
    return mFull;
  }
  
  public void setNlabel(boolean nlabel)
  {
    mNlabel = nlabel;
  }
  
  public boolean getNlabel()
  {
    return mNlabel;
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
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.clearcase.CCMkbl
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */