package org.apache.tools.ant.types.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.AbstractFileSet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

public class MultiRootFileSet
  extends AbstractFileSet
  implements ResourceCollection
{
  private SetType type = SetType.file;
  private boolean cache = true;
  private List<File> baseDirs = new ArrayList();
  private Union union;
  
  public void setDir(File dir)
  {
    throw new BuildException(getDataTypeName() + " doesn't support the dir attribute");
  }
  
  public void setType(SetType type)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    this.type = type;
  }
  
  public synchronized void setCache(boolean b)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    cache = b;
  }
  
  public void setBaseDirs(String dirs)
  {
    if (isReference()) {
      throw tooManyAttributes();
    }
    if ((dirs != null) && (!dirs.isEmpty())) {
      for (String d : dirs.split(",")) {
        baseDirs.add(getProject().resolveFile(d));
      }
    }
  }
  
  public void addConfiguredBaseDir(FileResource r)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    baseDirs.add(r.getFile());
  }
  
  public void setRefid(Reference r)
  {
    if (!baseDirs.isEmpty()) {
      throw tooManyAttributes();
    }
    super.setRefid(r);
  }
  
  public Object clone()
  {
    if (isReference()) {
      return getRef().clone();
    }
    MultiRootFileSet fs = (MultiRootFileSet)super.clone();
    baseDirs = new ArrayList(baseDirs);
    union = null;
    return fs;
  }
  
  public Iterator<Resource> iterator()
  {
    if (isReference()) {
      return getRef().iterator();
    }
    return merge().iterator();
  }
  
  public int size()
  {
    if (isReference()) {
      return getRef().size();
    }
    return merge().size();
  }
  
  public boolean isFilesystemOnly()
  {
    return true;
  }
  
  public String toString()
  {
    if (isReference()) {
      return getRef().toString();
    }
    return merge().toString();
  }
  
  private MultiRootFileSet getRef()
  {
    return (MultiRootFileSet)getCheckedRef(MultiRootFileSet.class);
  }
  
  private synchronized Union merge()
  {
    if ((cache) && (union != null)) {
      return union;
    }
    Union u = new Union();
    setup(u);
    if (cache) {
      union = u;
    }
    return u;
  }
  
  private void setup(Union u)
  {
    for (File d : baseDirs) {
      u.add(new Worker(this, type, d, null));
    }
  }
  
  public static enum SetType
  {
    file,  dir,  both;
    
    private SetType() {}
  }
  
  private static class Worker
    extends AbstractFileSet
    implements ResourceCollection
  {
    private final MultiRootFileSet.SetType type;
    
    private Worker(MultiRootFileSet fs, MultiRootFileSet.SetType type, File dir)
    {
      super();
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
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.MultiRootFileSet
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */