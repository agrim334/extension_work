package org.apache.tools.ant.types;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.resources.FileResourceIterator;

public class FileList
  extends DataType
  implements ResourceCollection
{
  private List<String> filenames = new ArrayList();
  private File dir;
  
  public FileList() {}
  
  protected FileList(FileList filelist)
  {
    dir = dir;
    filenames = filenames;
    setProject(filelist.getProject());
  }
  
  public void setRefid(Reference r)
    throws BuildException
  {
    if ((dir != null) || (!filenames.isEmpty())) {
      throw tooManyAttributes();
    }
    super.setRefid(r);
  }
  
  public void setDir(File dir)
    throws BuildException
  {
    checkAttributesAllowed();
    this.dir = dir;
  }
  
  public File getDir(Project p)
  {
    if (isReference()) {
      return getRef(p).getDir(p);
    }
    return dir;
  }
  
  public void setFiles(String filenames)
  {
    checkAttributesAllowed();
    if ((filenames != null) && (!filenames.isEmpty()))
    {
      StringTokenizer tok = new StringTokenizer(filenames, ", \t\n\r\f", false);
      while (tok.hasMoreTokens()) {
        this.filenames.add(tok.nextToken());
      }
    }
  }
  
  public String[] getFiles(Project p)
  {
    if (isReference()) {
      return getRef(p).getFiles(p);
    }
    if (dir == null) {
      throw new BuildException("No directory specified for filelist.");
    }
    if (filenames.isEmpty()) {
      throw new BuildException("No files specified for filelist.");
    }
    return (String[])filenames.toArray(new String[filenames.size()]);
  }
  
  public static class FileName
  {
    private String name;
    
    public void setName(String name)
    {
      this.name = name;
    }
    
    public String getName()
    {
      return name;
    }
  }
  
  public void addConfiguredFile(FileName name)
  {
    if (name.getName() == null) {
      throw new BuildException("No name specified in nested file element");
    }
    filenames.add(name.getName());
  }
  
  public Iterator<Resource> iterator()
  {
    if (isReference()) {
      return getRef().iterator();
    }
    return new FileResourceIterator(getProject(), dir, 
      (String[])filenames.toArray(new String[filenames.size()]));
  }
  
  public int size()
  {
    if (isReference()) {
      return getRef().size();
    }
    return filenames.size();
  }
  
  public boolean isFilesystemOnly()
  {
    return true;
  }
  
  private FileList getRef()
  {
    return (FileList)getCheckedRef(FileList.class);
  }
  
  private FileList getRef(Project p)
  {
    return (FileList)getCheckedRef(FileList.class, getDataTypeName(), p);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.FileList
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */