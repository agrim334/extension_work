package org.apache.tools.ant.loader;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

/**
 * @deprecated
 */
public class AntClassLoader5
  extends AntClassLoader
{
  static
  {
    registerAsParallelCapable();
  }
  
  public AntClassLoader5(ClassLoader parent, Project project, Path classpath, boolean parentFirst)
  {
    super(parent, project, classpath, parentFirst);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.loader.AntClassLoader5
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */