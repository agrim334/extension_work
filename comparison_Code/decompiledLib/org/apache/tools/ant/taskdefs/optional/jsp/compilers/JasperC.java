package org.apache.tools.ant.taskdefs.optional.jsp.compilers;

import java.io.File;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.optional.jsp.JspC;
import org.apache.tools.ant.taskdefs.optional.jsp.JspC.WebAppParameter;
import org.apache.tools.ant.taskdefs.optional.jsp.JspMangler;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Path;

public class JasperC
  extends DefaultJspCompilerAdapter
{
  JspMangler mangler;
  
  public JasperC(JspMangler mangler)
  {
    this.mangler = mangler;
  }
  
  public boolean execute()
    throws BuildException
  {
    getJspc().log("Using jasper compiler", 3);
    CommandlineJava cmd = setupJasperCommand();
    try
    {
      Java java = new Java(owner);
      Path p = getClasspath();
      if (getJspc().getClasspath() != null) {
        getProject().log("using user supplied classpath: " + p, 4);
      } else {
        getProject().log("using system classpath: " + p, 4);
      }
      java.setClasspath(p);
      java.setDir(getProject().getBaseDir());
      java.setClassname("org.apache.jasper.JspC");
      for (String arg : cmd.getJavaCommand().getArguments()) {
        java.createArg().setValue(arg);
      }
      java.setFailonerror(getJspc().getFailonerror());
      
      java.setFork(true);
      java.setTaskName("jasperc");
      java.execute();
      return true;
    }
    catch (Exception ex)
    {
      if ((ex instanceof BuildException)) {
        throw ((BuildException)ex);
      }
      throw new BuildException("Error running jsp compiler: ", ex, getJspc().getLocation());
    }
    finally
    {
      getJspc().deleteEmptyJavaFiles();
    }
  }
  
  private CommandlineJava setupJasperCommand()
  {
    CommandlineJava cmd = new CommandlineJava();
    JspC jspc = getJspc();
    addArg(cmd, "-d", jspc.getDestdir());
    addArg(cmd, "-p", jspc.getPackage());
    if (!isTomcat5x()) {
      addArg(cmd, "-v" + jspc.getVerbose());
    } else {
      getProject().log("this task doesn't support Tomcat 5.x properly, please use the Tomcat provided jspc task instead");
    }
    addArg(cmd, "-uriroot", jspc.getUriroot());
    addArg(cmd, "-uribase", jspc.getUribase());
    addArg(cmd, "-ieplugin", jspc.getIeplugin());
    addArg(cmd, "-webinc", jspc.getWebinc());
    addArg(cmd, "-webxml", jspc.getWebxml());
    addArg(cmd, "-die9");
    if (jspc.isMapped()) {
      addArg(cmd, "-mapped");
    }
    if (jspc.getWebApp() != null)
    {
      File dir = jspc.getWebApp().getDirectory();
      addArg(cmd, "-webapp", dir);
    }
    logAndAddFilesToCompile(getJspc(), getJspc().getCompileList(), cmd);
    return cmd;
  }
  
  public JspMangler createMangler()
  {
    return mangler;
  }
  
  private Path getClasspath()
  {
    Path p = getJspc().getClasspath();
    if (p == null)
    {
      p = new Path(getProject());
      return p.concatSystemClasspath("only");
    }
    return p.concatSystemClasspath("ignore");
  }
  
  private boolean isTomcat5x()
  {
    try
    {
      AntClassLoader l = getProject().createClassLoader(getClasspath());
      try
      {
        l.loadClass("org.apache.jasper.tagplugins.jstl.If");
        boolean bool = true;
        if (l != null) {
          l.close();
        }
        return bool;
      }
      catch (Throwable localThrowable2)
      {
        if (l != null) {
          try
          {
            l.close();
          }
          catch (Throwable localThrowable1)
          {
            localThrowable2.addSuppressed(localThrowable1);
          }
        }
        throw localThrowable2;
      }
      return false;
    }
    catch (ClassNotFoundException e) {}
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.jsp.compilers.JasperC
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */