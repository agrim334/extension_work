package org.apache.tools.ant.types.spi;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;

public class Provider
  extends ProjectComponent
{
  private String type;
  
  public String getClassName()
  {
    return type;
  }
  
  public void setClassName(String type)
  {
    this.type = type;
  }
  
  public void check()
  {
    if (type == null) {
      throw new BuildException("classname attribute must be set for provider element", getLocation());
    }
    if (type.isEmpty()) {
      throw new BuildException("Invalid empty classname", getLocation());
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.spi.Provider
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */