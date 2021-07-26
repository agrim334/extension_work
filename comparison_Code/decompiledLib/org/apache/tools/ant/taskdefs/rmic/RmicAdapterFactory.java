package org.apache.tools.ant.taskdefs.rmic;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.JavaEnvUtils;

public final class RmicAdapterFactory
{
  public static final String ERROR_UNKNOWN_COMPILER = "Class not found: ";
  public static final String ERROR_NOT_RMIC_ADAPTER = "Class of unexpected Type: ";
  public static final String DEFAULT_COMPILER = "default";
  
  public static RmicAdapter getRmic(String rmicType, Task task)
    throws BuildException
  {
    return getRmic(rmicType, task, null);
  }
  
  public static RmicAdapter getRmic(String rmicType, Task task, Path classpath)
    throws BuildException
  {
    if (("default".equalsIgnoreCase(rmicType)) || (rmicType.isEmpty())) {
      if (KaffeRmic.isAvailable()) {
        rmicType = "kaffe";
      } else if (JavaEnvUtils.isAtLeastJavaVersion("9")) {
        rmicType = "forking";
      } else {
        rmicType = "sun";
      }
    }
    if ("sun".equalsIgnoreCase(rmicType)) {
      return new SunRmic();
    }
    if ("kaffe".equalsIgnoreCase(rmicType)) {
      return new KaffeRmic();
    }
    if ("weblogic".equalsIgnoreCase(rmicType)) {
      return new WLRmic();
    }
    if ("forking".equalsIgnoreCase(rmicType)) {
      return new ForkingSunRmic();
    }
    if ("xnew".equalsIgnoreCase(rmicType)) {
      return new XNewRmic();
    }
    return resolveClassName(rmicType, task
    
      .getProject().createClassLoader(classpath));
  }
  
  private static RmicAdapter resolveClassName(String className, ClassLoader loader)
    throws BuildException
  {
    return (RmicAdapter)ClasspathUtils.newInstance(className, 
      loader != null ? loader : 
      RmicAdapterFactory.class.getClassLoader(), RmicAdapter.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.rmic.RmicAdapterFactory
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */