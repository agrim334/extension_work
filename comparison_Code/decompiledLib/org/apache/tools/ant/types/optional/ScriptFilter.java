package org.apache.tools.ant.types.optional;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.filters.TokenFilter.ChainableReaderFilter;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.ScriptRunnerBase;
import org.apache.tools.ant.util.ScriptRunnerHelper;

public class ScriptFilter
  extends TokenFilter.ChainableReaderFilter
{
  private ScriptRunnerHelper helper = new ScriptRunnerHelper();
  private ScriptRunnerBase runner = null;
  private String token;
  
  public void setProject(Project project)
  {
    super.setProject(project);
    helper.setProjectComponent(this);
  }
  
  public void setLanguage(String language)
  {
    helper.setLanguage(language);
  }
  
  private void init()
    throws BuildException
  {
    if (runner != null) {
      return;
    }
    runner = helper.getScriptRunner();
  }
  
  public void setToken(String token)
  {
    this.token = token;
  }
  
  public String getToken()
  {
    return token;
  }
  
  public String filter(String token)
  {
    init();
    setToken(token);
    runner.executeScript("ant_filter");
    return getToken();
  }
  
  public void setSrc(File file)
  {
    helper.setSrc(file);
  }
  
  public void addText(String text)
  {
    helper.addText(text);
  }
  
  public void setManager(String manager)
  {
    helper.setManager(manager);
  }
  
  public void setClasspath(Path classpath)
  {
    helper.setClasspath(classpath);
  }
  
  public Path createClasspath()
  {
    return helper.createClasspath();
  }
  
  public void setClasspathRef(Reference r)
  {
    helper.setClasspathRef(r);
  }
  
  public void setSetBeans(boolean setBeans)
  {
    helper.setSetBeans(setBeans);
  }
  
  public void setEncoding(String encoding)
  {
    helper.setEncoding(encoding);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.optional.ScriptFilter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */