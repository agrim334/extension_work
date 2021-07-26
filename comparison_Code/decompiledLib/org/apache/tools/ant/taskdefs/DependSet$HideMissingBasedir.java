package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.Iterator;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Resources;

final class DependSet$HideMissingBasedir
  implements ResourceCollection
{
  private FileSet fs;
  
  private DependSet$HideMissingBasedir(FileSet fs)
  {
    this.fs = fs;
  }
  
  public Iterator<Resource> iterator()
  {
    return basedirExists() ? fs.iterator() : Resources.EMPTY_ITERATOR;
  }
  
  public int size()
  {
    return basedirExists() ? fs.size() : 0;
  }
  
  public boolean isFilesystemOnly()
  {
    return true;
  }
  
  private boolean basedirExists()
  {
    File basedir = fs.getDir();
    
    return (basedir == null) || (basedir.exists());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.DependSet.HideMissingBasedir
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */