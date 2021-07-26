package org.apache.tools.ant.types;

public final class Parameter
{
  private String name = null;
  private String type = null;
  private String value = null;
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void setType(String type)
  {
    this.type = type;
  }
  
  public void setValue(String value)
  {
    this.value = value;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getType()
  {
    return type;
  }
  
  public String getValue()
  {
    return value;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.Parameter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */