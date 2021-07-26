package org.apache.tools.ant.taskdefs.compilers;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.JavaEnvUtils;

public final class CompilerAdapterFactory
{
  private static final String MODERN_COMPILER = "com.sun.tools.javac.Main";
  
  public static CompilerAdapter getCompiler(String compilerType, Task task)
    throws BuildException
  {
    return getCompiler(compilerType, task, null);
  }
  
  public static CompilerAdapter getCompiler(String compilerType, Task task, Path classpath)
    throws BuildException
  {
    if ("jikes".equalsIgnoreCase(compilerType)) {
      return new Jikes();
    }
    if ("extjavac".equalsIgnoreCase(compilerType)) {
      return new JavacExternal();
    }
    if (("classic".equalsIgnoreCase(compilerType)) || 
      ("javac1.1".equalsIgnoreCase(compilerType)) || 
      ("javac1.2".equalsIgnoreCase(compilerType)))
    {
      task.log("This version of java does not support the classic compiler; upgrading to modern", 1);
      
      compilerType = "modern";
    }
    if (("modern".equalsIgnoreCase(compilerType)) || 
      ("javac1.3".equalsIgnoreCase(compilerType)) || 
      ("javac1.4".equalsIgnoreCase(compilerType)) || 
      ("javac1.5".equalsIgnoreCase(compilerType)) || 
      ("javac1.6".equalsIgnoreCase(compilerType)) || 
      ("javac1.7".equalsIgnoreCase(compilerType)) || 
      ("javac1.8".equalsIgnoreCase(compilerType)) || 
      ("javac1.9".equalsIgnoreCase(compilerType)) || 
      ("javac9".equalsIgnoreCase(compilerType)) || 
      ("javac10+".equalsIgnoreCase(compilerType)))
    {
      if (doesModernCompilerExist()) {
        return new Javac13();
      }
      throw new BuildException("Unable to find a javac compiler;\n%s is not on the classpath.\nPerhaps JAVA_HOME does not point to the JDK.\nIt is currently set to \"%s\"", new Object[] { "com.sun.tools.javac.Main", JavaEnvUtils.getJavaHome() });
    }
    if (("jvc".equalsIgnoreCase(compilerType)) || 
      ("microsoft".equalsIgnoreCase(compilerType))) {
      return new Jvc();
    }
    if ("kjc".equalsIgnoreCase(compilerType)) {
      return new Kjc();
    }
    if ("gcj".equalsIgnoreCase(compilerType)) {
      return new Gcj();
    }
    if (("sj".equalsIgnoreCase(compilerType)) || 
      ("symantec".equalsIgnoreCase(compilerType))) {
      return new Sj();
    }
    return resolveClassName(compilerType, task
    
      .getProject().createClassLoader(classpath));
  }
  
  private static boolean doesModernCompilerExist()
  {
    try
    {
      Class.forName("com.sun.tools.javac.Main");
      return true;
    }
    catch (ClassNotFoundException cnfe)
    {
      try
      {
        ClassLoader cl = CompilerAdapterFactory.class.getClassLoader();
        if (cl != null)
        {
          cl.loadClass("com.sun.tools.javac.Main");
          return true;
        }
      }
      catch (ClassNotFoundException localClassNotFoundException1) {}
    }
    return false;
  }
  
  private static CompilerAdapter resolveClassName(String className, ClassLoader loader)
    throws BuildException
  {
    return (CompilerAdapter)ClasspathUtils.newInstance(className, 
      loader != null ? loader : 
      CompilerAdapterFactory.class.getClassLoader(), CompilerAdapter.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.compilers.CompilerAdapterFactory
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */