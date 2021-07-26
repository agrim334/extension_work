package org.apache.tools.ant.util;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

public class ClasspathUtils$Delegate
{
  private final ProjectComponent component;
  private Path classpath;
  private String classpathId;
  private String className;
  private String loaderId;
  private boolean reverseLoader = false;
  
  ClasspathUtils$Delegate(ProjectComponent component)
  {
    this.component = component;
  }
  
  public void setClasspath(Path classpath)
  {
    if (this.classpath == null) {
      this.classpath = classpath;
    } else {
      this.classpath.append(classpath);
    }
  }
  
  public Path createClasspath()
  {
    if (classpath == null) {
      classpath = new Path(component.getProject());
    }
    return classpath.createPath();
  }
  
  public void setClassname(String fcqn)
  {
    className = fcqn;
  }
  
  public void setClasspathref(Reference r)
  {
    classpathId = r.getRefId();
    createClasspath().setRefid(r);
  }
  
  public void setReverseLoader(boolean reverseLoader)
  {
    this.reverseLoader = reverseLoader;
  }
  
  public void setLoaderRef(Reference r)
  {
    loaderId = r.getRefId();
  }
  
  public ClassLoader getClassLoader()
  {
    return ClasspathUtils.getClassLoaderForPath(getContextProject(), classpath, getClassLoadId(), reverseLoader, (loaderId != null) || 
      (ClasspathUtils.access$000(getContextProject())));
  }
  
  private Project getContextProject()
  {
    return component.getProject();
  }
  
  public String getClassLoadId()
  {
    if ((loaderId == null) && (classpathId != null)) {
      return "ant.loader." + classpathId;
    }
    return loaderId;
  }
  
  public Object newInstance()
  {
    return ClasspathUtils.newInstance(className, getClassLoader());
  }
  
  public Path getClasspath()
  {
    return classpath;
  }
  
  public boolean isReverseLoader()
  {
    return reverseLoader;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.ClasspathUtils.Delegate
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */