package org.apache.tools.ant.taskdefs.optional.clearcase;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;

public class CCMkelem
  extends ClearCase
{
  public static final String FLAG_COMMENT = "-c";
  public static final String FLAG_COMMENTFILE = "-cfile";
  public static final String FLAG_NOCOMMENT = "-nc";
  public static final String FLAG_NOWARN = "-nwarn";
  public static final String FLAG_PRESERVETIME = "-ptime";
  public static final String FLAG_NOCHECKOUT = "-nco";
  public static final String FLAG_CHECKIN = "-ci";
  public static final String FLAG_MASTER = "-master";
  public static final String FLAG_ELTYPE = "-eltype";
  private String mComment = null;
  private String mCfile = null;
  private boolean mNwarn = false;
  private boolean mPtime = false;
  private boolean mNoco = false;
  private boolean mCheckin = false;
  private boolean mMaster = false;
  private String mEltype = null;
  
  public void execute()
    throws BuildException
  {
    Commandline commandLine = new Commandline();
    Project aProj = getProject();
    if (getViewPath() == null) {
      setViewPath(aProj.getBaseDir().getPath());
    }
    commandLine.setExecutable(getClearToolCommand());
    commandLine.createArgument().setValue("mkelem");
    
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
    if ((getNoCheckout()) && (getCheckin())) {
      throw new BuildException("Should choose either [nocheckout | checkin]");
    }
    if (getNoCheckout()) {
      cmd.createArgument().setValue("-nco");
    }
    if (getCheckin())
    {
      cmd.createArgument().setValue("-ci");
      if (getPreserveTime()) {
        cmd.createArgument().setValue("-ptime");
      }
    }
    if (getMaster()) {
      cmd.createArgument().setValue("-master");
    }
    if (getEltype() != null) {
      getEltypeCommand(cmd);
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
  
  public void setNoCheckout(boolean co)
  {
    mNoco = co;
  }
  
  public boolean getNoCheckout()
  {
    return mNoco;
  }
  
  public void setCheckin(boolean ci)
  {
    mCheckin = ci;
  }
  
  public boolean getCheckin()
  {
    return mCheckin;
  }
  
  public void setMaster(boolean master)
  {
    mMaster = master;
  }
  
  public boolean getMaster()
  {
    return mMaster;
  }
  
  public void setEltype(String eltype)
  {
    mEltype = eltype;
  }
  
  public String getEltype()
  {
    return mEltype;
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
  
  private void getEltypeCommand(Commandline cmd)
  {
    if (getEltype() != null)
    {
      cmd.createArgument().setValue("-eltype");
      cmd.createArgument().setValue(getEltype());
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.clearcase.CCMkelem
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */