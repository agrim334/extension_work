package org.apache.tools.ant.types.resources;

import java.util.Iterator;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;

public class BCFileSet
  extends FileSet
{
  public BCFileSet() {}
  
  public BCFileSet(FileSet fs)
  {
    super(fs);
  }
  
  public Iterator<Resource> iterator()
  {
    if (isReference()) {
      return getRef().iterator();
    }
    FileResourceIterator result = new FileResourceIterator(getProject(), getDir());
    result.addFiles(getDirectoryScanner().getIncludedFiles());
    result.addFiles(getDirectoryScanner().getIncludedDirectories());
    return result;
  }
  
  public int size()
  {
    if (isReference()) {
      return getRef().size();
    }
    return 
      getDirectoryScanner().getIncludedFilesCount() + getDirectoryScanner().getIncludedDirsCount();
  }
  
  private FileSet getRef()
  {
    return (FileSet)getCheckedRef(FileSet.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.BCFileSet
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */