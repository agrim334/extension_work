package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;

public class CCRmtype
  extends ClearCase
{
  public static final String FLAG_IGNORE = "-ignore";
  public static final String FLAG_RMALL = "-rmall";
  public static final String FLAG_FORCE = "-force";
  public static final String FLAG_COMMENT = "-c";
  public static final String FLAG_COMMENTFILE = "-cfile";
  public static final String FLAG_NOCOMMENT = "-nc";
  private String mTypeKind = null;
  private String mTypeName = null;
  private String mVOB = null;
  private String mComment = null;
  private String mCfile = null;
  private boolean mRmall = false;
  private boolean mIgnore = false;
  
  public void execute()
    throws BuildException
  {
    Commandline commandLine = new Commandline();
    if (getTypeKind() == null) {
      throw new BuildException("Required attribute TypeKind not specified");
    }
    if (getTypeName() == null) {
      throw new BuildException("Required attribute TypeName not specified");
    }
    commandLine.setExecutable(getClearToolCommand());
    commandLine.createArgument().setValue("rmtype");
    
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
    if (getIgnore()) {
      cmd.createArgument().setValue("-ignore");
    }
    if (getRmAll())
    {
      cmd.createArgument().setValue("-rmall");
      cmd.createArgument().setValue("-force");
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
  
  public void setIgnore(boolean ignore)
  {
    mIgnore = ignore;
  }
  
  public boolean getIgnore()
  {
    return mIgnore;
  }
  
  public void setRmAll(boolean rmall)
  {
    mRmall = rmall;
  }
  
  public boolean getRmAll()
  {
    return mRmall;
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
  
  public void setTypeKind(String tk)
  {
    mTypeKind = tk;
  }
  
  public String getTypeKind()
  {
    return mTypeKind;
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
  
  private String getTypeSpecifier()
  {
    String tkind = getTypeKind();
    String tname = getTypeName();
    
    String typeSpec = tkind + ":" + tname;
    if (getVOB() != null) {
      typeSpec = typeSpec + "@" + getVOB();
    }
    return typeSpec;
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
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.clearcase.CCRmtype
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */