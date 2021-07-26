package org.apache.tools.ant.types;

public class FlexInteger
{
  private Integer value;
  
  public FlexInteger(String value)
  {
    this.value = Integer.decode(value);
  }
  
  public int intValue()
  {
    return value.intValue();
  }
  
  public String toString()
  {
    return value.toString();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.FlexInteger
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */