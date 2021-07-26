package org.apache.tools.ant.taskdefs.rmic;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.taskdefs.Rmic;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.JavaEnvUtils;

public class ForkingSunRmic
  extends DefaultRmicAdapter
{
  public static final String COMPILER_NAME = "forking";
  
  protected boolean areIiopAndIdlSupported()
  {
    boolean supported = !JavaEnvUtils.isAtLeastJavaVersion("11");
    if ((!supported) && (getRmic().getExecutable() != null))
    {
      getRmic().getProject().log("Allowing -iiop and -idl for forked rmic even though this version of Java doesn't support it.", 2);
      
      return true;
    }
    return supported;
  }
  
  public boolean execute()
    throws BuildException
  {
    Rmic owner = getRmic();
    Commandline cmd = setupRmicCommand();
    Project project = owner.getProject();
    String executable = owner.getExecutable();
    if (executable == null)
    {
      if (JavaEnvUtils.isAtLeastJavaVersion("15")) {
        throw new BuildException("rmic does not exist under Java 15 and higher, use rmic of an older JDK and explicitly set the executable attribute");
      }
      executable = JavaEnvUtils.getJdkExecutable(getExecutableName());
    }
    cmd.setExecutable(executable);
    
    String[] args = cmd.getCommandline();
    try
    {
      Execute exe = new Execute(new LogStreamHandler(owner, 2, 1));
      
      exe.setAntRun(project);
      exe.setWorkingDirectory(project.getBaseDir());
      exe.setCommandline(args);
      exe.execute();
      return !exe.isFailure();
    }
    catch (IOException exception)
    {
      throw new BuildException("Error running " + getExecutableName() + " -maybe it is not on the path", exception);
    }
  }
  
  protected String getExecutableName()
  {
    return "rmic";
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.rmic.ForkingSunRmic
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */