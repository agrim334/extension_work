package org.apache.tools.ant.taskdefs.optional.clearcase;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;

public class CCMkattr
  extends ClearCase
{
  public static final String FLAG_REPLACE = "-replace";
  public static final String FLAG_RECURSE = "-recurse";
  public static final String FLAG_VERSION = "-version";
  public static final String FLAG_COMMENT = "-c";
  public static final String FLAG_COMMENTFILE = "-cfile";
  public static final String FLAG_NOCOMMENT = "-nc";
  private boolean mReplace = false;
  private boolean mRecurse = false;
  private String mVersion = null;
  private String mTypeName = null;
  private String mTypeValue = null;
  private String mComment = null;
  private String mCfile = null;
  
  public void execute()
    throws BuildException
  {
    Commandline commandLine = new Commandline();
    Project aProj = getProject();
    if (getTypeName() == null) {
      throw new BuildException("Required attribute TypeName not specified");
    }
    if (getTypeValue() == null) {
      throw new BuildException("Required attribute TypeValue not specified");
    }
    if (getViewPath() == null) {
      setViewPath(aProj.getBaseDir().getPath());
    }
    commandLine.setExecutable(getClearToolCommand());
    commandLine.createArgument().setValue("mkattr");
    
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
    if (getReplace()) {
      cmd.createArgument().setValue("-replace");
    }
    if (getRecurse()) {
      cmd.createArgument().setValue("-recurse");
    }
    if (getVersion() != null) {
      getVersionCommand(cmd);
    }
    if (getComment() != null) {
      getCommentCommand(cmd);
    } else if (getCommentFile() != null) {
      getCommentFileCommand(cmd);
    } else {
      cmd.createArgument().setValue("-nc");
    }
    if (getTypeName() != null) {
      getTypeCommand(cmd);
    }
    if (getTypeValue() != null) {
      getTypeValueCommand(cmd);
    }
    cmd.createArgument().setValue(getViewPath());
  }
  
  public void setReplace(boolean replace)
  {
    mReplace = replace;
  }
  
  public boolean getReplace()
  {
    return mReplace;
  }
  
  public void setRecurse(boolean recurse)
  {
    mRecurse = recurse;
  }
  
  public boolean getRecurse()
  {
    return mRecurse;
  }
  
  public void setVersion(String version)
  {
    mVersion = version;
  }
  
  public String getVersion()
  {
    return mVersion;
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
  
  public void setTypeName(String tn)
  {
    mTypeName = tn;
  }
  
  public String getTypeName()
  {
    return mTypeName;
  }
  
  public void setTypeValue(String tv)
  {
    mTypeValue = tv;
  }
  
  public String getTypeValue()
  {
    return mTypeValue;
  }
  
  private void getVersionCommand(Commandline cmd)
  {
    if (getVersion() != null)
    {
      cmd.createArgument().setValue("-version");
      cmd.createArgument().setValue(getVersion());
    }
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
  
  private void getTypeCommand(Commandline cmd)
  {
    String typenm = getTypeName();
    if (typenm != null) {
      cmd.createArgument().setValue(typenm);
    }
  }
  
  private void getTypeValueCommand(Commandline cmd)
  {
    String typevl = getTypeValue();
    if (typevl != null)
    {
      if (Os.isFamily("windows")) {
        typevl = "\\\"" + typevl + "\\\"";
      } else {
        typevl = "\"" + typevl + "\"";
      }
      cmd.createArgument().setValue(typevl);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.clearcase.CCMkattr
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */