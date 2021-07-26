package org.apache.tools.ant.taskdefs.optional.native2ascii;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.ExecuteJava;
import org.apache.tools.ant.taskdefs.optional.Native2Ascii;
import org.apache.tools.ant.types.Commandline;

public final class KaffeNative2Ascii
  extends DefaultNative2Ascii
{
  private static final String[] N2A_CLASSNAMES = { "gnu.classpath.tools.native2ascii.Native2ASCII", "kaffe.tools.native2ascii.Native2Ascii" };
  public static final String IMPLEMENTATION_NAME = "kaffe";
  
  protected void setup(Commandline cmd, Native2Ascii args)
    throws BuildException
  {
    if (args.getReverse()) {
      throw new BuildException("-reverse is not supported by Kaffe");
    }
    super.setup(cmd, args);
  }
  
  protected boolean run(Commandline cmd, ProjectComponent log)
    throws BuildException
  {
    ExecuteJava ej = new ExecuteJava();
    Class<?> c = getN2aClass();
    if (c == null) {
      throw new BuildException("Couldn't load Kaffe's Native2Ascii class");
    }
    cmd.setExecutable(c.getName());
    ej.setJavaCommand(cmd);
    ej.execute(log.getProject());
    
    return true;
  }
  
  private static Class<?> getN2aClass()
  {
    for (String className : N2A_CLASSNAMES) {
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
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.native2ascii.KaffeNative2Ascii
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */