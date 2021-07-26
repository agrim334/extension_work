package org.apache.tools.ant.taskdefs.optional.clearcase;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;

public class CCUnCheckout
  extends ClearCase
{
  public static final String FLAG_KEEPCOPY = "-keep";
  public static final String FLAG_RM = "-rm";
  private boolean mKeep = false;
  
  public void execute()
    throws BuildException
  {
    Commandline commandLine = new Commandline();
    Project aProj = getProject();
    if (getViewPath() == null) {
      setViewPath(aProj.getBaseDir().getPath());
    }
    commandLine.setExecutable(getClearToolCommand());
    commandLine.createArgument().setValue("uncheckout");
    
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
    if (getKeepCopy()) {
      cmd.createArgument().setValue("-keep");
    } else {
      cmd.createArgument().setValue("-rm");
    }
    cmd.createArgument().setValue(getViewPath());
  }
  
  public void setKeepCopy(boolean keep)
  {
    mKeep = keep;
  }
  
  public boolean getKeepCopy()
  {
    return mKeep;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.clearcase.CCUnCheckout
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */