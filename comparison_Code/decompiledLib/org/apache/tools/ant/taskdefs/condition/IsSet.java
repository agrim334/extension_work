package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;

public class IsSet
  extends ProjectComponent
  implements Condition
{
  private String property;
  
  public void setProperty(String p)
  {
    property = p;
  }
  
  public boolean eval()
    throws BuildException
  {
    if (property == null) {
      throw new BuildException("No property specified for isset condition");
    }
    return getProject().getProperty(property) != null;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.condition.IsSet
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */