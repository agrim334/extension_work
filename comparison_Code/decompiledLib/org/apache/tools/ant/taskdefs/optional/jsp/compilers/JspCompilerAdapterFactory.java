package org.apache.tools.ant.taskdefs.optional.jsp.compilers;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.jsp.Jasper41Mangler;
import org.apache.tools.ant.taskdefs.optional.jsp.JspNameMangler;

public final class JspCompilerAdapterFactory
{
  public static JspCompilerAdapter getCompiler(String compilerType, Task task)
    throws BuildException
  {
    return getCompiler(compilerType, task, task
    
      .getProject().createClassLoader(null));
  }
  
  public static JspCompilerAdapter getCompiler(String compilerType, Task task, AntClassLoader loader)
    throws BuildException
  {
    if ("jasper".equalsIgnoreCase(compilerType)) {
      return new JasperC(new JspNameMangler());
    }
    if ("jasper41".equalsIgnoreCase(compilerType)) {
      return new JasperC(new Jasper41Mangler());
    }
    return resolveClassName(compilerType, loader);
  }
  
  private static JspCompilerAdapter resolveClassName(String className, AntClassLoader classloader)
    throws BuildException
  {
    try
    {
      Class<? extends JspCompilerAdapter> c = classloader.findClass(className).asSubclass(JspCompilerAdapter.class);
      return (JspCompilerAdapter)c.newInstance();
    }
    catch (ClassNotFoundException cnfe)
    {
      throw new BuildException(className + " can't be found.", cnfe);
    }
    catch (ClassCastException cce)
    {
      throw new BuildException(className + " isn't the classname of a compiler adapter.", cce);
    }
    catch (Throwable t)
    {
      throw new BuildException(className + " caused an interesting exception.", t);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.jsp.compilers.JspCompilerAdapterFactory
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */