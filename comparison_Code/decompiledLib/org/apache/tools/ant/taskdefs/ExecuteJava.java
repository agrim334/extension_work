package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.CommandlineJava.SysProperties;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Permissions;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.TimeoutObserver;
import org.apache.tools.ant.util.Watchdog;

public class ExecuteJava
  implements Runnable, TimeoutObserver
{
  private Commandline javaCommand = null;
  private Path classpath = null;
  private CommandlineJava.SysProperties sysProperties = null;
  private Permissions perm = null;
  private Method main = null;
  private Long timeout = null;
  private volatile Throwable caught = null;
  private volatile boolean timedOut = false;
  private boolean done = false;
  private Thread thread = null;
  
  public void setJavaCommand(Commandline javaCommand)
  {
    this.javaCommand = javaCommand;
  }
  
  public void setClasspath(Path p)
  {
    classpath = p;
  }
  
  public void setSystemProperties(CommandlineJava.SysProperties s)
  {
    sysProperties = s;
  }
  
  public void setPermissions(Permissions permissions)
  {
    perm = permissions;
  }
  
  @Deprecated
  public void setOutput(PrintStream out) {}
  
  public void setTimeout(Long timeout)
  {
    this.timeout = timeout;
  }
  
  public void execute(Project project)
    throws BuildException
  {
    String classname = javaCommand.getExecutable();
    
    AntClassLoader loader = null;
    try
    {
      if (sysProperties != null) {
        sysProperties.setSystem();
      }
      try
      {
        Class<?> target;
        if (classpath == null)
        {
          target = Class.forName(classname);
        }
        else
        {
          loader = project.createClassLoader(classpath);
          loader.setParent(project.getCoreLoader());
          loader.setParentFirst(false);
          loader.addJavaLibraries();
          loader.setIsolated(true);
          loader.setThreadContextLoader();
          loader.forceLoadClass(classname);
          target = Class.forName(classname, true, loader);
        }
      }
      catch (ClassNotFoundException e)
      {
        Class<?> target;
        throw new BuildException("Could not find %s. Make sure you have it in your classpath", new Object[] { classname });
      }
      Class<?> target;
      main = target.getMethod("main", new Class[] { String[].class });
      if (main == null) {
        throw new BuildException("Could not find main() method in %s", new Object[] { classname });
      }
      if ((main.getModifiers() & 0x8) == 0) {
        throw new BuildException("main() method in %s is not declared static", new Object[] { classname });
      }
      if (timeout == null)
      {
        run();
      }
      else
      {
        thread = new Thread(this, "ExecuteJava");
        
        Task currentThreadTask = project.getThreadTask(Thread.currentThread());
        
        project.registerThreadTask(thread, currentThreadTask);
        
        thread.setDaemon(true);
        Watchdog w = new Watchdog(timeout.longValue());
        w.addTimeoutObserver(this);
        synchronized (this)
        {
          thread.start();
          w.start();
          try
          {
            while (!done) {
              wait();
            }
          }
          catch (InterruptedException localInterruptedException) {}
          if (timedOut)
          {
            project.log("Timeout: sub-process interrupted", 1);
          }
          else
          {
            thread = null;
            w.stop();
          }
        }
      }
      if (caught != null) {
        throw caught;
      }
    }
    catch (BuildException|ThreadDeath|SecurityException e)
    {
      throw e;
    }
    catch (Throwable e)
    {
      throw new BuildException(e);
    }
    finally
    {
      if (loader != null)
      {
        loader.resetThreadContextLoader();
        loader.cleanup();
        loader = null;
      }
      if (sysProperties != null) {
        sysProperties.restoreSystem();
      }
    }
  }
  
  public void run()
  {
    Object[] argument = { javaCommand.getArguments() };
    try
    {
      if (perm != null) {
        perm.setSecurityManager();
      }
      main.invoke(null, argument);
    }
    catch (InvocationTargetException e)
    {
      Throwable t = e.getTargetException();
      if (!(t instanceof InterruptedException)) {
        caught = t;
      }
    }
    catch (Throwable t)
    {
      caught = t;
    }
    finally
    {
      if (perm != null) {
        perm.restoreSecurityManager();
      }
      synchronized (this)
      {
        done = true;
        notifyAll();
      }
    }
  }
  
  public synchronized void timeoutOccured(Watchdog w)
  {
    if (thread != null)
    {
      timedOut = true;
      thread.interrupt();
    }
    done = true;
    notifyAll();
  }
  
  public synchronized boolean killedProcess()
  {
    return timedOut;
  }
  
  public int fork(ProjectComponent pc)
    throws BuildException
  {
    CommandlineJava cmdl = new CommandlineJava();
    cmdl.setClassname(javaCommand.getExecutable());
    String arg;
    for (arg : javaCommand.getArguments()) {
      cmdl.createArgument().setValue(arg);
    }
    if (classpath != null) {
      cmdl.createClasspath(pc.getProject()).append(classpath);
    }
    if (sysProperties != null) {
      cmdl.addSysproperties(sysProperties);
    }
    Redirector redirector = new Redirector(pc);
    
    Execute exe = new Execute(redirector.createHandler(), timeout == null ? null : new ExecuteWatchdog(timeout.longValue()));
    exe.setAntRun(pc.getProject());
    if (Os.isFamily("openvms")) {
      setupCommandLineForVMS(exe, cmdl.getCommandline());
    } else {
      exe.setCommandline(cmdl.getCommandline());
    }
    try
    {
      int rc = exe.execute();
      redirector.complete();
      return rc;
    }
    catch (IOException e)
    {
      throw new BuildException(e);
    }
    finally
    {
      timedOut = exe.killedProcess();
    }
  }
  
  public static void setupCommandLineForVMS(Execute exe, String[] command)
  {
    exe.setVMLauncher(true);
    File vmsJavaOptionFile = null;
    try
    {
      String[] args = new String[command.length - 1];
      System.arraycopy(command, 1, args, 0, command.length - 1);
      vmsJavaOptionFile = JavaEnvUtils.createVmsJavaOptionFile(args);
      
      vmsJavaOptionFile.deleteOnExit();
      String[] vmsCmd = { command[0], "-V", vmsJavaOptionFile.getPath() };
      exe.setCommandline(vmsCmd);
    }
    catch (IOException e)
    {
      throw new BuildException("Failed to create a temporary file for \"-V\" switch");
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.ExecuteJava
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */