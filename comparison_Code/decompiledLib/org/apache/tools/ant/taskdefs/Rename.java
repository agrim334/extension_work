package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;

@Deprecated
public class Rename
  extends Task
{
  private static final FileUtils FILE_UTILS = ;
  private File src;
  private File dest;
  private boolean replace = true;
  
  public void setSrc(File src)
  {
    this.src = src;
  }
  
  public void setDest(File dest)
  {
    this.dest = dest;
  }
  
  public void setReplace(String replace)
  {
    this.replace = Project.toBoolean(replace);
  }
  
  public void execute()
    throws BuildException
  {
    log("DEPRECATED - The rename task is deprecated.  Use move instead.");
    if (dest == null) {
      throw new BuildException("dest attribute is required", getLocation());
    }
    if (src == null) {
      throw new BuildException("src attribute is required", getLocation());
    }
    if ((!replace) && (dest.exists())) {
      throw new BuildException(dest + " already exists.");
    }
    try
    {
      FILE_UTILS.rename(src, dest);
    }
    catch (IOException e)
    {
      throw new BuildException("Unable to rename " + src + " to " + dest, e, getLocation());
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Rename
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */