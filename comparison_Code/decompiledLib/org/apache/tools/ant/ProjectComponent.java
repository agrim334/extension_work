package org.apache.tools.ant;

import java.io.PrintStream;

public abstract class ProjectComponent
  implements Cloneable
{
  @Deprecated
  protected Project project;
  @Deprecated
  protected Location location = Location.UNKNOWN_LOCATION;
  @Deprecated
  protected String description;
  
  public void setProject(Project project)
  {
    this.project = project;
  }
  
  public Project getProject()
  {
    return project;
  }
  
  public Location getLocation()
  {
    return location;
  }
  
  public void setLocation(Location location)
  {
    this.location = location;
  }
  
  public void setDescription(String desc)
  {
    description = desc;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public void log(String msg)
  {
    log(msg, 2);
  }
  
  public void log(String msg, int msgLevel)
  {
    if (getProject() != null) {
      getProject().log(msg, msgLevel);
    } else if (msgLevel <= 2) {
      System.err.println(msg);
    }
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    ProjectComponent pc = (ProjectComponent)super.clone();
    pc.setLocation(getLocation());
    pc.setProject(getProject());
    return pc;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.ProjectComponent
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */