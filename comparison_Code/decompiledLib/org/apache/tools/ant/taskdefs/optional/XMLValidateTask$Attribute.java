package org.apache.tools.ant.taskdefs.optional;

public class XMLValidateTask$Attribute
{
  private String attributeName = null;
  private boolean attributeValue;
  
  public void setName(String name)
  {
    attributeName = name;
  }
  
  public void setValue(boolean value)
  {
    attributeValue = value;
  }
  
  public String getName()
  {
    return attributeName;
  }
  
  public boolean getValue()
  {
    return attributeValue;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.XMLValidateTask.Attribute
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */