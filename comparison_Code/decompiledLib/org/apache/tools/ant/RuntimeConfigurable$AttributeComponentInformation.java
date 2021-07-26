package org.apache.tools.ant;

class RuntimeConfigurable$AttributeComponentInformation
{
  String componentName;
  boolean restricted;
  
  private RuntimeConfigurable$AttributeComponentInformation(String componentName, boolean restricted)
  {
    this.componentName = componentName;
    this.restricted = restricted;
  }
  
  public String getComponentName()
  {
    return componentName;
  }
  
  public boolean isRestricted()
  {
    return restricted;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.RuntimeConfigurable.AttributeComponentInformation
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */