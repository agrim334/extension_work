package org.apache.tools.ant.taskdefs.compilers;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.ExecuteJava;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.Path;

public class Kjc
  extends DefaultCompilerAdapter
{
  public boolean execute()
    throws BuildException
  {
    attributes.log("Using kjc compiler", 3);
    Commandline cmd = setupKjcCommand();
    cmd.setExecutable("at.dms.kjc.Main");
    ExecuteJava ej = new ExecuteJava();
    ej.setJavaCommand(cmd);
    return ej.fork(getJavac()) == 0;
  }
  
  protected Commandline setupKjcCommand()
  {
    Commandline cmd = new Commandline();
    
    Path classpath = getCompileClasspath();
    if (deprecation) {
      cmd.createArgument().setValue("-deprecation");
    }
    if (destDir != null)
    {
      cmd.createArgument().setValue("-d");
      cmd.createArgument().setFile(destDir);
    }
    cmd.createArgument().setValue("-classpath");
    
    Path cp = new Path(project);
    
    Path p = getBootClassPath();
    if (!p.isEmpty()) {
      cp.append(p);
    }
    if (extdirs != null) {
      cp.addExtdirs(extdirs);
    }
    cp.append(classpath);
    if (compileSourcepath != null) {
      cp.append(compileSourcepath);
    } else {
      cp.append(src);
    }
    cmd.createArgument().setPath(cp);
    if (encoding != null)
    {
      cmd.createArgument().setValue("-encoding");
      cmd.createArgument().setValue(encoding);
    }
    if (debug) {
      cmd.createArgument().setValue("-g");
    }
    if (optimize) {
      cmd.createArgument().setValue("-O2");
    }
    if (verbose) {
      cmd.createArgument().setValue("-verbose");
    }
    addCurrentCompilerArgs(cmd);
    
    logAndAddFilesToCompile(cmd);
    return cmd;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.compilers.Kjc
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */