package org.apache.tools.ant.taskdefs.optional.native2ascii;

import java.lang.reflect.Method;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.optional.Native2Ascii;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;

public final class SunNative2Ascii
  extends DefaultNative2Ascii
{
  public static final String IMPLEMENTATION_NAME = "sun";
  private static final String SUN_TOOLS_NATIVE2ASCII_MAIN = "sun.tools.native2ascii.Main";
  
  protected void setup(Commandline cmd, Native2Ascii args)
    throws BuildException
  {
    if (args.getReverse()) {
      cmd.createArgument().setValue("-reverse");
    }
    super.setup(cmd, args);
  }
  
  protected boolean run(Commandline cmd, ProjectComponent log)
    throws BuildException
  {
    try
    {
      Class<?> n2aMain = Class.forName("sun.tools.native2ascii.Main");
      Method convert = n2aMain.getMethod("convert", new Class[] { String[].class });
      return Boolean.TRUE.equals(convert.invoke(n2aMain.newInstance(), new Object[] {cmd
        .getArguments() }));
    }
    catch (BuildException ex)
    {
      throw ex;
    }
    catch (NoSuchMethodException ex)
    {
      throw new BuildException("Could not find convert() method in %s", new Object[] { "sun.tools.native2ascii.Main" });
    }
    catch (Exception ex)
    {
      throw new BuildException("Error starting Sun's native2ascii: ", ex);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.native2ascii.SunNative2Ascii
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */