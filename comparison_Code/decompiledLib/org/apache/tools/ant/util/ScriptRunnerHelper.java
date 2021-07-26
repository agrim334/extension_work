package org.apache.tools.ant.util;

import java.io.File;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Union;

public class ScriptRunnerHelper
{
  private ClasspathUtils.Delegate cpDelegate = null;
  private File srcFile;
  private String encoding;
  private String manager = "auto";
  private String language;
  private String text;
  private boolean compiled = false;
  private boolean setBeans = true;
  private ProjectComponent projectComponent;
  private ClassLoader scriptLoader = null;
  private Union resources = new Union();
  
  public void setProjectComponent(ProjectComponent component)
  {
    projectComponent = component;
  }
  
  public ScriptRunnerBase getScriptRunner()
  {
    ScriptRunnerBase runner = getRunner();
    runner.setCompiled(compiled);
    if (encoding != null) {
      runner.setEncoding(encoding);
    }
    if (srcFile != null) {
      runner.setSrc(srcFile);
    }
    if (text != null) {
      runner.addText(text);
    }
    if (resources != null) {
      runner.loadResources(resources);
    }
    if (setBeans) {
      runner.bindToComponent(projectComponent);
    } else {
      runner.bindToComponentMinimum(projectComponent);
    }
    return runner;
  }
  
  public Path createClasspath()
  {
    return getClassPathDelegate().createClasspath();
  }
  
  public void setClasspath(Path classpath)
  {
    getClassPathDelegate().setClasspath(classpath);
  }
  
  public void setClasspathRef(Reference r)
  {
    getClassPathDelegate().setClasspathref(r);
  }
  
  public void setSrc(File file)
  {
    srcFile = file;
  }
  
  public File getSrc()
  {
    return srcFile;
  }
  
  public void setEncoding(String encoding)
  {
    this.encoding = encoding;
  }
  
  public String getEncoding()
  {
    return encoding;
  }
  
  public void addText(String text)
  {
    this.text = text;
  }
  
  public void setManager(String manager)
  {
    this.manager = manager;
  }
  
  public void setLanguage(String language)
  {
    this.language = language;
  }
  
  public String getLanguage()
  {
    return language;
  }
  
  public void setCompiled(boolean compiled)
  {
    this.compiled = compiled;
  }
  
  public boolean getCompiled()
  {
    return compiled;
  }
  
  public void setSetBeans(boolean setBeans)
  {
    this.setBeans = setBeans;
  }
  
  public void setClassLoader(ClassLoader loader)
  {
    scriptLoader = loader;
  }
  
  private synchronized ClassLoader generateClassLoader()
  {
    if (scriptLoader != null) {
      return scriptLoader;
    }
    if (cpDelegate == null)
    {
      scriptLoader = getClass().getClassLoader();
      return scriptLoader;
    }
    scriptLoader = cpDelegate.getClassLoader();
    return scriptLoader;
  }
  
  private ClasspathUtils.Delegate getClassPathDelegate()
  {
    if (cpDelegate == null)
    {
      if (projectComponent == null) {
        throw new IllegalStateException("Can't access classpath without a project component");
      }
      cpDelegate = ClasspathUtils.getDelegate(projectComponent);
    }
    return cpDelegate;
  }
  
  private ScriptRunnerBase getRunner()
  {
    return new ScriptRunnerCreator(projectComponent.getProject()).createRunner(manager, language, 
      generateClassLoader());
  }
  
  public void add(ResourceCollection resource)
  {
    resources.add(resource);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.ScriptRunnerHelper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */