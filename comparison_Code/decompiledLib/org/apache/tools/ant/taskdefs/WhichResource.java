package org.apache.tools.ant.taskdefs;

import java.net.URL;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

public class WhichResource
  extends Task
{
  private Path classpath;
  private String classname;
  private String resource;
  private String property;
  
  public void setClasspath(Path cp)
  {
    if (classpath == null) {
      classpath = cp;
    } else {
      classpath.append(cp);
    }
  }
  
  public Path createClasspath()
  {
    if (classpath == null) {
      classpath = new Path(getProject());
    }
    return classpath.createPath();
  }
  
  public void setClasspathRef(Reference r)
  {
    createClasspath().setRefid(r);
  }
  
  private void validate()
  {
    int setcount = 0;
    if (classname != null) {
      setcount++;
    }
    if (resource != null) {
      setcount++;
    }
    if (setcount == 0) {
      throw new BuildException("One of classname or resource must be specified");
    }
    if (setcount > 1) {
      throw new BuildException("Only one of classname or resource can be specified");
    }
    if (property == null) {
      throw new BuildException("No property defined");
    }
  }
  
  public void execute()
    throws BuildException
  {
    validate();
    if (classpath != null)
    {
      classpath = classpath.concatSystemClasspath("ignore");
      getProject().log("using user supplied classpath: " + classpath, 4);
    }
    else
    {
      classpath = new Path(getProject());
      classpath = classpath.concatSystemClasspath("only");
      getProject().log("using system classpath: " + classpath, 4);
    }
    AntClassLoader loader = AntClassLoader.newAntClassLoader(getProject().getCoreLoader(), 
      getProject(), classpath, false);
    try
    {
      String loc = null;
      if (classname != null) {
        resource = (classname.replace('.', '/') + ".class");
      }
      if (resource == null) {
        throw new BuildException("One of class or resource is required");
      }
      if (resource.startsWith("/")) {
        resource = resource.substring(1);
      }
      log("Searching for " + resource, 3);
      URL url = loader.getResource(resource);
      if (url != null)
      {
        loc = url.toExternalForm();
        getProject().setNewProperty(property, loc);
      }
      if (loader == null) {
        return;
      }
      loader.close();
    }
    catch (Throwable localThrowable)
    {
      if (loader == null) {
        break label304;
      }
    }
    try
    {
      loader.close();
    }
    catch (Throwable localThrowable1)
    {
      localThrowable.addSuppressed(localThrowable1);
    }
    label304:
    throw localThrowable;
  }
  
  public void setResource(String resource)
  {
    this.resource = resource;
  }
  
  public void setClass(String classname)
  {
    this.classname = classname;
  }
  
  public void setProperty(String property)
  {
    this.property = property;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.WhichResource
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */