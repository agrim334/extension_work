package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;

public class CCMklbtype
  extends ClearCase
{
  public static final String FLAG_REPLACE = "-replace";
  public static final String FLAG_GLOBAL = "-global";
  public static final String FLAG_ORDINARY = "-ordinary";
  public static final String FLAG_PBRANCH = "-pbranch";
  public static final String FLAG_SHARED = "-shared";
  public static final String FLAG_COMMENT = "-c";
  public static final String FLAG_COMMENTFILE = "-cfile";
  public static final String FLAG_NOCOMMENT = "-nc";
  private String mTypeName = null;
  private String mVOB = null;
  private String mComment = null;
  private String mCfile = null;
  private boolean mReplace = false;
  private boolean mGlobal = false;
  private boolean mOrdinary = true;
  private boolean mPbranch = false;
  private boolean mShared = false;
  
  public void execute()
    throws BuildException
  {
    Commandline commandLine = new Commandline();
    if (getTypeName() == null) {
      throw new BuildException("Required attribute TypeName not specified");
    }
    commandLine.setExecutable(getClearToolCommand());
    commandLine.createArgument().setValue("mklbtype");
    
    checkOptions(commandLine);
    if (!getFailOnErr()) {
      getProject().log("Ignoring any errors that occur for: " + 
        getTypeSpecifier(), 3);
    }
    int result = run(commandLine);
    if ((Execute.isFailure(result)) && (getFailOnErr())) {
      throw new BuildException("Failed executing: " + commandLine, getLocation());
    }
  }
  
  private void checkOptions(Commandline cmd)
  {
    if (getReplace()) {
      cmd.createArgument().setValue("-replace");
    }
    if (getOrdinary()) {
      cmd.createArgument().setValue("-ordinary");
    } else if (getGlobal()) {
      cmd.createArgument().setValue("-global");
    }
    if (getPbranch()) {
      cmd.createArgument().setValue("-pbranch");
    }
    if (getShared()) {
      cmd.createArgument().setValue("-shared");
    }
    if (getComment() != null) {
      getCommentCommand(cmd);
    } else if (getCommentFile() != null) {
      getCommentFileCommand(cmd);
    } else {
      cmd.createArgument().setValue("-nc");
    }
    cmd.createArgument().setValue(getTypeSpecifier());
  }
  
  public void setTypeName(String tn)
  {
    mTypeName = tn;
  }
  
  public String getTypeName()
  {
    return mTypeName;
  }
  
  public void setVOB(String vob)
  {
    mVOB = vob;
  }
  
  public String getVOB()
  {
    return mVOB;
  }
  
  public void setReplace(boolean repl)
  {
    mReplace = repl;
  }
  
  public boolean getReplace()
  {
    return mReplace;
  }
  
  public void setGlobal(boolean glob)
  {
    mGlobal = glob;
  }
  
  public boolean getGlobal()
  {
    return mGlobal;
  }
  
  public void setOrdinary(boolean ordinary)
  {
    mOrdinary = ordinary;
  }
  
  public boolean getOrdinary()
  {
    return mOrdinary;
  }
  
  public void setPbranch(boolean pbranch)
  {
    mPbranch = pbranch;
  }
  
  public boolean getPbranch()
  {
    return mPbranch;
  }
  
  public void setShared(boolean shared)
  {
    mShared = shared;
  }
  
  public boolean getShared()
  {
    return mShared;
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
  
  private String getTypeSpecifier()
  {
    String typenm = getTypeName();
    if (getVOB() != null) {
      typenm = typenm + "@" + getVOB();
    }
    return typenm;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.clearcase.CCMklbtype
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */