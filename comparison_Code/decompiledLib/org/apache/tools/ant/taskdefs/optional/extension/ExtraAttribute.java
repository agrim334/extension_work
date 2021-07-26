package org.apache.tools.ant.taskdefs.optional.extension;

import org.apache.tools.ant.BuildException;

public class ExtraAttribute
{
  private String name;
  private String value;
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void setValue(String value)
  {
    this.value = value;
  }
  
  String getName()
  {
    return name;
  }
  
  String getValue()
  {
    return value;
  }
  
  public void validate()
    throws BuildException
  {
    if (null == name) {
      throw new BuildException("Missing name from parameter.");
    }
    if (null == value) {
      throw new BuildException("Missing value from parameter " + name + ".");
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.extension.ExtraAttribute
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */