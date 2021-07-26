package org.apache.tools.ant.taskdefs.compilers;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Commandline;

public class Sj
  extends DefaultCompilerAdapter
{
  public boolean execute()
    throws BuildException
  {
    attributes.log("Using symantec java compiler", 3);
    
    Commandline cmd = setupJavacCommand();
    String exec = getJavac().getExecutable();
    cmd.setExecutable(exec == null ? "sj" : exec);
    
    int firstFileName = cmd.size() - compileList.length;
    
    return 
      executeExternalCompile(cmd.getCommandline(), firstFileName) == 0;
  }
  
  protected String getNoDebugArgument()
  {
    return null;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.compilers.Sj
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */