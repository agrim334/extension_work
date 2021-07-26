package org.apache.tools.ant.taskdefs.launcher;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.apache.tools.ant.Project;

public class MacCommandLauncher
  extends CommandLauncherProxy
{
  public MacCommandLauncher(CommandLauncher launcher)
  {
    super(launcher);
  }
  
  public Process exec(Project project, String[] cmd, String[] env, File workingDir)
    throws IOException
  {
    if (workingDir == null) {
      return exec(project, cmd, env);
    }
    System.getProperties().put("user.dir", workingDir.getAbsolutePath());
    try
    {
      return exec(project, cmd, env);
    }
    finally
    {
      System.getProperties().put("user.dir", System.getProperty("user.dir"));
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.launcher.MacCommandLauncher
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */