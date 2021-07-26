package org.apache.tools.ant.types;

import java.util.Iterator;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.resources.FileResourceIterator;

public class FileSet
  extends AbstractFileSet
  implements ResourceCollection
{
  public FileSet() {}
  
  protected FileSet(FileSet fileset)
  {
    super(fileset);
  }
  
  public Object clone()
  {
    if (isReference()) {
      return getRef().clone();
    }
    return super.clone();
  }
  
  public Iterator<Resource> iterator()
  {
    if (isReference()) {
      return getRef().iterator();
    }
    return new FileResourceIterator(getProject(), getDir(getProject()), 
      getDirectoryScanner().getIncludedFiles());
  }
  
  public int size()
  {
    if (isReference()) {
      return getRef().size();
    }
    return getDirectoryScanner().getIncludedFilesCount();
  }
  
  public boolean isFilesystemOnly()
  {
    return true;
  }
  
  protected AbstractFileSet getRef(Project p)
  {
    return (AbstractFileSet)getCheckedRef(FileSet.class, getDataTypeName(), p);
  }
  
  private FileSet getRef()
  {
    return (FileSet)getCheckedRef(FileSet.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.FileSet
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */