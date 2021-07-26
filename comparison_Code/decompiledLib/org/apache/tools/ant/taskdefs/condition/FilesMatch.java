package org.apache.tools.ant.taskdefs.condition;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;

public class FilesMatch
  implements Condition
{
  private static final FileUtils FILE_UTILS = ;
  private File file1;
  private File file2;
  private boolean textfile = false;
  
  public void setFile1(File file1)
  {
    this.file1 = file1;
  }
  
  public void setFile2(File file2)
  {
    this.file2 = file2;
  }
  
  public void setTextfile(boolean textfile)
  {
    this.textfile = textfile;
  }
  
  public boolean eval()
    throws BuildException
  {
    if ((file1 == null) || (file2 == null)) {
      throw new BuildException("both file1 and file2 are required in filesmatch");
    }
    boolean matches = false;
    try
    {
      matches = FILE_UTILS.contentEquals(file1, file2, textfile);
    }
    catch (IOException ioe)
    {
      throw new BuildException("when comparing files: " + ioe.getMessage(), ioe);
    }
    return matches;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.condition.FilesMatch
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */