package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;

@Deprecated
public class Copydir
  extends MatchingTask
{
  private File srcDir;
  private File destDir;
  private boolean filtering = false;
  private boolean flatten = false;
  private boolean forceOverwrite = false;
  private Map<String, String> filecopyList = new Hashtable();
  
  public void setSrc(File src)
  {
    srcDir = src;
  }
  
  public void setDest(File dest)
  {
    destDir = dest;
  }
  
  public void setFiltering(boolean filter)
  {
    filtering = filter;
  }
  
  public void setFlatten(boolean flatten)
  {
    this.flatten = flatten;
  }
  
  public void setForceoverwrite(boolean force)
  {
    forceOverwrite = force;
  }
  
  public void execute()
    throws BuildException
  {
    log("DEPRECATED - The copydir task is deprecated.  Use copy instead.");
    if (srcDir == null) {
      throw new BuildException("src attribute must be set!", getLocation());
    }
    if (!srcDir.exists()) {
      throw new BuildException("srcdir " + srcDir.toString() + " does not exist!", getLocation());
    }
    if (destDir == null) {
      throw new BuildException("The dest attribute must be set.", getLocation());
    }
    if (srcDir.equals(destDir)) {
      log("Warning: src == dest", 1);
    }
    DirectoryScanner ds = super.getDirectoryScanner(srcDir);
    try
    {
      scanDir(srcDir, destDir, ds.getIncludedFiles());
      if (filecopyList.size() > 0)
      {
        log("Copying " + filecopyList.size() + " file" + (
          filecopyList.size() == 1 ? "" : "s") + " to " + destDir
          .getAbsolutePath());
        for (Map.Entry<String, String> e : filecopyList.entrySet())
        {
          String fromFile = (String)e.getKey();
          String toFile = (String)e.getValue();
          try
          {
            getProject().copyFile(fromFile, toFile, filtering, forceOverwrite);
          }
          catch (IOException ioe)
          {
            String msg = "Failed to copy " + fromFile + " to " + toFile + " due to " + ioe.getMessage();
            throw new BuildException(msg, ioe, getLocation());
          }
        }
      }
    }
    finally
    {
      filecopyList.clear();
    }
  }
  
  private void scanDir(File from, File to, String[] files)
  {
    for (String filename : files)
    {
      File srcFile = new File(from, filename);
      File destFile;
      File destFile;
      if (flatten) {
        destFile = new File(to, new File(filename).getName());
      } else {
        destFile = new File(to, filename);
      }
      if ((forceOverwrite) || 
        (srcFile.lastModified() > destFile.lastModified())) {
        filecopyList.put(srcFile.getAbsolutePath(), destFile
          .getAbsolutePath());
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Copydir
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */