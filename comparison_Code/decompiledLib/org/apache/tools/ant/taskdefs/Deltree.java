package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

@Deprecated
public class Deltree
  extends Task
{
  private File dir;
  
  public void setDir(File dir)
  {
    this.dir = dir;
  }
  
  public void execute()
    throws BuildException
  {
    log("DEPRECATED - The deltree task is deprecated.  Use delete instead.");
    if (dir == null) {
      throw new BuildException("dir attribute must be set!", getLocation());
    }
    if (dir.exists())
    {
      if (!dir.isDirectory())
      {
        if (!dir.delete()) {
          throw new BuildException("Unable to delete directory " + dir.getAbsolutePath(), getLocation());
        }
        return;
      }
      log("Deleting: " + dir.getAbsolutePath());
      
      removeDir(dir);
    }
  }
  
  private void removeDir(File dir)
  {
    for (String s : dir.list())
    {
      File f = new File(dir, s);
      if (f.isDirectory()) {
        removeDir(f);
      } else if (!f.delete()) {
        throw new BuildException("Unable to delete file " + f.getAbsolutePath());
      }
    }
    if (!dir.delete()) {
      throw new BuildException("Unable to delete directory " + dir.getAbsolutePath());
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Deltree
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */