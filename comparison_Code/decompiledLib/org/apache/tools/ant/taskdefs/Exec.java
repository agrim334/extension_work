package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Locale;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

@Deprecated
public class Exec
  extends Task
{
  private String os;
  private String out;
  private File dir;
  private String command;
  protected PrintWriter fos = null;
  private boolean failOnError = false;
  
  public Exec()
  {
    System.err.println("As of Ant 1.2 released in October 2000, the Exec class");
    
    System.err.println("is considered to be dead code by the Ant developers and is unmaintained.");
    
    System.err.println("Don't use it!");
  }
  
  public void execute()
    throws BuildException
  {
    run(command);
  }
  
  protected int run(String command)
    throws BuildException
  {
    int err = -1;
    
    String myos = System.getProperty("os.name");
    log("Myos = " + myos, 3);
    if ((os != null) && (!os.contains(myos)))
    {
      log("Not found in " + os, 3);
      return 0;
    }
    if (dir == null) {
      dir = getProject().getBaseDir();
    }
    if (myos.toLowerCase(Locale.ENGLISH).contains("windows"))
    {
      if (!dir.equals(getProject().resolveFile("."))) {
        if (myos.toLowerCase(Locale.ENGLISH).contains("nt"))
        {
          command = "cmd /c cd " + dir + " && " + command;
        }
        else
        {
          String ant = getProject().getProperty("ant.home");
          if (ant == null) {
            throw new BuildException("Property 'ant.home' not found", getLocation());
          }
          String antRun = getProject().resolveFile(ant + "/bin/antRun.bat").toString();
          command = antRun + " " + dir + " " + command;
        }
      }
    }
    else
    {
      String ant = getProject().getProperty("ant.home");
      if (ant == null) {
        throw new BuildException("Property 'ant.home' not found", getLocation());
      }
      String antRun = getProject().resolveFile(ant + "/bin/antRun").toString();
      
      command = antRun + " " + dir + " " + command;
    }
    try
    {
      log(command, 3);
      
      Process proc = Runtime.getRuntime().exec(command);
      if (out != null)
      {
        fos = new PrintWriter(new FileWriter(out));
        log("Output redirected to " + out, 3);
      }
      StreamPumper inputPumper = new StreamPumper(proc.getInputStream(), 2);
      
      StreamPumper errorPumper = new StreamPumper(proc.getErrorStream(), 1);
      
      inputPumper.start();
      errorPumper.start();
      
      proc.waitFor();
      inputPumper.join();
      errorPumper.join();
      proc.destroy();
      
      logFlush();
      
      err = proc.exitValue();
      if (err != 0)
      {
        if (failOnError) {
          throw new BuildException("Exec returned: " + err, getLocation());
        }
        log("Result: " + err, 0);
      }
    }
    catch (IOException ioe)
    {
      throw new BuildException("Error exec: " + command, ioe, getLocation());
    }
    catch (InterruptedException localInterruptedException) {}
    return err;
  }
  
  public void setDir(String d)
  {
    dir = getProject().resolveFile(d);
  }
  
  public void setOs(String os)
  {
    this.os = os;
  }
  
  public void setCommand(String command)
  {
    this.command = command;
  }
  
  public void setOutput(String out)
  {
    this.out = out;
  }
  
  public void setFailonerror(boolean fail)
  {
    failOnError = fail;
  }
  
  protected void outputLog(String line, int messageLevel)
  {
    if (fos == null) {
      log(line, messageLevel);
    } else {
      fos.println(line);
    }
  }
  
  protected void logFlush()
  {
    if (fos != null) {
      fos.close();
    }
  }
  
  class StreamPumper
    extends Thread
  {
    private BufferedReader din;
    private int messageLevel;
    private boolean endOfStream = false;
    private static final int SLEEP_TIME = 5;
    
    public StreamPumper(InputStream is, int messageLevel)
    {
      din = new BufferedReader(new InputStreamReader(is));
      this.messageLevel = messageLevel;
    }
    
    public void pumpStream()
      throws IOException
    {
      if (!endOfStream)
      {
        String line = din.readLine();
        if (line != null) {
          outputLog(line, messageLevel);
        } else {
          endOfStream = true;
        }
      }
    }
    
    public void run()
    {
      try
      {
        try
        {
          while (!endOfStream)
          {
            pumpStream();
            sleep(5L);
          }
        }
        catch (InterruptedException localInterruptedException) {}
        din.close();
      }
      catch (IOException localIOException) {}
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Exec
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */