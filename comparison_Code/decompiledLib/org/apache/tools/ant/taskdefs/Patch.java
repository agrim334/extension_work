package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;

public class Patch
  extends Task
{
  private File originalFile;
  private File directory;
  private boolean havePatchfile = false;
  private Commandline cmd = new Commandline();
  private boolean failOnError = false;
  private static final String PATCH = "patch";
  
  public void setOriginalfile(File file)
  {
    originalFile = file;
  }
  
  public void setDestfile(File file)
  {
    if (file != null)
    {
      cmd.createArgument().setValue("-o");
      cmd.createArgument().setFile(file);
    }
  }
  
  public void setPatchfile(File file)
  {
    if (!file.exists()) {
      throw new BuildException("patchfile " + file + " doesn't exist", getLocation());
    }
    cmd.createArgument().setValue("-i");
    cmd.createArgument().setFile(file);
    havePatchfile = true;
  }
  
  public void setBackups(boolean backups)
  {
    if (backups) {
      cmd.createArgument().setValue("-b");
    }
  }
  
  public void setIgnorewhitespace(boolean ignore)
  {
    if (ignore) {
      cmd.createArgument().setValue("-l");
    }
  }
  
  public void setStrip(int num)
    throws BuildException
  {
    if (num < 0) {
      throw new BuildException("strip has to be >= 0", getLocation());
    }
    cmd.createArgument().setValue("-p" + num);
  }
  
  public void setQuiet(boolean q)
  {
    if (q) {
      cmd.createArgument().setValue("-s");
    }
  }
  
  public void setReverse(boolean r)
  {
    if (r) {
      cmd.createArgument().setValue("-R");
    }
  }
  
  public void setDir(File directory)
  {
    this.directory = directory;
  }
  
  public void setFailOnError(boolean value)
  {
    failOnError = value;
  }
  
  public void execute()
    throws BuildException
  {
    if (!havePatchfile) {
      throw new BuildException("patchfile argument is required", getLocation());
    }
    Commandline toExecute = (Commandline)cmd.clone();
    toExecute.setExecutable("patch");
    if (originalFile != null) {
      toExecute.createArgument().setFile(originalFile);
    }
    Execute exe = new Execute(new LogStreamHandler(this, 2, 1), null);
    
    exe.setCommandline(toExecute.getCommandline());
    if (directory == null)
    {
      exe.setWorkingDirectory(getProject().getBaseDir());
    }
    else
    {
      if (!directory.isDirectory()) {
        throw new BuildException(directory + " is not a directory.", getLocation());
      }
      exe.setWorkingDirectory(directory);
    }
    log(toExecute.describeCommand(), 3);
    try
    {
      int returncode = exe.execute();
      if (Execute.isFailure(returncode))
      {
        String msg = "'patch' failed with exit code " + returncode;
        if (failOnError) {
          throw new BuildException(msg);
        }
        log(msg, 0);
      }
    }
    catch (IOException e)
    {
      throw new BuildException(e, getLocation());
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Patch
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */