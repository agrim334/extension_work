package org.apache.tools.ant.types.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

public abstract class ResourceDecorator
  extends Resource
{
  private Resource resource;
  
  protected ResourceDecorator() {}
  
  protected ResourceDecorator(ResourceCollection other)
  {
    addConfigured(other);
  }
  
  public final void addConfigured(ResourceCollection a)
  {
    checkChildrenAllowed();
    if (resource != null) {
      throw new BuildException("you must not specify more than one resource");
    }
    if (a.size() != 1) {
      throw new BuildException("only single argument resource collections are supported");
    }
    setChecked(false);
    resource = ((Resource)a.iterator().next());
  }
  
  public String getName()
  {
    return getResource().getName();
  }
  
  public boolean isExists()
  {
    return getResource().isExists();
  }
  
  public long getLastModified()
  {
    return getResource().getLastModified();
  }
  
  public boolean isDirectory()
  {
    return getResource().isDirectory();
  }
  
  public long getSize()
  {
    return getResource().getSize();
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    return getResource().getInputStream();
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    return getResource().getOutputStream();
  }
  
  public boolean isFilesystemOnly()
  {
    return as(FileProvider.class) != null;
  }
  
  public void setRefid(Reference r)
  {
    if (resource != null) {
      throw noChildrenAllowed();
    }
    super.setRefid(r);
  }
  
  public <T> T as(Class<T> clazz)
  {
    return (T)getResource().as(clazz);
  }
  
  public int compareTo(Resource other)
  {
    if (other == this) {
      return 0;
    }
    if ((other instanceof ResourceDecorator)) {
      return getResource().compareTo(((ResourceDecorator)other)
        .getResource());
    }
    return getResource().compareTo(other);
  }
  
  public int hashCode()
  {
    return getClass().hashCode() << 4 | getResource().hashCode();
  }
  
  protected final Resource getResource()
  {
    if (isReference()) {
      return (Resource)getCheckedRef(Resource.class);
    }
    if (resource == null) {
      throw new BuildException("no resource specified");
    }
    dieOnCircularReference();
    return resource;
  }
  
  protected void dieOnCircularReference(Stack<Object> stack, Project project)
    throws BuildException
  {
    if (isChecked()) {
      return;
    }
    if (isReference())
    {
      super.dieOnCircularReference(stack, project);
    }
    else
    {
      pushAndInvokeCircularReferenceCheck(resource, stack, project);
      setChecked(true);
    }
  }
  
  public void setName(String name)
    throws BuildException
  {
    throw new BuildException("you can't change the name of a " + getDataTypeName());
  }
  
  public void setExists(boolean exists)
  {
    throw new BuildException("you can't change the exists state of a " + getDataTypeName());
  }
  
  public void setLastModified(long lastmodified)
    throws BuildException
  {
    throw new BuildException("you can't change the timestamp of a " + getDataTypeName());
  }
  
  public void setDirectory(boolean directory)
    throws BuildException
  {
    throw new BuildException("you can't change the directory state of a " + getDataTypeName());
  }
  
  public void setSize(long size)
    throws BuildException
  {
    throw new BuildException("you can't change the size of a " + getDataTypeName());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.ResourceDecorator
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */