package org.apache.tools.ant.types.resources;

import java.io.File;
import java.util.Iterator;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.AbstractFileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

class MultiRootFileSet$Worker
  extends AbstractFileSet
  implements ResourceCollection
{
  private final MultiRootFileSet.SetType type;
  
  private MultiRootFileSet$Worker(MultiRootFileSet fs, MultiRootFileSet.SetType type, File dir)
  {
    super(fs);
    this.type = type;
    setDir(dir);
  }
  
  public boolean isFilesystemOnly()
  {
    return true;
  }
  
  public Iterator<Resource> iterator()
  {
    DirectoryScanner ds = getDirectoryScanner();
    
    String[] names = type == MultiRootFileSet.SetType.file ? ds.getIncludedFiles() : ds.getIncludedDirectories();
    if (type == MultiRootFileSet.SetType.both)
    {
      String[] files = ds.getIncludedFiles();
      String[] merged = new String[names.length + files.length];
      System.arraycopy(names, 0, merged, 0, names.length);
      System.arraycopy(files, 0, merged, names.length, files.length);
      names = merged;
    }
    return new FileResourceIterator(getProject(), getDir(getProject()), names);
  }
  
  public int size()
  {
    DirectoryScanner ds = getDirectoryScanner();
    
    int count = type == MultiRootFileSet.SetType.file ? ds.getIncludedFilesCount() : ds.getIncludedDirsCount();
    if (type == MultiRootFileSet.SetType.both) {
      count += ds.getIncludedFilesCount();
    }
    return count;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.MultiRootFileSet.Worker
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */