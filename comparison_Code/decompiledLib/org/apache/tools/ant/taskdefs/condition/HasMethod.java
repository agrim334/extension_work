package org.apache.tools.ant.taskdefs.condition;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

public class HasMethod
  extends ProjectComponent
  implements Condition
{
  private String classname;
  private String method;
  private String field;
  private Path classpath;
  private AntClassLoader loader;
  private boolean ignoreSystemClasses = false;
  
  public void setClasspath(Path classpath)
  {
    createClasspath().append(classpath);
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
  
  public void setClassname(String classname)
  {
    this.classname = classname;
  }
  
  public void setMethod(String method)
  {
    this.method = method;
  }
  
  public void setField(String field)
  {
    this.field = field;
  }
  
  public void setIgnoreSystemClasses(boolean ignoreSystemClasses)
  {
    this.ignoreSystemClasses = ignoreSystemClasses;
  }
  
  private Class<?> loadClass(String classname)
  {
    try
    {
      if (ignoreSystemClasses)
      {
        loader = getProject().createClassLoader(classpath);
        loader.setParentFirst(false);
        loader.addJavaLibraries();
        try
        {
          return loader.findClass(classname);
        }
        catch (SecurityException se)
        {
          throw new BuildException("class \"" + classname + "\" was found but a SecurityException has been raised while loading it", se);
        }
      }
      if (loader != null) {
        return loader.loadClass(classname);
      }
      ClassLoader l = getClass().getClassLoader();
      if (l != null) {
        return Class.forName(classname, true, l);
      }
      return Class.forName(classname);
    }
    catch (ClassNotFoundException e)
    {
      throw new BuildException("class \"" + classname + "\" was not found");
    }
    catch (NoClassDefFoundError e)
    {
      throw new BuildException("Could not load dependent class \"" + e.getMessage() + "\" for class \"" + classname + "\"");
    }
  }
  
  public boolean eval()
    throws BuildException
  {
    if (classname == null) {
      throw new BuildException("No classname defined");
    }
    ClassLoader preLoadClass = loader;
    try
    {
      Class<?> clazz = loadClass(classname);
      boolean bool;
      if (method != null) {
        return isMethodFound(clazz);
      }
      if (field != null) {
        return isFieldFound(clazz);
      }
      throw new BuildException("Neither method nor field defined");
    }
    finally
    {
      if ((preLoadClass != loader) && (loader != null))
      {
        loader.cleanup();
        loader = null;
      }
    }
  }
  
  private boolean isFieldFound(Class<?> clazz)
  {
    for (Field fieldEntry : clazz.getDeclaredFields()) {
      if (fieldEntry.getName().equals(field)) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isMethodFound(Class<?> clazz)
  {
    for (Method methodEntry : clazz.getDeclaredMethods()) {
      if (methodEntry.getName().equals(method)) {
        return true;
      }
    }
    return false;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.condition.HasMethod
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */