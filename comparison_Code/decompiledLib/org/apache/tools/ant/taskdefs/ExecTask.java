package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Environment.Variable;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.RedirectorElement;
import org.apache.tools.ant.util.FileUtils;

public class ExecTask
  extends Task
{
  private static final FileUtils FILE_UTILS = ;
  private String os;
  private String osFamily;
  private File dir;
  protected boolean failOnError = false;
  protected boolean newEnvironment = false;
  private Long timeout = null;
  private Environment env = new Environment();
  protected Commandline cmdl = new Commandline();
  private String resultProperty;
  private boolean failIfExecFails = true;
  private String executable;
  private boolean resolveExecutable = false;
  private boolean searchPath = false;
  private boolean spawn = false;
  private boolean incompatibleWithSpawn = false;
  private String inputString;
  private File input;
  private File output;
  private File error;
  protected Redirector redirector = new Redirector(this);
  protected RedirectorElement redirectorElement;
  private boolean vmLauncher = true;
  
  public ExecTask() {}
  
  public ExecTask(Task owner)
  {
    bindToOwner(owner);
  }
  
  public void setSpawn(boolean spawn)
  {
    this.spawn = spawn;
  }
  
  public void setTimeout(Long value)
  {
    timeout = value;
    incompatibleWithSpawn |= timeout != null;
  }
  
  public void setTimeout(Integer value)
  {
    setTimeout(
      value == null ? null : Long.valueOf(value.intValue()));
  }
  
  public void setExecutable(String value)
  {
    executable = value;
    cmdl.setExecutable(value);
  }
  
  public void setDir(File d)
  {
    dir = d;
  }
  
  public void setOs(String os)
  {
    this.os = os;
  }
  
  public final String getOs()
  {
    return os;
  }
  
  public void setCommand(Commandline cmdl)
  {
    log("The command attribute is deprecated.\nPlease use the executable attribute and nested arg elements.", 1);
    
    this.cmdl = cmdl;
  }
  
  public void setOutput(File out)
  {
    output = out;
    incompatibleWithSpawn = true;
  }
  
  public void setInput(File input)
  {
    if (inputString != null) {
      throw new BuildException("The \"input\" and \"inputstring\" attributes cannot both be specified");
    }
    this.input = input;
    incompatibleWithSpawn = true;
  }
  
  public void setInputString(String inputString)
  {
    if (input != null) {
      throw new BuildException("The \"input\" and \"inputstring\" attributes cannot both be specified");
    }
    this.inputString = inputString;
    incompatibleWithSpawn = true;
  }
  
  public void setLogError(boolean logError)
  {
    redirector.setLogError(logError);
    incompatibleWithSpawn |= logError;
  }
  
  public void setError(File error)
  {
    this.error = error;
    incompatibleWithSpawn = true;
  }
  
  public void setOutputproperty(String outputProp)
  {
    redirector.setOutputProperty(outputProp);
    incompatibleWithSpawn = true;
  }
  
  public void setErrorProperty(String errorProperty)
  {
    redirector.setErrorProperty(errorProperty);
    incompatibleWithSpawn = true;
  }
  
  public void setFailonerror(boolean fail)
  {
    failOnError = fail;
    incompatibleWithSpawn |= fail;
  }
  
  public void setNewenvironment(boolean newenv)
  {
    newEnvironment = newenv;
  }
  
  public void setResolveExecutable(boolean resolveExecutable)
  {
    this.resolveExecutable = resolveExecutable;
  }
  
  public void setSearchPath(boolean searchPath)
  {
    this.searchPath = searchPath;
  }
  
  public boolean getResolveExecutable()
  {
    return resolveExecutable;
  }
  
  public void addEnv(Environment.Variable var)
  {
    env.addVariable(var);
  }
  
  public Commandline.Argument createArg()
  {
    return cmdl.createArgument();
  }
  
  public void setResultProperty(String resultProperty)
  {
    this.resultProperty = resultProperty;
    incompatibleWithSpawn = true;
  }
  
  protected void maybeSetResultPropertyValue(int result)
  {
    if (resultProperty != null)
    {
      String res = Integer.toString(result);
      getProject().setNewProperty(resultProperty, res);
    }
  }
  
  public void setFailIfExecutionFails(boolean flag)
  {
    failIfExecFails = flag;
    incompatibleWithSpawn |= flag;
  }
  
  public void setAppend(boolean append)
  {
    redirector.setAppend(append);
    incompatibleWithSpawn |= append;
  }
  
  public void addConfiguredRedirector(RedirectorElement redirectorElement)
  {
    if (this.redirectorElement != null) {
      throw new BuildException("cannot have > 1 nested <redirector>s");
    }
    this.redirectorElement = redirectorElement;
    incompatibleWithSpawn = true;
  }
  
  public void setOsFamily(String osFamily)
  {
    this.osFamily = osFamily.toLowerCase(Locale.ENGLISH);
  }
  
  public final String getOsFamily()
  {
    return osFamily;
  }
  
  protected String resolveExecutable(String exec, boolean mustSearchPath)
  {
    if (!resolveExecutable) {
      return exec;
    }
    File executableFile = getProject().resolveFile(exec);
    if (executableFile.exists()) {
      return executableFile.getAbsolutePath();
    }
    if (dir != null)
    {
      executableFile = FILE_UTILS.resolveFile(dir, exec);
      if (executableFile.exists()) {
        return executableFile.getAbsolutePath();
      }
    }
    if (mustSearchPath)
    {
      Path p = null;
      String[] environment = env.getVariables();
      if (environment != null) {
        for (String variable : environment) {
          if (isPath(variable))
          {
            p = new Path(getProject(), getPath(variable));
            break;
          }
        }
      }
      Object path;
      if (p == null)
      {
        path = getPath(Execute.getEnvironmentVariables());
        if (path != null) {
          p = new Path(getProject(), (String)path);
        }
      }
      if (p != null) {
        for (String pathname : p.list())
        {
          executableFile = FILE_UTILS.resolveFile(new File(pathname), exec);
          if (executableFile.exists()) {
            return executableFile.getAbsolutePath();
          }
        }
      }
    }
    return exec;
  }
  
  /* Error */
  public void execute()
    throws BuildException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 231	org/apache/tools/ant/taskdefs/ExecTask:isValidOs	()Z
    //   4: ifne +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: getfield 88	org/apache/tools/ant/taskdefs/ExecTask:dir	Ljava/io/File;
    //   12: astore_1
    //   13: aload_0
    //   14: getfield 30	org/apache/tools/ant/taskdefs/ExecTask:cmdl	Lorg/apache/tools/ant/types/Commandline;
    //   17: aload_0
    //   18: aload_0
    //   19: getfield 80	org/apache/tools/ant/taskdefs/ExecTask:executable	Ljava/lang/String;
    //   22: aload_0
    //   23: getfield 40	org/apache/tools/ant/taskdefs/ExecTask:searchPath	Z
    //   26: invokevirtual 234	org/apache/tools/ant/taskdefs/ExecTask:resolveExecutable	(Ljava/lang/String;Z)Ljava/lang/String;
    //   29: invokevirtual 84	org/apache/tools/ant/types/Commandline:setExecutable	(Ljava/lang/String;)V
    //   32: aload_0
    //   33: invokevirtual 237	org/apache/tools/ant/taskdefs/ExecTask:checkConfiguration	()V
    //   36: aload_0
    //   37: aload_0
    //   38: invokevirtual 240	org/apache/tools/ant/taskdefs/ExecTask:prepareExec	()Lorg/apache/tools/ant/taskdefs/Execute;
    //   41: invokevirtual 244	org/apache/tools/ant/taskdefs/ExecTask:runExec	(Lorg/apache/tools/ant/taskdefs/Execute;)V
    //   44: aload_0
    //   45: aload_1
    //   46: putfield 88	org/apache/tools/ant/taskdefs/ExecTask:dir	Ljava/io/File;
    //   49: goto +11 -> 60
    //   52: astore_2
    //   53: aload_0
    //   54: aload_1
    //   55: putfield 88	org/apache/tools/ant/taskdefs/ExecTask:dir	Ljava/io/File;
    //   58: aload_2
    //   59: athrow
    //   60: return
    // Line number table:
    //   Java source line #490	-> byte code offset #0
    //   Java source line #491	-> byte code offset #7
    //   Java source line #493	-> byte code offset #8
    //   Java source line #494	-> byte code offset #13
    //   Java source line #495	-> byte code offset #32
    //   Java source line #497	-> byte code offset #36
    //   Java source line #499	-> byte code offset #44
    //   Java source line #500	-> byte code offset #49
    //   Java source line #499	-> byte code offset #52
    //   Java source line #500	-> byte code offset #58
    //   Java source line #501	-> byte code offset #60
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	61	0	this	ExecTask
    //   12	43	1	savedDir	File
    //   52	7	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   36	44	52	finally
  }
  
  protected void checkConfiguration()
    throws BuildException
  {
    if (cmdl.getExecutable() == null) {
      throw new BuildException("no executable specified", getLocation());
    }
    if ((dir != null) && (!dir.exists())) {
      throw new BuildException("The directory " + dir + " does not exist");
    }
    if ((dir != null) && (!dir.isDirectory())) {
      throw new BuildException(dir + " is not a directory");
    }
    if ((spawn) && (incompatibleWithSpawn))
    {
      getProject().log("spawn does not allow attributes related to input, output, error, result", 0);
      
      getProject().log("spawn also does not allow timeout", 0);
      getProject().log("finally, spawn is not compatible with a nested I/O <redirector>", 0);
      
      throw new BuildException("You have used an attribute or nested element which is not compatible with spawn");
    }
    setupRedirector();
  }
  
  protected void setupRedirector()
  {
    redirector.setInput(input);
    redirector.setInputString(inputString);
    redirector.setOutput(output);
    redirector.setError(error);
  }
  
  protected boolean isValidOs()
  {
    if ((osFamily != null) && (!Os.isFamily(osFamily))) {
      return false;
    }
    String myos = System.getProperty("os.name");
    log("Current OS is " + myos, 3);
    if ((os != null) && (!os.contains(myos)))
    {
      log("This OS, " + myos + " was not found in the specified list of valid OSes: " + os, 3);
      
      return false;
    }
    return true;
  }
  
  public void setVMLauncher(boolean vmLauncher)
  {
    this.vmLauncher = vmLauncher;
  }
  
  protected Execute prepareExec()
    throws BuildException
  {
    if (dir == null) {
      dir = getProject().getBaseDir();
    }
    if (redirectorElement != null) {
      redirectorElement.configure(redirector);
    }
    Execute exe = new Execute(createHandler(), createWatchdog());
    exe.setAntRun(getProject());
    exe.setWorkingDirectory(dir);
    exe.setVMLauncher(vmLauncher);
    String[] environment = env.getVariables();
    if (environment != null) {
      for (String variable : environment) {
        log("Setting environment variable: " + variable, 3);
      }
    }
    exe.setNewenvironment(newEnvironment);
    exe.setEnvironment(environment);
    return exe;
  }
  
  protected final void runExecute(Execute exe)
    throws IOException
  {
    int returnCode = -1;
    if (!spawn)
    {
      returnCode = exe.execute();
      if (exe.killedProcess())
      {
        String msg = "Timeout: killed the sub-process";
        if (failOnError) {
          throw new BuildException(msg);
        }
        log(msg, 1);
      }
      maybeSetResultPropertyValue(returnCode);
      redirector.complete();
      if (Execute.isFailure(returnCode))
      {
        if (failOnError) {
          throw new BuildException(getTaskType() + " returned: " + returnCode, getLocation());
        }
        log("Result: " + returnCode, 0);
      }
    }
    else
    {
      exe.spawn();
    }
  }
  
  protected void runExec(Execute exe)
    throws BuildException
  {
    log(cmdl.describeCommand(), 3);
    
    exe.setCommandline(cmdl.getCommandline());
    try
    {
      runExecute(exe);
    }
    catch (IOException e)
    {
      if (failIfExecFails) {
        throw new BuildException("Execute failed: " + e.toString(), e, getLocation());
      }
      log("Execute failed: " + e.toString(), 0);
    }
    finally
    {
      logFlush();
    }
  }
  
  protected ExecuteStreamHandler createHandler()
    throws BuildException
  {
    return redirector.createHandler();
  }
  
  protected ExecuteWatchdog createWatchdog()
    throws BuildException
  {
    return timeout == null ? 
      null : new ExecuteWatchdog(timeout.longValue());
  }
  
  protected void logFlush() {}
  
  private boolean isPath(String line)
  {
    return (line.startsWith("PATH=")) || 
      (line.startsWith("Path="));
  }
  
  private String getPath(String line)
  {
    return line.substring("PATH=".length());
  }
  
  private String getPath(Map<String, String> map)
  {
    String p = (String)map.get("PATH");
    return p != null ? p : (String)map.get("Path");
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.ExecTask
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */