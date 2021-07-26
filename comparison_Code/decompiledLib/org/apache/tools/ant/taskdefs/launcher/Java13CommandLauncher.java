package org.apache.tools.ant.taskdefs.launcher;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;

public class Java13CommandLauncher
  extends CommandLauncher
{
  public Process exec(Project project, String[] cmd, String[] env, File workingDir)
    throws IOException
  {
    try
    {
      if (project != null) {
        project.log("Execute:Java13CommandLauncher: " + 
          Commandline.describeCommand(cmd), 4);
      }
      return Runtime.getRuntime().exec(cmd, env, workingDir);
    }
    catch (IOException ioex)
    {
      throw ioex;
    }
    catch (Exception exc)
    {
      throw new BuildException("Unable to execute command", exc);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.launcher.Java13CommandLauncher
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */