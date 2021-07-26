package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.property.LocalProperties;

public class Local
  extends Task
{
  private String name;
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void execute()
  {
    if (name == null) {
      throw new BuildException("Missing attribute name");
    }
    LocalProperties.get(getProject()).addLocal(name);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Local
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */