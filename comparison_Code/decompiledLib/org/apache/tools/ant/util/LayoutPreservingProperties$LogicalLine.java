package org.apache.tools.ant.util;

import java.io.Serializable;

abstract class LayoutPreservingProperties$LogicalLine
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private String text;
  
  public LayoutPreservingProperties$LogicalLine(String text)
  {
    this.text = text;
  }
  
  public void setText(String text)
  {
    this.text = text;
  }
  
  public String toString()
  {
    return text;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.LayoutPreservingProperties.LogicalLine
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */