package org.apache.tools.ant.taskdefs.compilers;

import java.lang.reflect.Method;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Commandline;

public class Javac13
  extends DefaultCompilerAdapter
{
  private static final int MODERN_COMPILER_SUCCESS = 0;
  
  public boolean execute()
    throws BuildException
  {
    attributes.log("Using modern compiler", 3);
    Commandline cmd = setupModernJavacCommand();
    try
    {
      Class<?> c = Class.forName("com.sun.tools.javac.Main");
      Object compiler = c.newInstance();
      Method compile = c.getMethod("compile", new Class[] { String[].class });
      int result = ((Integer)compile.invoke(compiler, new Object[] {cmd
        .getArguments() })).intValue();
      return result == 0;
    }
    catch (Exception ex)
    {
      if ((ex instanceof BuildException)) {
        throw ((BuildException)ex);
      }
      throw new BuildException("Error starting modern compiler", ex, location);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.compilers.Javac13
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */