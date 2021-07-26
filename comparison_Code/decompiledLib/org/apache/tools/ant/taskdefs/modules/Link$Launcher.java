package org.apache.tools.ant.taskdefs.modules;

import java.util.Objects;
import org.apache.tools.ant.BuildException;

public class Link$Launcher
{
  private String name;
  private String module;
  private String mainClass;
  
  public Link$Launcher(Link this$0) {}
  
  public Link$Launcher(Link this$0, String textSpec)
  {
    Objects.requireNonNull(textSpec, "Text cannot be null");
    
    int equals = textSpec.lastIndexOf('=');
    if (equals < 1) {
      throw new BuildException("Launcher command must take the form name=module or name=module/mainclass");
    }
    setName(textSpec.substring(0, equals));
    
    int slash = textSpec.indexOf('/', equals);
    if (slash < 0)
    {
      setModule(textSpec.substring(equals + 1));
    }
    else if ((slash > equals + 1) && (slash < textSpec.length() - 1))
    {
      setModule(textSpec.substring(equals + 1, slash));
      setMainClass(textSpec.substring(slash + 1));
    }
    else
    {
      throw new BuildException("Launcher command must take the form name=module or name=module/mainclass");
    }
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getModule()
  {
    return module;
  }
  
  public void setModule(String module)
  {
    this.module = module;
  }
  
  public String getMainClass()
  {
    return mainClass;
  }
  
  public void setMainClass(String className)
  {
    mainClass = className;
  }
  
  public void validate()
  {
    if ((name == null) || (name.isEmpty())) {
      throw new BuildException("Launcher must have a name", this$0.getLocation());
    }
    if ((module == null) || (module.isEmpty())) {
      throw new BuildException("Launcher must have specify a module", this$0.getLocation());
    }
  }
  
  public String toString()
  {
    if (mainClass != null) {
      return name + "=" + module + "/" + mainClass;
    }
    return name + "=" + module;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.modules.Link.Launcher
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */