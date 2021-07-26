package org.apache.tools.ant;

public class UnsupportedElementException
  extends BuildException
{
  private static final long serialVersionUID = 1L;
  private final String element;
  
  public UnsupportedElementException(String msg, String element)
  {
    super(msg);
    this.element = element;
  }
  
  public String getElement()
  {
    return element;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.UnsupportedElementException
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */