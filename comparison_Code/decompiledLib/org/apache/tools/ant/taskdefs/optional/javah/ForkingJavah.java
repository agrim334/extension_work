package org.apache.tools.ant.taskdefs.optional.javah;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.taskdefs.optional.Javah;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.JavaEnvUtils;

public class ForkingJavah
  implements JavahAdapter
{
  public static final String IMPLEMENTATION_NAME = "forking";
  
  public boolean compile(Javah javah)
    throws BuildException
  {
    Commandline cmd = SunJavah.setupJavahCommand(javah);
    Project project = javah.getProject();
    String executable = JavaEnvUtils.getJdkExecutable("javah");
    javah.log("Running " + executable, 3);
    cmd.setExecutable(executable);
    
    String[] args = cmd.getCommandline();
    try
    {
      Execute exe = new Execute(new LogStreamHandler(javah, 2, 1));
      
      exe.setAntRun(project);
      exe.setWorkingDirectory(project.getBaseDir());
      exe.setCommandline(args);
      exe.execute();
      return !exe.isFailure();
    }
    catch (IOException exception)
    {
      throw new BuildException("Error running " + executable + " -maybe it is not on the path", exception);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.javah.ForkingJavah
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */