package org.apache.tools.ant.taskdefs.rmic;

import java.lang.reflect.Method;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Rmic;
import org.apache.tools.ant.types.Commandline;

public class WLRmic
  extends DefaultRmicAdapter
{
  public static final String WLRMIC_CLASSNAME = "weblogic.rmic";
  public static final String COMPILER_NAME = "weblogic";
  public static final String ERROR_NO_WLRMIC_ON_CLASSPATH = "Cannot use WebLogic rmic, as it is not available. Add it to Ant's classpath with the -lib option";
  public static final String ERROR_WLRMIC_FAILED = "Error starting WebLogic rmic: ";
  public static final String WL_RMI_STUB_SUFFIX = "_WLStub";
  public static final String WL_RMI_SKEL_SUFFIX = "_WLSkel";
  public static final String UNSUPPORTED_STUB_OPTION = "Unsupported stub option: ";
  
  protected boolean areIiopAndIdlSupported()
  {
    return true;
  }
  
  public boolean execute()
    throws BuildException
  {
    getRmic().log("Using WebLogic rmic", 3);
    Commandline cmd = setupRmicCommand(new String[] { "-noexit" });
    
    AntClassLoader loader = null;
    try
    {
      Class<?> c;
      Class<?> c;
      if (getRmic().getClasspath() == null)
      {
        c = Class.forName("weblogic.rmic");
      }
      else
      {
        loader = getRmic().getProject().createClassLoader(getRmic().getClasspath());
        c = Class.forName("weblogic.rmic", true, loader);
      }
      Method doRmic = c.getMethod("main", new Class[] { String[].class });
      doRmic.invoke(null, new Object[] { cmd.getArguments() });
      return true;
    }
    catch (ClassNotFoundException ex)
    {
      throw new BuildException("Cannot use WebLogic rmic, as it is not available. Add it to Ant's classpath with the -lib option", getRmic().getLocation());
    }
    catch (Exception ex)
    {
      if ((ex instanceof BuildException)) {
        throw ((BuildException)ex);
      }
      throw new BuildException("Error starting WebLogic rmic: ", ex, getRmic().getLocation());
    }
    finally
    {
      if (loader != null) {
        loader.cleanup();
      }
    }
  }
  
  public String getStubClassSuffix()
  {
    return "_WLStub";
  }
  
  public String getSkelClassSuffix()
  {
    return "_WLSkel";
  }
  
  protected String[] preprocessCompilerArgs(String[] compilerArgs)
  {
    return filterJvmCompilerArgs(compilerArgs);
  }
  
  protected String addStubVersionOptions()
  {
    String stubVersion = getRmic().getStubVersion();
    if (null != stubVersion) {
      getRmic().log("Unsupported stub option: " + stubVersion, 1);
    }
    return null;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.rmic.WLRmic
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */