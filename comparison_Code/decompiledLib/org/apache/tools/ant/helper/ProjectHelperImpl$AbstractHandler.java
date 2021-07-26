package org.apache.tools.ant.helper;

import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.HandlerBase;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class ProjectHelperImpl$AbstractHandler
  extends HandlerBase
{
  protected DocumentHandler parentHandler;
  ProjectHelperImpl helperImpl;
  
  public ProjectHelperImpl$AbstractHandler(ProjectHelperImpl helperImpl, DocumentHandler parentHandler)
  {
    this.parentHandler = parentHandler;
    this.helperImpl = helperImpl;
    
    ProjectHelperImpl.access$000(helperImpl).setDocumentHandler(this);
  }
  
  public void startElement(String tag, AttributeList attrs)
    throws SAXParseException
  {
    throw new SAXParseException("Unexpected element \"" + tag + "\"", ProjectHelperImpl.access$100(helperImpl));
  }
  
  public void characters(char[] buf, int start, int count)
    throws SAXParseException
  {
    String s = new String(buf, start, count).trim();
    if (!s.isEmpty()) {
      throw new SAXParseException("Unexpected text \"" + s + "\"", ProjectHelperImpl.access$100(helperImpl));
    }
  }
  
  public void endElement(String name)
    throws SAXException
  {
    ProjectHelperImpl.access$000(helperImpl).setDocumentHandler(parentHandler);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.helper.ProjectHelperImpl.AbstractHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */