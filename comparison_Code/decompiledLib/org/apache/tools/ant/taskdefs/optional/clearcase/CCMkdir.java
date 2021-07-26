package org.apache.tools.ant.taskdefs.optional.clearcase;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;

public class CCMkdir
  extends ClearCase
{
  public static final String FLAG_COMMENT = "-c";
  public static final String FLAG_COMMENTFILE = "-cfile";
  public static final String FLAG_NOCOMMENT = "-nc";
  public static final String FLAG_NOCHECKOUT = "-nco";
  private String mComment = null;
  private String mCfile = null;
  private boolean mNoco = false;
  
  public void execute()
    throws BuildException
  {
    Commandline commandLine = new Commandline();
    Project aProj = getProject();
    if (getViewPath() == null) {
      setViewPath(aProj.getBaseDir().getPath());
    }
    commandLine.setExecutable(getClearToolCommand());
    commandLine.createArgument().setValue("mkdir");
    
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
    if (getNoCheckout()) {
      cmd.createArgument().setValue("-nco");
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
  
  public void setNoCheckout(boolean co)
  {
    mNoco = co;
  }
  
  public boolean getNoCheckout()
  {
    return mNoco;
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
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.clearcase.CCMkdir
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */