package org.apache.tools.ant.taskdefs.rmic;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.taskdefs.Rmic;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.JavaEnvUtils;

public class SunRmic
  extends DefaultRmicAdapter
{
  public static final String RMIC_CLASSNAME = "sun.rmi.rmic.Main";
  public static final String COMPILER_NAME = "sun";
  public static final String RMIC_EXECUTABLE = "rmic";
  public static final String ERROR_NO_RMIC_ON_CLASSPATH = "Cannot use SUN rmic, as it is not available.  A common solution is to set the environment variable JAVA_HOME";
  public static final String ERROR_NO_RMIC_ON_CLASSPATH_JAVA_9 = "Cannot use SUN rmic, as it is not available.  The class we try to use is part of the jdk.rmic module which may not be. Please use the 'forking' compiler for JDK 9+";
  public static final String ERROR_RMIC_FAILED = "Error starting SUN rmic: ";
  
  public boolean execute()
    throws BuildException
  {
    getRmic().log("Using SUN rmic compiler", 3);
    Commandline cmd = setupRmicCommand();
    
    LogOutputStream logstr = new LogOutputStream(getRmic(), 1);
    
    boolean success = false;
    try
    {
      Class<?> c = Class.forName("sun.rmi.rmic.Main");
      
      Constructor<?> cons = c.getConstructor(new Class[] { OutputStream.class, String.class });
      Object rmic = cons.newInstance(new Object[] { logstr, "rmic" });
      
      Method doRmic = c.getMethod("compile", new Class[] { String[].class });
      
      boolean ok = Boolean.TRUE.equals(doRmic.invoke(rmic, new Object[] {cmd.getArguments() }));
      success = true;
      return ok;
    }
    catch (ClassNotFoundException ex)
    {
      if (JavaEnvUtils.isAtLeastJavaVersion("9")) {
        throw new BuildException("Cannot use SUN rmic, as it is not available.  The class we try to use is part of the jdk.rmic module which may not be. Please use the 'forking' compiler for JDK 9+", getRmic().getLocation());
      }
      throw new BuildException("Cannot use SUN rmic, as it is not available.  A common solution is to set the environment variable JAVA_HOME", getRmic().getLocation());
    }
    catch (Exception ex)
    {
      if ((ex instanceof BuildException)) {
        throw ((BuildException)ex);
      }
      throw new BuildException("Error starting SUN rmic: ", ex, getRmic().getLocation());
    }
    finally
    {
      try
      {
        logstr.close();
      }
      catch (IOException e)
      {
        if (success) {
          throw new BuildException(e);
        }
      }
    }
  }
  
  protected String[] preprocessCompilerArgs(String[] compilerArgs)
  {
    return filterJvmCompilerArgs(compilerArgs);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.rmic.SunRmic
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */