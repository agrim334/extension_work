package org.apache.tools.ant.taskdefs.compilers;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.Path;

public class Gcj
  extends DefaultCompilerAdapter
{
  private static final String[] CONFLICT_WITH_DASH_C = { "-o", "--main=", "-D", "-fjni", "-L" };
  
  public boolean execute()
    throws BuildException
  {
    attributes.log("Using gcj compiler", 3);
    Commandline cmd = setupGCJCommand();
    
    int firstFileName = cmd.size();
    logAndAddFilesToCompile(cmd);
    
    return 
      executeExternalCompile(cmd.getCommandline(), firstFileName) == 0;
  }
  
  protected Commandline setupGCJCommand()
  {
    Commandline cmd = new Commandline();
    Path classpath = new Path(project);
    
    Path p = getBootClassPath();
    if (!p.isEmpty()) {
      classpath.append(p);
    }
    if ((extdirs != null) || (includeJavaRuntime)) {
      classpath.addExtdirs(extdirs);
    }
    classpath.append(getCompileClasspath());
    if (compileSourcepath != null) {
      classpath.append(compileSourcepath);
    } else {
      classpath.append(src);
    }
    String exec = getJavac().getExecutable();
    cmd.setExecutable(exec == null ? "gcj" : exec);
    if (destDir != null)
    {
      cmd.createArgument().setValue("-d");
      cmd.createArgument().setFile(destDir);
      if ((!destDir.exists()) && 
        (!destDir.mkdirs()) && (!destDir.isDirectory())) {
        throw new BuildException("Can't make output directories. Maybe permission is wrong.");
      }
    }
    cmd.createArgument().setValue("-classpath");
    cmd.createArgument().setPath(classpath);
    if (encoding != null) {
      cmd.createArgument().setValue("--encoding=" + encoding);
    }
    if (debug) {
      cmd.createArgument().setValue("-g1");
    }
    if (optimize) {
      cmd.createArgument().setValue("-O");
    }
    if (!isNativeBuild()) {
      cmd.createArgument().setValue("-C");
    }
    if (attributes.getSource() != null)
    {
      String source = attributes.getSource();
      cmd.createArgument().setValue("-fsource=" + source);
    }
    if (attributes.getTarget() != null)
    {
      String target = attributes.getTarget();
      cmd.createArgument().setValue("-ftarget=" + target);
    }
    addCurrentCompilerArgs(cmd);
    
    return cmd;
  }
  
  public boolean isNativeBuild()
  {
    boolean nativeBuild = false;
    String[] additionalArguments = getJavac().getCurrentCompilerArgs();
    int argsLength = 0;
    while ((!nativeBuild) && (argsLength < additionalArguments.length))
    {
      int conflictLength = 0;
      while ((!nativeBuild) && (conflictLength < CONFLICT_WITH_DASH_C.length))
      {
        nativeBuild = additionalArguments[argsLength].startsWith(CONFLICT_WITH_DASH_C[conflictLength]);
        conflictLength++;
      }
      argsLength++;
    }
    return nativeBuild;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.compilers.Gcj
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */