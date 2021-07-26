package org.apache.tools.ant.util;

import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class DOMUtils
{
  public static Document newDocument()
  {
    return JAXPUtils.getDocumentBuilder().newDocument();
  }
  
  public static Element createChildElement(Element parent, String name)
  {
    Document doc = parent.getOwnerDocument();
    Element e = doc.createElement(name);
    parent.appendChild(e);
    return e;
  }
  
  public static void appendText(Element parent, String content)
  {
    Document doc = parent.getOwnerDocument();
    Text t = doc.createTextNode(content);
    parent.appendChild(t);
  }
  
  public static void appendCDATA(Element parent, String content)
  {
    Document doc = parent.getOwnerDocument();
    CDATASection c = doc.createCDATASection(content);
    parent.appendChild(c);
  }
  
  public static void appendTextElement(Element parent, String name, String content)
  {
    Element e = createChildElement(parent, name);
    appendText(e, content);
  }
  
  public static void appendCDATAElement(Element parent, String name, String content)
  {
    Element e = createChildElement(parent, name);
    appendCDATA(e, content);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.DOMUtils
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */