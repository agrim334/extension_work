package org.apache.tools.ant.taskdefs.compilers;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JavaEnvUtils;

public class JavacExternal
  extends DefaultCompilerAdapter
{
  public boolean execute()
    throws BuildException
  {
    attributes.log("Using external javac compiler", 3);
    
    Commandline cmd = new Commandline();
    cmd.setExecutable(getJavac().getJavacExecutable());
    if (assumeJava1_3Plus()) {
      setupModernJavacCommandlineSwitches(cmd);
    } else {
      setupJavacCommandlineSwitches(cmd, true);
    }
    int openVmsFirstFileName = assumeJava1_2Plus() ? cmd.size() : -1;
    
    logAndAddFilesToCompile(cmd);
    if (Os.isFamily("openvms")) {
      return execOnVMS(cmd, openVmsFirstFileName);
    }
    String[] commandLine = cmd.getCommandline();
    int firstFileName;
    int firstFileName;
    if (assumeJava1_2Plus()) {
      firstFileName = moveArgFileEligibleOptionsToEnd(commandLine);
    } else {
      firstFileName = -1;
    }
    return executeExternalCompile(commandLine, firstFileName, true) == 0;
  }
  
  private int moveArgFileEligibleOptionsToEnd(String[] commandLine)
  {
    int nonArgFileOptionIdx = 1;
    while ((nonArgFileOptionIdx < commandLine.length) && 
      (!isArgFileEligible(commandLine[nonArgFileOptionIdx]))) {
      nonArgFileOptionIdx++;
    }
    for (int i = nonArgFileOptionIdx + 1; i < commandLine.length; i++) {
      if (!isArgFileEligible(commandLine[i]))
      {
        String option = commandLine[i];
        for (int j = i - 1; j >= nonArgFileOptionIdx; j--) {
          commandLine[(j + 1)] = commandLine[j];
        }
        commandLine[nonArgFileOptionIdx] = option;
        nonArgFileOptionIdx++;
      }
    }
    return nonArgFileOptionIdx;
  }
  
  private static boolean isArgFileEligible(String option)
  {
    return (!option.startsWith("-J")) && (!option.startsWith("@"));
  }
  
  private boolean execOnVMS(Commandline cmd, int firstFileName)
  {
    File vmsFile = null;
    try
    {
      vmsFile = JavaEnvUtils.createVmsJavaOptionFile(cmd.getArguments());
      
      String[] commandLine = { cmd.getExecutable(), "-V", vmsFile.getPath() };
      return 0 == executeExternalCompile(commandLine, firstFileName, true);
    }
    catch (IOException e)
    {
      throw new BuildException("Failed to create a temporary file for \"-V\" switch");
    }
    finally
    {
      FileUtils.delete(vmsFile);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.compilers.JavacExternal
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */