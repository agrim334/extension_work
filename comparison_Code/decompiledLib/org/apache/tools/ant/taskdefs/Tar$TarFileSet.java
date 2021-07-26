package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.TarFileSet;

public class Tar$TarFileSet
  extends TarFileSet
{
  private String[] files = null;
  private boolean preserveLeadingSlashes = false;
  
  public Tar$TarFileSet(FileSet fileset)
  {
    super(fileset);
  }
  
  public Tar$TarFileSet() {}
  
  public String[] getFiles(Project p)
  {
    if (files == null) {
      files = Tar.getFileNames(this);
    }
    return files;
  }
  
  public void setMode(String octalString)
  {
    setFileMode(octalString);
  }
  
  public int getMode()
  {
    return getFileMode(getProject());
  }
  
  public void setPreserveLeadingSlashes(boolean b)
  {
    preserveLeadingSlashes = b;
  }
  
  public boolean getPreserveLeadingSlashes()
  {
    return preserveLeadingSlashes;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Tar.TarFileSet
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */