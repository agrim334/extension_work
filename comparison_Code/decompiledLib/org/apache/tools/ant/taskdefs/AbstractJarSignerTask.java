package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.filters.LineContainsRegExp;
import org.apache.tools.ant.types.Commandline.Argument;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Environment.Variable;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.RedirectorElement;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.util.JavaEnvUtils;

public abstract class AbstractJarSignerTask
  extends Task
{
  public static final String ERROR_NO_SOURCE = "jar must be set through jar attribute or nested filesets";
  protected static final String JARSIGNER_COMMAND = "jarsigner";
  protected File jar;
  protected String alias;
  protected String keystore;
  protected String storepass;
  protected String storetype;
  protected String keypass;
  protected boolean verbose;
  protected boolean strict = false;
  protected String maxMemory;
  protected Vector<FileSet> filesets = new Vector();
  private RedirectorElement redirector;
  private Environment sysProperties = new Environment();
  private Path path = null;
  private String executable;
  private String providerName;
  private String providerClass;
  private String providerArg;
  private List<Commandline.Argument> additionalArgs = new ArrayList();
  
  public void setMaxmemory(String max)
  {
    maxMemory = max;
  }
  
  public void setJar(File jar)
  {
    this.jar = jar;
  }
  
  public void setAlias(String alias)
  {
    this.alias = alias;
  }
  
  public void setKeystore(String keystore)
  {
    this.keystore = keystore;
  }
  
  public void setStorepass(String storepass)
  {
    this.storepass = storepass;
  }
  
  public void setStoretype(String storetype)
  {
    this.storetype = storetype;
  }
  
  public void setKeypass(String keypass)
  {
    this.keypass = keypass;
  }
  
  public void setVerbose(boolean verbose)
  {
    this.verbose = verbose;
  }
  
  public void setStrict(boolean strict)
  {
    this.strict = strict;
  }
  
  public void addFileset(FileSet set)
  {
    filesets.addElement(set);
  }
  
  public void addSysproperty(Environment.Variable sysp)
  {
    sysProperties.addVariable(sysp);
  }
  
  public Path createPath()
  {
    if (path == null) {
      path = new Path(getProject());
    }
    return path.createPath();
  }
  
  public void setProviderName(String providerName)
  {
    this.providerName = providerName;
  }
  
  public void setProviderClass(String providerClass)
  {
    this.providerClass = providerClass;
  }
  
  public void setProviderArg(String providerArg)
  {
    this.providerArg = providerArg;
  }
  
  public void addArg(Commandline.Argument arg)
  {
    additionalArgs.add(arg);
  }
  
  protected void beginExecution()
  {
    redirector = createRedirector();
  }
  
  protected void endExecution()
  {
    redirector = null;
  }
  
  private RedirectorElement createRedirector()
  {
    RedirectorElement result = new RedirectorElement();
    if (storepass != null)
    {
      StringBuilder input = new StringBuilder(storepass).append('\n');
      if (keypass != null) {
        input.append(keypass).append('\n');
      }
      result.setInputString(input.toString());
      result.setLogInputString(false);
      
      LineContainsRegExp filter = new LineContainsRegExp();
      RegularExpression rx = new RegularExpression();
      
      rx.setPattern("^(Enter Passphrase for keystore: |Enter key password for .+: )$");
      filter.addConfiguredRegexp(rx);
      filter.setNegate(true);
      result.createErrorFilterChain().addLineContainsRegExp(filter);
    }
    return result;
  }
  
  public RedirectorElement getRedirector()
  {
    return redirector;
  }
  
  public void setExecutable(String executable)
  {
    this.executable = executable;
  }
  
  protected void setCommonOptions(ExecTask cmd)
  {
    if (maxMemory != null) {
      addValue(cmd, "-J-Xmx" + maxMemory);
    }
    if (verbose) {
      addValue(cmd, "-verbose");
    }
    if (strict) {
      addValue(cmd, "-strict");
    }
    for (Environment.Variable variable : sysProperties.getVariablesVector()) {
      declareSysProperty(cmd, variable);
    }
    for (Commandline.Argument arg : additionalArgs) {
      addArgument(cmd, arg);
    }
  }
  
  protected void declareSysProperty(ExecTask cmd, Environment.Variable property)
    throws BuildException
  {
    addValue(cmd, "-J-D" + property.getContent());
  }
  
  protected void bindToKeystore(ExecTask cmd)
  {
    if (null != keystore)
    {
      addValue(cmd, "-keystore");
      
      File keystoreFile = getProject().resolveFile(keystore);
      String loc;
      String loc;
      if (keystoreFile.exists()) {
        loc = keystoreFile.getPath();
      } else {
        loc = keystore;
      }
      addValue(cmd, loc);
    }
    if (null != storetype)
    {
      addValue(cmd, "-storetype");
      addValue(cmd, storetype);
    }
    if (null != providerName)
    {
      addValue(cmd, "-providerName");
      addValue(cmd, providerName);
    }
    if (null != providerClass)
    {
      addValue(cmd, "-providerClass");
      addValue(cmd, providerClass);
      if (null != providerArg)
      {
        addValue(cmd, "-providerArg");
        addValue(cmd, providerArg);
      }
    }
    else if (null != providerArg)
    {
      log("Ignoring providerArg as providerClass has not been set");
    }
  }
  
  protected ExecTask createJarSigner()
  {
    ExecTask cmd = new ExecTask(this);
    if (executable == null) {
      cmd.setExecutable(JavaEnvUtils.getJdkExecutable("jarsigner"));
    } else {
      cmd.setExecutable(executable);
    }
    cmd.setTaskType("jarsigner");
    cmd.setFailonerror(true);
    cmd.addConfiguredRedirector(redirector);
    return cmd;
  }
  
  protected Vector<FileSet> createUnifiedSources()
  {
    Vector<FileSet> sources = new Vector(filesets);
    if (jar != null)
    {
      FileSet sourceJar = new FileSet();
      sourceJar.setProject(getProject());
      sourceJar.setFile(jar);
      sources.add(sourceJar);
    }
    return sources;
  }
  
  protected Path createUnifiedSourcePath()
  {
    Path p = path == null ? new Path(getProject()) : (Path)path.clone();
    for (FileSet fileSet : createUnifiedSources()) {
      p.add(fileSet);
    }
    return p;
  }
  
  protected boolean hasResources()
  {
    return (path != null) || (!filesets.isEmpty());
  }
  
  protected void addValue(ExecTask cmd, String value)
  {
    cmd.createArg().setValue(value);
  }
  
  protected void addArgument(ExecTask cmd, Commandline.Argument arg)
  {
    cmd.createArg().copyFrom(arg);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.AbstractJarSignerTask
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */