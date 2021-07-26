package org.apache.tools.ant.taskdefs.email;

public class Header
{
  private String name;
  private String value;
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setValue(String value)
  {
    this.value = value;
  }
  
  public String getValue()
  {
    return value;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.email.Header
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */