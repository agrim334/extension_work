package org.apache.tools.ant.helper;

import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;

public class ProjectHelper2$AntHandler
{
  public void onStartElement(String uri, String tag, String qname, Attributes attrs, AntXMLContext context)
    throws SAXParseException
  {}
  
  public AntHandler onStartChild(String uri, String tag, String qname, Attributes attrs, AntXMLContext context)
    throws SAXParseException
  {
    throw new SAXParseException("Unexpected element \"" + qname + " \"", context.getLocator());
  }
  
  public void onEndChild(String uri, String tag, String qname, AntXMLContext context)
    throws SAXParseException
  {}
  
  public void onEndElement(String uri, String tag, AntXMLContext context) {}
  
  public void characters(char[] buf, int start, int count, AntXMLContext context)
    throws SAXParseException
  {
    String s = new String(buf, start, count).trim();
    if (!s.isEmpty()) {
      throw new SAXParseException("Unexpected text \"" + s + "\"", context.getLocator());
    }
  }
  
  protected void checkNamespace(String uri) {}
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.helper.ProjectHelper2.AntHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */