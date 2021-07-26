package org.apache.tools.ant.taskdefs;

public class GenerateKey$DnameParam
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
  
  public boolean isComplete()
  {
    return (name != null) && (value != null);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.GenerateKey.DnameParam
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */