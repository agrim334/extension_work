package org.apache.tools.ant.types;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.JavaEnvUtils;

public class CommandlineJava
  implements Cloneable
{
  private Commandline vmCommand = new Commandline();
  private Commandline javaCommand = new Commandline();
  private SysProperties sysProperties = new SysProperties();
  private Path classpath = null;
  private Path bootclasspath = null;
  private Path modulepath = null;
  private Path upgrademodulepath = null;
  private String vmVersion;
  private String maxMemory = null;
  private Assertions assertions = null;
  private ExecutableType executableType;
  private boolean cloneVm = false;
  
  public static class SysProperties
    extends Environment
    implements Cloneable
  {
    Properties sys = null;
    private Vector<PropertySet> propertySets = new Vector();
    
    public String[] getVariables()
      throws BuildException
    {
      List<String> definitions = new LinkedList();
      addDefinitionsToList(definitions.listIterator());
      if (definitions.isEmpty()) {
        return null;
      }
      return (String[])definitions.toArray(new String[definitions.size()]);
    }
    
    public void addDefinitionsToList(ListIterator<String> listIt)
    {
      String[] props = super.getVariables();
      if (props != null) {
        for (String prop : props) {
          listIt.add("-D" + prop);
        }
      }
      Properties propertySetProperties = mergePropertySets();
      for (String key : propertySetProperties.stringPropertyNames()) {
        listIt.add("-D" + key + "=" + propertySetProperties.getProperty(key));
      }
    }
    
    public int size()
    {
      Properties p = mergePropertySets();
      return variables.size() + p.size();
    }
    
    public void setSystem()
      throws BuildException
    {
      try
      {
        sys = System.getProperties();
        Properties p = new Properties();
        for (String name : sys.stringPropertyNames())
        {
          String value = sys.getProperty(name);
          if (value != null) {
            p.put(name, value);
          }
        }
        p.putAll(mergePropertySets());
        for (Environment.Variable v : variables)
        {
          v.validate();
          p.put(v.getKey(), v.getValue());
        }
        System.setProperties(p);
      }
      catch (SecurityException e)
      {
        throw new BuildException("Cannot modify system properties", e);
      }
    }
    
    public void restoreSystem()
      throws BuildException
    {
      if (sys == null) {
        throw new BuildException("Unbalanced nesting of SysProperties");
      }
      try
      {
        System.setProperties(sys);
        sys = null;
      }
      catch (SecurityException e)
      {
        throw new BuildException("Cannot modify system properties", e);
      }
    }
    
    public Object clone()
      throws CloneNotSupportedException
    {
      try
      {
        SysProperties c = (SysProperties)super.clone();
        variables = ((Vector)variables.clone());
        propertySets = ((Vector)propertySets.clone());
        return c;
      }
      catch (CloneNotSupportedException e) {}
      return null;
    }
    
    public void addSyspropertyset(PropertySet ps)
    {
      propertySets.addElement(ps);
    }
    
    public void addSysproperties(SysProperties ps)
    {
      variables.addAll(variables);
      propertySets.addAll(propertySets);
    }
    
    private Properties mergePropertySets()
    {
      Properties p = new Properties();
      for (PropertySet ps : propertySets) {
        p.putAll(ps.getProperties());
      }
      return p;
    }
  }
  
  public CommandlineJava()
  {
    setVm(JavaEnvUtils.getJreExecutable("java"));
    setVmversion(JavaEnvUtils.getJavaVersion());
  }
  
  public Commandline.Argument createArgument()
  {
    return javaCommand.createArgument();
  }
  
  public Commandline.Argument createVmArgument()
  {
    return vmCommand.createArgument();
  }
  
  public void addSysproperty(Environment.Variable sysp)
  {
    sysProperties.addVariable(sysp);
  }
  
  public void addSyspropertyset(PropertySet sysp)
  {
    sysProperties.addSyspropertyset(sysp);
  }
  
  public void addSysproperties(SysProperties sysp)
  {
    sysProperties.addSysproperties(sysp);
  }
  
  public void setVm(String vm)
  {
    vmCommand.setExecutable(vm);
  }
  
  public void setVmversion(String value)
  {
    vmVersion = value;
  }
  
  public void setCloneVm(boolean cloneVm)
  {
    this.cloneVm = cloneVm;
  }
  
  public Assertions getAssertions()
  {
    return assertions;
  }
  
  public void setAssertions(Assertions assertions)
  {
    this.assertions = assertions;
  }
  
  public void setJar(String jarpathname)
  {
    javaCommand.setExecutable(jarpathname);
    executableType = ExecutableType.JAR;
  }
  
  public String getJar()
  {
    if (executableType == ExecutableType.JAR) {
      return javaCommand.getExecutable();
    }
    return null;
  }
  
  public void setClassname(String classname)
  {
    if (executableType == ExecutableType.MODULE)
    {
      javaCommand.setExecutable(createModuleClassPair(
        parseModuleFromModuleClassPair(javaCommand.getExecutable()), classname), false);
    }
    else
    {
      javaCommand.setExecutable(classname);
      executableType = ExecutableType.CLASS;
    }
  }
  
  public String getClassname()
  {
    if (executableType != null) {
      switch (executableType)
      {
      case CLASS: 
        return javaCommand.getExecutable();
      case MODULE: 
        return parseClassFromModuleClassPair(javaCommand.getExecutable());
      }
    }
    return null;
  }
  
  public void setSourceFile(String sourceFile)
  {
    executableType = ExecutableType.SOURCE_FILE;
    javaCommand.setExecutable(sourceFile);
  }
  
  public String getSourceFile()
  {
    return executableType == ExecutableType.SOURCE_FILE ? javaCommand.getExecutable() : null;
  }
  
  public void setModule(String module)
  {
    if (executableType == null) {
      javaCommand.setExecutable(module);
    } else {
      switch (executableType)
      {
      case JAR: 
        javaCommand.setExecutable(module, false);
        break;
      case CLASS: 
        javaCommand.setExecutable(createModuleClassPair(module, javaCommand
          .getExecutable()), false);
        break;
      case MODULE: 
        javaCommand.setExecutable(createModuleClassPair(module, 
          parseClassFromModuleClassPair(javaCommand.getExecutable())), false);
        break;
      }
    }
    executableType = ExecutableType.MODULE;
  }
  
  public String getModule()
  {
    if (executableType == ExecutableType.MODULE) {
      return parseModuleFromModuleClassPair(javaCommand.getExecutable());
    }
    return null;
  }
  
  public Path createClasspath(Project p)
  {
    if (classpath == null) {
      classpath = new Path(p);
    }
    return classpath;
  }
  
  public Path createBootclasspath(Project p)
  {
    if (bootclasspath == null) {
      bootclasspath = new Path(p);
    }
    return bootclasspath;
  }
  
  public Path createModulepath(Project p)
  {
    if (modulepath == null) {
      modulepath = new Path(p);
    }
    return modulepath;
  }
  
  public Path createUpgrademodulepath(Project p)
  {
    if (upgrademodulepath == null) {
      upgrademodulepath = new Path(p);
    }
    return upgrademodulepath;
  }
  
  public String getVmversion()
  {
    return vmVersion;
  }
  
  public String[] getCommandline()
  {
    List<String> commands = new LinkedList();
    
    addCommandsToList(commands.listIterator());
    
    return (String[])commands.toArray(new String[commands.size()]);
  }
  
  private void addCommandsToList(ListIterator<String> listIterator)
  {
    getActualVMCommand().addCommandToList(listIterator);
    
    sysProperties.addDefinitionsToList(listIterator);
    if (isCloneVm())
    {
      SysProperties clonedSysProperties = new SysProperties();
      PropertySet ps = new PropertySet();
      PropertySet.BuiltinPropertySetName sys = new PropertySet.BuiltinPropertySetName();
      sys.setValue("system");
      ps.appendBuiltin(sys);
      clonedSysProperties.addSyspropertyset(ps);
      clonedSysProperties.addDefinitionsToList(listIterator);
    }
    Path bcp = calculateBootclasspath(true);
    if (bcp.size() > 0) {
      listIterator.add("-Xbootclasspath:" + bcp.toString());
    }
    if (haveClasspath())
    {
      listIterator.add("-classpath");
      listIterator.add(classpath.concatSystemClasspath("ignore").toString());
    }
    if (haveModulepath())
    {
      listIterator.add("--module-path");
      listIterator.add(modulepath.concatSystemClasspath("ignore").toString());
    }
    if (haveUpgrademodulepath())
    {
      listIterator.add("--upgrade-module-path");
      listIterator.add(upgrademodulepath.concatSystemClasspath("ignore").toString());
    }
    if (getAssertions() != null) {
      getAssertions().applyAssertions(listIterator);
    }
    if (executableType == ExecutableType.JAR) {
      listIterator.add("-jar");
    } else if (executableType == ExecutableType.MODULE) {
      listIterator.add("-m");
    }
    javaCommand.addCommandToList(listIterator);
  }
  
  public void setMaxmemory(String max)
  {
    maxMemory = max;
  }
  
  public String toString()
  {
    return Commandline.toString(getCommandline());
  }
  
  public String describeCommand()
  {
    return Commandline.describeCommand(getCommandline());
  }
  
  public String describeJavaCommand()
  {
    return Commandline.describeCommand(getJavaCommand());
  }
  
  protected Commandline getActualVMCommand()
  {
    Commandline actualVMCommand = (Commandline)vmCommand.clone();
    if (maxMemory != null) {
      if (vmVersion.startsWith("1.1")) {
        actualVMCommand.createArgument().setValue("-mx" + maxMemory);
      } else {
        actualVMCommand.createArgument().setValue("-Xmx" + maxMemory);
      }
    }
    return actualVMCommand;
  }
  
  @Deprecated
  public int size()
  {
    int size = getActualVMCommand().size() + javaCommand.size() + sysProperties.size();
    if (isCloneVm()) {
      size += System.getProperties().size();
    }
    if (haveClasspath()) {
      size += 2;
    }
    if (calculateBootclasspath(true).size() > 0) {
      size++;
    }
    if ((executableType == ExecutableType.JAR) || (executableType == ExecutableType.MODULE)) {
      size++;
    }
    if (getAssertions() != null) {
      size += getAssertions().size();
    }
    return size;
  }
  
  public Commandline getJavaCommand()
  {
    return javaCommand;
  }
  
  public Commandline getVmCommand()
  {
    return getActualVMCommand();
  }
  
  public Path getClasspath()
  {
    return classpath;
  }
  
  public Path getBootclasspath()
  {
    return bootclasspath;
  }
  
  public Path getModulepath()
  {
    return modulepath;
  }
  
  public Path getUpgrademodulepath()
  {
    return upgrademodulepath;
  }
  
  public void setSystemProperties()
    throws BuildException
  {
    sysProperties.setSystem();
  }
  
  public void restoreSystemProperties()
    throws BuildException
  {
    sysProperties.restoreSystem();
  }
  
  public SysProperties getSystemProperties()
  {
    return sysProperties;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    try
    {
      CommandlineJava c = (CommandlineJava)super.clone();
      vmCommand = ((Commandline)vmCommand.clone());
      javaCommand = ((Commandline)javaCommand.clone());
      sysProperties = ((SysProperties)sysProperties.clone());
      if (classpath != null) {
        classpath = ((Path)classpath.clone());
      }
      if (bootclasspath != null) {
        bootclasspath = ((Path)bootclasspath.clone());
      }
      if (modulepath != null) {
        modulepath = ((Path)modulepath.clone());
      }
      if (upgrademodulepath != null) {
        upgrademodulepath = ((Path)upgrademodulepath.clone());
      }
      if (assertions != null) {
        assertions = ((Assertions)assertions.clone());
      }
      return c;
    }
    catch (CloneNotSupportedException e)
    {
      throw new BuildException(e);
    }
  }
  
  public void clearJavaArgs()
  {
    javaCommand.clearArgs();
  }
  
  public boolean haveClasspath()
  {
    Path fullClasspath = classpath == null ? null : classpath.concatSystemClasspath("ignore");
    return (fullClasspath != null) && (!fullClasspath.toString().trim().isEmpty());
  }
  
  protected boolean haveBootclasspath(boolean log)
  {
    return calculateBootclasspath(log).size() > 0;
  }
  
  public boolean haveModulepath()
  {
    Path fullClasspath = modulepath != null ? modulepath.concatSystemClasspath("ignore") : null;
    return (fullClasspath != null) && 
      (!fullClasspath.toString().trim().isEmpty());
  }
  
  public boolean haveUpgrademodulepath()
  {
    Path fullClasspath = upgrademodulepath != null ? upgrademodulepath.concatSystemClasspath("ignore") : null;
    return (fullClasspath != null) && (!fullClasspath.toString().trim().isEmpty());
  }
  
  private Path calculateBootclasspath(boolean log)
  {
    if (vmVersion.startsWith("1.1"))
    {
      if ((bootclasspath != null) && (log)) {
        bootclasspath.log("Ignoring bootclasspath as the target VM doesn't support it.");
      }
    }
    else
    {
      Path b = bootclasspath;
      if (b == null) {
        b = new Path(null);
      }
      return b.concatSystemBootClasspath(isCloneVm() ? "last" : "ignore");
    }
    return new Path(null);
  }
  
  private boolean isCloneVm()
  {
    return (cloneVm) || (Boolean.parseBoolean(System.getProperty("ant.build.clonevm")));
  }
  
  private static String createModuleClassPair(String module, String classname)
  {
    return classname == null ? module : String.format("%s/%s", new Object[] { module, classname });
  }
  
  private static String parseModuleFromModuleClassPair(String moduleClassPair)
  {
    if (moduleClassPair == null) {
      return null;
    }
    String[] moduleAndClass = moduleClassPair.split("/");
    return moduleAndClass[0];
  }
  
  private static String parseClassFromModuleClassPair(String moduleClassPair)
  {
    if (moduleClassPair == null) {
      return null;
    }
    String[] moduleAndClass = moduleClassPair.split("/");
    return moduleAndClass.length == 2 ? moduleAndClass[1] : null;
  }
  
  private static enum ExecutableType
  {
    CLASS,  JAR,  MODULE,  SOURCE_FILE;
    
    private ExecutableType() {}
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.CommandlineJava
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */