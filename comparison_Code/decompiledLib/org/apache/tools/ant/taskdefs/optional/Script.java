package org.apache.tools.ant.taskdefs.optional;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.ScriptRunnerBase;
import org.apache.tools.ant.util.ScriptRunnerHelper;

public class Script
  extends Task
{
  private ScriptRunnerHelper helper = new ScriptRunnerHelper();
  
  public void setProject(Project project)
  {
    super.setProject(project);
    helper.setProjectComponent(this);
  }
  
  public void execute()
    throws BuildException
  {
    helper.getScriptRunner().executeScript("ANT");
  }
  
  public void setManager(String manager)
  {
    helper.setManager(manager);
  }
  
  public void setLanguage(String language)
  {
    helper.setLanguage(language);
  }
  
  public void setSrc(String fileName)
  {
    helper.setSrc(new File(fileName));
  }
  
  public void addText(String text)
  {
    helper.addText(text);
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
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.Script
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */