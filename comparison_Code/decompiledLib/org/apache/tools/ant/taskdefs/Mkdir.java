package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class Mkdir
  extends Task
{
  private static final int MKDIR_RETRY_SLEEP_MILLIS = 10;
  private File dir;
  
  public void execute()
    throws BuildException
  {
    if (dir == null) {
      throw new BuildException("dir attribute is required", getLocation());
    }
    if (dir.isFile()) {
      throw new BuildException("Unable to create directory as a file already exists with that name: %s", new Object[] {dir.getAbsolutePath() });
    }
    if (!dir.exists())
    {
      boolean result = mkdirs(dir);
      if (!result)
      {
        if (dir.exists())
        {
          log("A different process or task has already created dir " + dir
            .getAbsolutePath(), 3);
          return;
        }
        throw new BuildException("Directory " + dir.getAbsolutePath() + " creation was not successful for an unknown reason", getLocation());
      }
      log("Created dir: " + dir.getAbsolutePath());
    }
    else
    {
      log("Skipping " + dir.getAbsolutePath() + " because it already exists.", 3);
    }
  }
  
  public void setDir(File dir)
  {
    this.dir = dir;
  }
  
  public File getDir()
  {
    return dir;
  }
  
  private boolean mkdirs(File f)
  {
    if (!f.mkdirs()) {
      try
      {
        Thread.sleep(10L);
        return f.mkdirs();
      }
      catch (InterruptedException ex)
      {
        return f.mkdirs();
      }
    }
    return true;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Mkdir
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */