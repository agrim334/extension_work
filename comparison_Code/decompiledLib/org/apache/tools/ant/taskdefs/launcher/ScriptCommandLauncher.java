package org.apache.tools.ant.taskdefs.launcher;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.FileUtils;

public class ScriptCommandLauncher
  extends CommandLauncherProxy
{
  private final String myScript;
  
  public ScriptCommandLauncher(String script, CommandLauncher launcher)
  {
    super(launcher);
    myScript = script;
  }
  
  public Process exec(Project project, String[] cmd, String[] env, File workingDir)
    throws IOException
  {
    if (project == null)
    {
      if (workingDir == null) {
        return exec(project, cmd, env);
      }
      throw new IOException("Cannot locate antRun script: No project provided");
    }
    String antHome = project.getProperty("ant.home");
    if (antHome == null) {
      throw new IOException("Cannot locate antRun script: Property 'ant.home' not found");
    }
    String antRun = FILE_UTILS.resolveFile(project.getBaseDir(), antHome + File.separator + myScript).toString();
    
    File commandDir = workingDir;
    if (workingDir == null) {
      commandDir = project.getBaseDir();
    }
    String[] newcmd = new String[cmd.length + 2];
    newcmd[0] = antRun;
    newcmd[1] = commandDir.getAbsolutePath();
    System.arraycopy(cmd, 0, newcmd, 2, cmd.length);
    
    return exec(project, newcmd, env);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.launcher.ScriptCommandLauncher
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */