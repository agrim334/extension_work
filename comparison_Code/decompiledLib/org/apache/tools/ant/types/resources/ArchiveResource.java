package org.apache.tools.ant.types.resources;

import java.io.File;
import java.util.Iterator;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

public abstract class ArchiveResource
  extends Resource
{
  private static final int NULL_ARCHIVE = Resource.getMagicNumber("null archive".getBytes());
  private Resource archive;
  private boolean haveEntry = false;
  private boolean modeSet = false;
  private int mode = 0;
  
  protected ArchiveResource() {}
  
  protected ArchiveResource(File a)
  {
    this(a, false);
  }
  
  protected ArchiveResource(File a, boolean withEntry)
  {
    setArchive(a);
    haveEntry = withEntry;
  }
  
  protected ArchiveResource(Resource a, boolean withEntry)
  {
    addConfigured(a);
    haveEntry = withEntry;
  }
  
  public void setArchive(File a)
  {
    checkAttributesAllowed();
    archive = new FileResource(a);
  }
  
  public void setMode(int mode)
  {
    checkAttributesAllowed();
    this.mode = mode;
    modeSet = true;
  }
  
  public void addConfigured(ResourceCollection a)
  {
    checkChildrenAllowed();
    if (archive != null) {
      throw new BuildException("you must not specify more than one archive");
    }
    if (a.size() != 1) {
      throw new BuildException("only single argument resource collections are supported as archives");
    }
    archive = ((Resource)a.iterator().next());
  }
  
  public Resource getArchive()
  {
    return isReference() ? getRef().getArchive() : archive;
  }
  
  public long getLastModified()
  {
    if (isReference()) {
      return getRef().getLastModified();
    }
    checkEntry();
    return super.getLastModified();
  }
  
  public long getSize()
  {
    if (isReference()) {
      return getRef().getSize();
    }
    checkEntry();
    return super.getSize();
  }
  
  public boolean isDirectory()
  {
    if (isReference()) {
      return getRef().isDirectory();
    }
    checkEntry();
    return super.isDirectory();
  }
  
  public boolean isExists()
  {
    if (isReference()) {
      return getRef().isExists();
    }
    checkEntry();
    return super.isExists();
  }
  
  public int getMode()
  {
    if (isReference()) {
      return getRef().getMode();
    }
    checkEntry();
    return mode;
  }
  
  public void setRefid(Reference r)
  {
    if ((archive != null) || (modeSet)) {
      throw tooManyAttributes();
    }
    super.setRefid(r);
  }
  
  public int compareTo(Resource another)
  {
    return equals(another) ? 0 : super.compareTo(another);
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
    ArchiveResource r = (ArchiveResource)another;
    return (getArchive().equals(r.getArchive())) && 
      (getName().equals(r.getName()));
  }
  
  public int hashCode()
  {
    return 
      super.hashCode() * (getArchive() == null ? NULL_ARCHIVE : getArchive().hashCode());
  }
  
  public String toString()
  {
    return 
      getArchive().toString() + ':' + getName();
  }
  
  protected final synchronized void checkEntry()
    throws BuildException
  {
    dieOnCircularReference();
    if (haveEntry) {
      return;
    }
    String name = getName();
    if (name == null) {
      throw new BuildException("entry name not set");
    }
    Resource r = getArchive();
    if (r == null) {
      throw new BuildException("archive attribute not set");
    }
    if (!r.isExists()) {
      throw new BuildException("%s does not exist.", new Object[] { r });
    }
    if (r.isDirectory()) {
      throw new BuildException("%s denotes a directory.", new Object[] { r });
    }
    fetchEntry();
    haveEntry = true;
  }
  
  protected abstract void fetchEntry();
  
  protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p)
  {
    if (isChecked()) {
      return;
    }
    if (isReference())
    {
      super.dieOnCircularReference(stk, p);
    }
    else
    {
      if (archive != null) {
        pushAndInvokeCircularReferenceCheck(archive, stk, p);
      }
      setChecked(true);
    }
  }
  
  protected ArchiveResource getRef()
  {
    return (ArchiveResource)getCheckedRef(ArchiveResource.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.ArchiveResource
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */