package org.apache.tools.ant;

public class UnsupportedAttributeException
  extends BuildException
{
  private static final long serialVersionUID = 1L;
  private final String attribute;
  
  public UnsupportedAttributeException(String msg, String attribute)
  {
    super(msg);
    this.attribute = attribute;
  }
  
  public String getAttribute()
  {
    return attribute;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.UnsupportedAttributeException
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */