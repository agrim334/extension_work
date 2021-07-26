package org.apache.tools.ant.util;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.launch.Locator;

public class LoaderUtils
{
  private static final FileUtils FILE_UTILS = ;
  
  public static void setContextClassLoader(ClassLoader loader)
  {
    Thread currentThread = Thread.currentThread();
    currentThread.setContextClassLoader(loader);
  }
  
  public static ClassLoader getContextClassLoader()
  {
    Thread currentThread = Thread.currentThread();
    return currentThread.getContextClassLoader();
  }
  
  public static boolean isContextLoaderAvailable()
  {
    return true;
  }
  
  private static File normalizeSource(File source)
  {
    if (source != null) {
      try
      {
        source = FILE_UTILS.normalize(source.getAbsolutePath());
      }
      catch (BuildException localBuildException) {}
    }
    return source;
  }
  
  public static File getClassSource(Class<?> c)
  {
    return normalizeSource(Locator.getClassSource(c));
  }
  
  public static File getResourceSource(ClassLoader c, String resource)
  {
    if (c == null) {
      c = LoaderUtils.class.getClassLoader();
    }
    return normalizeSource(Locator.getResourceSource(c, resource));
  }
  
  public static String classNameToResource(String className)
  {
    return className.replace('.', '/') + ".class";
  }
  
  public static boolean classExists(ClassLoader loader, String className)
  {
    return loader.getResource(classNameToResource(className)) != null;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.LoaderUtils
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */