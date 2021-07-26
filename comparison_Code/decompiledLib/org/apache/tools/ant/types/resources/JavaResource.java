package org.apache.tools.ant.types.resources;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;

public class JavaResource
  extends AbstractClasspathResource
  implements URLProvider
{
  public JavaResource() {}
  
  public JavaResource(String name, Path path)
  {
    setName(name);
    setClasspath(path);
  }
  
  protected InputStream openInputStream(ClassLoader cl)
    throws IOException
  {
    InputStream inputStream;
    if (cl == null)
    {
      InputStream inputStream = ClassLoader.getSystemResourceAsStream(getName());
      if (inputStream == null) {
        throw new FileNotFoundException("No resource " + getName() + " on Ant's classpath");
      }
    }
    else
    {
      inputStream = cl.getResourceAsStream(getName());
      if (inputStream == null) {
        throw new FileNotFoundException("No resource " + getName() + " on the classpath " + cl);
      }
    }
    return inputStream;
  }
  
  public URL getURL()
  {
    if (isReference()) {
      return getRef().getURL();
    }
    AbstractClasspathResource.ClassLoaderWithFlag classLoader = getClassLoader();
    if (classLoader.getLoader() == null) {
      return ClassLoader.getSystemResource(getName());
    }
    try
    {
      return classLoader.getLoader().getResource(getName());
    }
    finally
    {
      classLoader.cleanup();
    }
  }
  
  public int compareTo(Resource another)
  {
    if (isReference()) {
      return getRef().compareTo(another);
    }
    if (another.getClass().equals(getClass()))
    {
      JavaResource otherjr = (JavaResource)another;
      if (!getName().equals(otherjr.getName())) {
        return getName().compareTo(otherjr.getName());
      }
      if (getLoader() != otherjr.getLoader())
      {
        if (getLoader() == null) {
          return -1;
        }
        if (otherjr.getLoader() == null) {
          return 1;
        }
        return 
          getLoader().getRefId().compareTo(otherjr.getLoader().getRefId());
      }
      Path p = getClasspath();
      Path op = otherjr.getClasspath();
      if (p != op)
      {
        if (p == null) {
          return -1;
        }
        if (op == null) {
          return 1;
        }
        return p.toString().compareTo(op.toString());
      }
      return 0;
    }
    return super.compareTo(another);
  }
  
  protected JavaResource getRef()
  {
    return (JavaResource)getCheckedRef(JavaResource.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.JavaResource
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */