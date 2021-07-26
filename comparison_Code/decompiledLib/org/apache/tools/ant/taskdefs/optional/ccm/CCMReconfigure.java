package org.apache.tools.ant.taskdefs.optional.ccm;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;

public class CCMReconfigure
  extends Continuus
{
  public static final String FLAG_RECURSE = "/recurse";
  public static final String FLAG_VERBOSE = "/verbose";
  public static final String FLAG_PROJECT = "/project";
  private String ccmProject = null;
  private boolean recurse = false;
  private boolean verbose = false;
  
  public CCMReconfigure()
  {
    setCcmAction("reconfigure");
  }
  
  public void execute()
    throws BuildException
  {
    Commandline commandLine = new Commandline();
    
    commandLine.setExecutable(getCcmCommand());
    commandLine.createArgument().setValue(getCcmAction());
    
    checkOptions(commandLine);
    
    int result = run(commandLine);
    if (Execute.isFailure(result)) {
      throw new BuildException("Failed executing: " + commandLine, getLocation());
    }
  }
  
  private void checkOptions(Commandline cmd)
  {
    if (isRecurse()) {
      cmd.createArgument().setValue("/recurse");
    }
    if (isVerbose()) {
      cmd.createArgument().setValue("/verbose");
    }
    if (getCcmProject() != null)
    {
      cmd.createArgument().setValue("/project");
      cmd.createArgument().setValue(getCcmProject());
    }
  }
  
  public String getCcmProject()
  {
    return ccmProject;
  }
  
  public void setCcmProject(String v)
  {
    ccmProject = v;
  }
  
  public boolean isRecurse()
  {
    return recurse;
  }
  
  public void setRecurse(boolean v)
  {
    recurse = v;
  }
  
  public boolean isVerbose()
  {
    return verbose;
  }
  
  public void setVerbose(boolean v)
  {
    verbose = v;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ccm.CCMReconfigure
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */