package org.apache.tools.ant.types.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

public class TarResource
  extends ArchiveResource
{
  private String userName = "";
  private String groupName = "";
  private long uid;
  private long gid;
  
  public TarResource() {}
  
  public TarResource(File a, TarEntry e)
  {
    super(a, true);
    setEntry(e);
  }
  
  public TarResource(Resource a, TarEntry e)
  {
    super(a, true);
    setEntry(e);
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    if (isReference()) {
      return getRef().getInputStream();
    }
    Resource archive = getArchive();
    TarInputStream i = new TarInputStream(archive.getInputStream());
    TarEntry te;
    while ((te = i.getNextEntry()) != null) {
      if (te.getName().equals(getName())) {
        return i;
      }
    }
    FileUtils.close(i);
    
    throw new BuildException("no entry " + getName() + " in " + getArchive());
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    if (isReference()) {
      return getRef().getOutputStream();
    }
    throw new UnsupportedOperationException("Use the tar task for tar output.");
  }
  
  public String getUserName()
  {
    if (isReference()) {
      return getRef().getUserName();
    }
    checkEntry();
    return userName;
  }
  
  public String getGroup()
  {
    if (isReference()) {
      return getRef().getGroup();
    }
    checkEntry();
    return groupName;
  }
  
  public long getLongUid()
  {
    if (isReference()) {
      return getRef().getLongUid();
    }
    checkEntry();
    return uid;
  }
  
  @Deprecated
  public int getUid()
  {
    return (int)getLongUid();
  }
  
  public long getLongGid()
  {
    if (isReference()) {
      return getRef().getLongGid();
    }
    checkEntry();
    return gid;
  }
  
  @Deprecated
  public int getGid()
  {
    return (int)getLongGid();
  }
  
  protected void fetchEntry()
  {
    Resource archive = getArchive();
    try
    {
      TarInputStream i = new TarInputStream(archive.getInputStream());
      try
      {
        TarEntry te = null;
        while ((te = i.getNextEntry()) != null) {
          if (te.getName().equals(getName()))
          {
            setEntry(te);
            
            i.close();return;
          }
        }
        i.close();
      }
      catch (Throwable localThrowable) {}
      try
      {
        i.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      throw localThrowable;
    }
    catch (IOException e)
    {
      log(e.getMessage(), 4);
      throw new BuildException(e);
    }
    setEntry(null);
  }
  
  protected TarResource getRef()
  {
    return (TarResource)getCheckedRef(TarResource.class);
  }
  
  private void setEntry(TarEntry e)
  {
    if (e == null)
    {
      setExists(false);
      return;
    }
    setName(e.getName());
    setExists(true);
    setLastModified(e.getModTime().getTime());
    setDirectory(e.isDirectory());
    setSize(e.getSize());
    setMode(e.getMode());
    userName = e.getUserName();
    groupName = e.getGroupName();
    uid = e.getLongUserId();
    gid = e.getLongGroupId();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.TarResource
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */