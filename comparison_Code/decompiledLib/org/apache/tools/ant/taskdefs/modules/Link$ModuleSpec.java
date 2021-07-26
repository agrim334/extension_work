package org.apache.tools.ant.taskdefs.modules;

import org.apache.tools.ant.BuildException;

public class Link$ModuleSpec
{
  private String name;
  
  public Link$ModuleSpec(Link this$0) {}
  
  public Link$ModuleSpec(Link this$0, String name)
  {
    setName(name);
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void validate()
  {
    if (name == null) {
      throw new BuildException("name is required for module.", this$0.getLocation());
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.modules.Link.ModuleSpec
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */