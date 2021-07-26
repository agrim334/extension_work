package org.apache.tools.ant.types.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceFactory;
import org.apache.tools.ant.util.FileUtils;

public class FileResource
  extends Resource
  implements Touchable, FileProvider, ResourceFactory, Appendable
{
  private static final FileUtils FILE_UTILS = ;
  private static final int NULL_FILE = Resource.getMagicNumber("null file".getBytes());
  private File file;
  private File baseDir;
  
  public FileResource() {}
  
  public FileResource(File b, String name)
  {
    baseDir = b;
    file = FILE_UTILS.resolveFile(b, name);
  }
  
  public FileResource(File f)
  {
    setFile(f);
  }
  
  public FileResource(Project p, File f)
  {
    this(f);
    setProject(p);
  }
  
  public FileResource(Project p, String s)
  {
    this(p, p.resolveFile(s));
  }
  
  public void setFile(File f)
  {
    checkAttributesAllowed();
    file = f;
    if ((f != null) && ((getBaseDir() == null) || (!FILE_UTILS.isLeadingPath(getBaseDir(), f)))) {
      setBaseDir(f.getParentFile());
    }
  }
  
  public File getFile()
  {
    if (isReference()) {
      return getRef().getFile();
    }
    dieOnCircularReference();
    synchronized (this)
    {
      if (file == null)
      {
        File d = getBaseDir();
        String n = super.getName();
        if (n != null) {
          setFile(FILE_UTILS.resolveFile(d, n));
        }
      }
    }
    return file;
  }
  
  public void setBaseDir(File b)
  {
    checkAttributesAllowed();
    baseDir = b;
  }
  
  public File getBaseDir()
  {
    if (isReference()) {
      return getRef().getBaseDir();
    }
    dieOnCircularReference();
    return baseDir;
  }
  
  public void setRefid(Reference r)
  {
    if ((file != null) || (baseDir != null)) {
      throw tooManyAttributes();
    }
    super.setRefid(r);
  }
  
  public String getName()
  {
    if (isReference()) {
      return getRef().getName();
    }
    File b = getBaseDir();
    return b == null ? getNotNullFile().getName() : 
      FILE_UTILS.removeLeadingPath(b, getNotNullFile());
  }
  
  public boolean isExists()
  {
    return isReference() ? getRef().isExists() : 
      getNotNullFile().exists();
  }
  
  public long getLastModified()
  {
    return isReference() ? 
      getRef().getLastModified() : 
      getNotNullFile().lastModified();
  }
  
  public boolean isDirectory()
  {
    return isReference() ? getRef().isDirectory() : 
      getNotNullFile().isDirectory();
  }
  
  public long getSize()
  {
    return isReference() ? getRef().getSize() : 
      getNotNullFile().length();
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    return isReference() ? getRef().getInputStream() : 
      Files.newInputStream(getNotNullFile().toPath(), new OpenOption[0]);
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    if (isReference()) {
      return getRef().getOutputStream();
    }
    return getOutputStream(false);
  }
  
  public OutputStream getAppendOutputStream()
    throws IOException
  {
    if (isReference()) {
      return getRef().getAppendOutputStream();
    }
    return getOutputStream(true);
  }
  
  private OutputStream getOutputStream(boolean append)
    throws IOException
  {
    File f = getNotNullFile();
    if (f.exists())
    {
      if ((f.isFile()) && (!append)) {
        f.delete();
      }
    }
    else
    {
      File p = f.getParentFile();
      if ((p != null) && (!p.exists())) {
        p.mkdirs();
      }
    }
    return FileUtils.newOutputStream(f.toPath(), append);
  }
  
  public int compareTo(Resource another)
  {
    if (isReference()) {
      return getRef().compareTo(another);
    }
    if (equals(another)) {
      return 0;
    }
    FileProvider otherFP = (FileProvider)another.as(FileProvider.class);
    if (otherFP != null)
    {
      File f = getFile();
      if (f == null) {
        return -1;
      }
      File of = otherFP.getFile();
      if (of == null) {
        return 1;
      }
      int compareFiles = f.compareTo(of);
      return compareFiles != 0 ? compareFiles : 
        getName().compareTo(another.getName());
    }
    return super.compareTo(another);
  }
  
  public boolean equals(Object another)
  {
    if (this == another) {
      return true;
    }
    if (isReference()) {
      return getRef().equals(another);
    }
    if ((another == null) || (!another.getClass().equals(getClass()))) {
      return false;
    }
    FileResource otherfr = (FileResource)another;
    return 
      otherfr.getFile() == null;
  }
  
  public int hashCode()
  {
    if (isReference()) {
      return getRef().hashCode();
    }
    return MAGIC * (getFile() == null ? NULL_FILE : getFile().hashCode());
  }
  
  public String toString()
  {
    if (isReference()) {
      return getRef().toString();
    }
    if (file == null) {
      return "(unbound file resource)";
    }
    String absolutePath = file.getAbsolutePath();
    return FILE_UTILS.normalize(absolutePath).getAbsolutePath();
  }
  
  public boolean isFilesystemOnly()
  {
    if (isReference()) {
      return getRef().isFilesystemOnly();
    }
    dieOnCircularReference();
    return true;
  }
  
  public void touch(long modTime)
  {
    if (isReference())
    {
      getRef().touch(modTime);
      return;
    }
    if (!getNotNullFile().setLastModified(modTime)) {
      log("Failed to change file modification time", 1);
    }
  }
  
  protected File getNotNullFile()
  {
    if (getFile() == null) {
      throw new BuildException("file attribute is null!");
    }
    dieOnCircularReference();
    return getFile();
  }
  
  public Resource getResource(String path)
  {
    File newfile = FILE_UTILS.resolveFile(getFile(), path);
    FileResource fileResource = new FileResource(newfile);
    if (FILE_UTILS.isLeadingPath(getBaseDir(), newfile)) {
      fileResource.setBaseDir(getBaseDir());
    }
    return fileResource;
  }
  
  protected FileResource getRef()
  {
    return (FileResource)getCheckedRef(FileResource.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.FileResource
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */