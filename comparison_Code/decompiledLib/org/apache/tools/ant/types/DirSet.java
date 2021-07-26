package org.apache.tools.ant.types;

import java.util.Iterator;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.resources.FileResourceIterator;

public class DirSet
  extends AbstractFileSet
  implements ResourceCollection
{
  public DirSet() {}
  
  protected DirSet(DirSet dirset)
  {
    super(dirset);
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
      getDirectoryScanner().getIncludedDirectories());
  }
  
  public int size()
  {
    if (isReference()) {
      return getRef().size();
    }
    return getDirectoryScanner().getIncludedDirsCount();
  }
  
  public boolean isFilesystemOnly()
  {
    return true;
  }
  
  public String toString()
  {
    return String.join(";", getDirectoryScanner().getIncludedDirectories());
  }
  
  protected AbstractFileSet getRef(Project p)
  {
    return (AbstractFileSet)getCheckedRef(DirSet.class, getDataTypeName(), p);
  }
  
  private DirSet getRef()
  {
    return (DirSet)getCheckedRef(DirSet.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.DirSet
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */