package org.apache.tools.ant;

import org.w3c.dom.Element;

class XmlLogger$TimedElement
{
  private long startTime;
  private Element element;
  
  public String toString()
  {
    return element.getTagName() + ":" + element.getAttribute("name");
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.XmlLogger.TimedElement
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */