package org.apache.tools.ant.taskdefs.launcher;

import java.io.IOException;
import org.apache.tools.ant.Project;

public class CommandLauncherProxy
  extends CommandLauncher
{
  private final CommandLauncher myLauncher;
  
  protected CommandLauncherProxy(CommandLauncher launcher)
  {
    myLauncher = launcher;
  }
  
  public Process exec(Project project, String[] cmd, String[] env)
    throws IOException
  {
    return myLauncher.exec(project, cmd, env);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.launcher.CommandLauncherProxy
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */