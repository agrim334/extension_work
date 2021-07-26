package org.apache.tools.ant.types;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public class ZipFileSet
  extends ArchiveFileSet
{
  public ZipFileSet() {}
  
  protected ZipFileSet(FileSet fileset)
  {
    super(fileset);
  }
  
  protected ZipFileSet(ZipFileSet fileset)
  {
    super(fileset);
  }
  
  protected ArchiveScanner newArchiveScanner()
  {
    ZipScanner zs = new ZipScanner();
    zs.setEncoding(getEncoding());
    return zs;
  }
  
  protected AbstractFileSet getRef(Project p)
  {
    dieOnCircularReference(p);
    Object o = getRefid().getReferencedObject(p);
    if ((o instanceof ZipFileSet)) {
      return (AbstractFileSet)o;
    }
    if ((o instanceof FileSet))
    {
      ZipFileSet zfs = new ZipFileSet((FileSet)o);
      configureFileSet(zfs);
      return zfs;
    }
    String msg = getRefid().getRefId() + " doesn't denote a zipfileset or a fileset";
    throw new BuildException(msg);
  }
  
  protected AbstractFileSet getRef()
  {
    return getRef(getProject());
  }
  
  public Object clone()
  {
    if (isReference()) {
      return getRef().clone();
    }
    return super.clone();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.ZipFileSet
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */