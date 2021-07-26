package org.apache.tools.ant.taskdefs.optional.javah;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.optional.Javah;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.JavaEnvUtils;

public class Gcjh
  implements JavahAdapter
{
  public static final String IMPLEMENTATION_NAME = "gcjh";
  
  public boolean compile(Javah javah)
    throws BuildException
  {
    Commandline cmd = setupGcjhCommand(javah);
    try
    {
      Execute.runCommand(javah, cmd.getCommandline());
      return true;
    }
    catch (BuildException e)
    {
      if (!e.getMessage().contains("failed with return code")) {
        throw e;
      }
    }
    return false;
  }
  
  private Commandline setupGcjhCommand(Javah javah)
  {
    Commandline cmd = new Commandline();
    cmd.setExecutable(JavaEnvUtils.getJdkExecutable("gcjh"));
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
    Path cp = new Path(javah.getProject());
    if (javah.getBootclasspath() != null) {
      cp.append(javah.getBootclasspath());
    }
    cp = cp.concatSystemBootClasspath("ignore");
    if (javah.getClasspath() != null) {
      cp.append(javah.getClasspath());
    }
    if (cp.size() > 0)
    {
      cmd.createArgument().setValue("--classpath");
      cmd.createArgument().setPath(cp);
    }
    if (!javah.getOld()) {
      cmd.createArgument().setValue("-jni");
    }
    cmd.addArguments(javah.getCurrentArgs());
    
    javah.logAndAddFiles(cmd);
    return cmd;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.javah.Gcjh
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */