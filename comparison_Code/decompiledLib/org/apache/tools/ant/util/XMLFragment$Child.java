package org.apache.tools.ant.util;

import org.apache.tools.ant.DynamicConfiguratorNS;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLFragment$Child
  implements DynamicConfiguratorNS
{
  private Element e;
  
  XMLFragment$Child(XMLFragment this$0, Element e)
  {
    this.e = e;
  }
  
  public void addText(String s)
  {
    XMLFragment.access$000(this$0, e, s);
  }
  
  public void setDynamicAttribute(String uri, String name, String qName, String value)
  {
    if (uri.isEmpty()) {
      e.setAttribute(name, value);
    } else {
      e.setAttributeNS(uri, qName, value);
    }
  }
  
  public Object createDynamicElement(String uri, String name, String qName)
  {
    Element e2 = null;
    if (uri.isEmpty()) {
      e2 = XMLFragment.access$100(this$0).createElement(name);
    } else {
      e2 = XMLFragment.access$100(this$0).createElementNS(uri, qName);
    }
    e.appendChild(e2);
    return new Child(this$0, e2);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.XMLFragment.Child
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */