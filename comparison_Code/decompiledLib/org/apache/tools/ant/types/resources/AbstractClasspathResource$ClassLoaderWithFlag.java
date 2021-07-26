package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.AntClassLoader;

public class AbstractClasspathResource$ClassLoaderWithFlag
{
  private final ClassLoader loader;
  private final boolean cleanup;
  
  AbstractClasspathResource$ClassLoaderWithFlag(ClassLoader l, boolean needsCleanup)
  {
    loader = l;
    cleanup = ((needsCleanup) && ((l instanceof AntClassLoader)));
  }
  
  public ClassLoader getLoader()
  {
    return loader;
  }
  
  public boolean needsCleanup()
  {
    return cleanup;
  }
  
  public void cleanup()
  {
    if (cleanup) {
      ((AntClassLoader)loader).cleanup();
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.AbstractClasspathResource.ClassLoaderWithFlag
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */