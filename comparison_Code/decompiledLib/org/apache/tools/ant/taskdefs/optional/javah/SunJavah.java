package org.apache.tools.ant.taskdefs.optional.javah;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.launch.Locator;
import org.apache.tools.ant.taskdefs.ExecuteJava;
import org.apache.tools.ant.taskdefs.optional.Javah;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.Path;

public class SunJavah
  implements JavahAdapter
{
  public static final String IMPLEMENTATION_NAME = "sun";
  
  public boolean compile(Javah javah)
    throws BuildException
  {
    Commandline cmd = setupJavahCommand(javah);
    ExecuteJava ej = new ExecuteJava();
    try
    {
      try
      {
        c = Class.forName("com.sun.tools.javah.oldjavah.Main");
      }
      catch (ClassNotFoundException cnfe)
      {
        Class<?> c;
        c = Class.forName("com.sun.tools.javah.Main");
      }
    }
    catch (ClassNotFoundException ex)
    {
      Class<?> c;
      throw new BuildException("Can't load javah", ex, javah.getLocation());
    }
    Class<?> c;
    cmd.setExecutable(c.getName());
    ej.setJavaCommand(cmd);
    File f = Locator.getClassSource(c);
    if (f != null) {
      ej.setClasspath(new Path(javah.getProject(), f.getPath()));
    }
    return ej.fork(javah) == 0;
  }
  
  static Commandline setupJavahCommand(Javah javah)
  {
    Commandline cmd = new Commandline();
    if (javah.getDestdir() != null)
    {
      cmd.createArgument().setValue("-d");
      cmd.createArgument().setFile(javah.getDestdir());
    }
    if (javah.getOutputfile() != null)
    {
      cmd.createArgument().setValue("-o");
      cmd.createArgument().setFile(javah.getOutputfile());
    }
    if (javah.getClasspath() != null)
    {
      cmd.createArgument().setValue("-classpath");
      cmd.createArgument().setPath(javah.getClasspath());
    }
    if (javah.getVerbose()) {
      cmd.createArgument().setValue("-verbose");
    }
    if (javah.getOld()) {
      cmd.createArgument().setValue("-old");
    }
    if (javah.getForce()) {
      cmd.createArgument().setValue("-force");
    }
    if ((javah.getStubs()) && (!javah.getOld())) {
      throw new BuildException("stubs only available in old mode.", javah.getLocation());
    }
    if (javah.getStubs()) {
      cmd.createArgument().setValue("-stubs");
    }
    Path bcp = new Path(javah.getProject());
    if (javah.getBootclasspath() != null) {
      bcp.append(javah.getBootclasspath());
    }
    bcp = bcp.concatSystemBootClasspath("ignore");
    if (bcp.size() > 0)
    {
      cmd.createArgument().setValue("-bootclasspath");
      cmd.createArgument().setPath(bcp);
    }
    cmd.addArguments(javah.getCurrentArgs());
    
    javah.logAndAddFiles(cmd);
    return cmd;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.javah.SunJavah
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */