package org.apache.tools.ant.taskdefs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.util.FileUtils;

@Deprecated
public class Jikes
{
  private static final int MAX_FILES_ON_COMMAND_LINE = 250;
  protected JikesOutputParser jop;
  protected String command;
  protected Project project;
  
  protected Jikes(JikesOutputParser jop, String command, Project project)
  {
    System.err.println("As of Ant 1.2 released in October 2000, the Jikes class");
    
    System.err.println("is considered to be dead code by the Ant developers and is unmaintained.");
    
    System.err.println("Don't use it!");
    
    this.jop = jop;
    this.command = command;
    this.project = project;
  }
  
  protected void compile(String[] args)
  {
    String[] commandArray = null;
    File tmpFile = null;
    try
    {
      if ((Os.isFamily("windows")) && (args.length > 250))
      {
        tmpFile = FileUtils.getFileUtils().createTempFile(project, "jikes", "tmp", null, false, true);
        try
        {
          BufferedWriter out = new BufferedWriter(new FileWriter(tmpFile));
          try
          {
            for (String arg : args)
            {
              out.write(arg);
              out.newLine();
            }
            out.flush();
            
            commandArray = new String[] { command, "@" + tmpFile.getAbsolutePath() };
            out.close();
          }
          catch (Throwable localThrowable2) {}
          try
          {
            out.close();
          }
          catch (Throwable localThrowable3)
          {
            localThrowable2.addSuppressed(localThrowable3);
          }
          throw localThrowable2;
        }
        catch (IOException e)
        {
          throw new BuildException("Error creating temporary file", e);
        }
        break label211;
      }
      commandArray = new String[args.length + 1];
      commandArray[0] = command;
      System.arraycopy(args, 0, commandArray, 1, args.length);
      try
      {
        label211:
        Execute exe = new Execute(jop);
        exe.setAntRun(project);
        exe.setWorkingDirectory(project.getBaseDir());
        exe.setCommandline(commandArray);
        exe.execute();
      }
      catch (IOException e)
      {
        throw new BuildException("Error running Jikes compiler", e);
      }
    }
    finally
    {
      if ((tmpFile != null) && (!tmpFile.delete())) {
        tmpFile.deleteOnExit();
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Jikes
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */