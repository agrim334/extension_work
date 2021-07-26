package org.apache.tools.ant.taskdefs.optional.javah;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.JavaEnvUtils;

public class JavahAdapterFactory
{
  public static String getDefault()
  {
    if (JavaEnvUtils.isKaffe()) {
      return "kaffeh";
    }
    if (JavaEnvUtils.isGij()) {
      return "gcjh";
    }
    return "forking";
  }
  
  public static JavahAdapter getAdapter(String choice, ProjectComponent log)
    throws BuildException
  {
    return getAdapter(choice, log, null);
  }
  
  public static JavahAdapter getAdapter(String choice, ProjectComponent log, Path classpath)
    throws BuildException
  {
    if (((JavaEnvUtils.isKaffe()) && (choice == null)) || 
      ("kaffeh".equals(choice))) {
      return new Kaffeh();
    }
    if (((JavaEnvUtils.isGij()) && (choice == null)) || 
      ("gcjh".equals(choice))) {
      return new Gcjh();
    }
    if ((JavaEnvUtils.isAtLeastJavaVersion("10")) && ((choice == null) || 
      ("forking".equals(choice)))) {
      throw new BuildException("javah does not exist under Java 10 and higher, use the javac task with nativeHeaderDir instead");
    }
    if ("forking".equals(choice)) {
      return new ForkingJavah();
    }
    if ("sun".equals(choice)) {
      return new SunJavah();
    }
    if (choice != null) {
      return resolveClassName(choice, log
      
        .getProject()
        .createClassLoader(classpath));
    }
    return new ForkingJavah();
  }
  
  private static JavahAdapter resolveClassName(String className, ClassLoader loader)
    throws BuildException
  {
    return (JavahAdapter)ClasspathUtils.newInstance(className, 
      loader != null ? loader : 
      JavahAdapterFactory.class.getClassLoader(), JavahAdapter.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.javah.JavahAdapterFactory
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */