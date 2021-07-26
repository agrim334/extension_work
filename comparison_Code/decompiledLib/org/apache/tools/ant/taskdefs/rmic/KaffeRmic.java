package org.apache.tools.ant.taskdefs.rmic;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.ExecuteJava;
import org.apache.tools.ant.taskdefs.Rmic;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;

public class KaffeRmic
  extends DefaultRmicAdapter
{
  private static final String[] RMIC_CLASSNAMES = { "gnu.classpath.tools.rmi.rmic.RMIC", "gnu.java.rmi.rmic.RMIC", "kaffe.rmi.rmic.RMIC" };
  public static final String COMPILER_NAME = "kaffe";
  
  protected boolean areIiopAndIdlSupported()
  {
    return true;
  }
  
  public boolean execute()
    throws BuildException
  {
    getRmic().log("Using Kaffe rmic", 3);
    Commandline cmd = setupRmicCommand();
    
    Class<?> c = getRmicClass();
    if (c == null)
    {
      StringBuilder buf = new StringBuilder("Cannot use Kaffe rmic, as it is not available.  None of ");
      for (String className : RMIC_CLASSNAMES)
      {
        if (buf.length() > 0) {
          buf.append(", ");
        }
        buf.append(className);
      }
      buf.append(" have been found. A common solution is to set the environment variable JAVA_HOME or CLASSPATH.");
      
      throw new BuildException(buf.toString(), getRmic().getLocation());
    }
    cmd.setExecutable(c.getName());
    if (!c.getName().equals(RMIC_CLASSNAMES[(RMIC_CLASSNAMES.length - 1)]))
    {
      cmd.createArgument().setValue("-verbose");
      getRmic().log(Commandline.describeCommand(cmd));
    }
    ExecuteJava ej = new ExecuteJava();
    ej.setJavaCommand(cmd);
    return ej.fork(getRmic()) == 0;
  }
  
  public static boolean isAvailable()
  {
    return getRmicClass() != null;
  }
  
  private static Class<?> getRmicClass()
  {
    for (String className : RMIC_CLASSNAMES) {
      try
      {
        return Class.forName(className);
      }
      catch (ClassNotFoundException localClassNotFoundException) {}
    }
    return null;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.rmic.KaffeRmic
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */