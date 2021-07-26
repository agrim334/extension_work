package org.apache.tools.ant.helper;

import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;

public class ProjectHelper2$MainHandler
  extends ProjectHelper2.AntHandler
{
  public ProjectHelper2.AntHandler onStartChild(String uri, String name, String qname, Attributes attrs, AntXMLContext context)
    throws SAXParseException
  {
    if (("project".equals(name)) && (
      (uri.isEmpty()) || (uri.equals("antlib:org.apache.tools.ant")))) {
      return ProjectHelper2.access$200();
    }
    if (name.equals(qname)) {
      throw new SAXParseException("Unexpected element \"{" + uri + "}" + name + "\" {" + "antlib:org.apache.tools.ant" + "}" + name, context.getLocator());
    }
    throw new SAXParseException("Unexpected element \"" + qname + "\" " + name, context.getLocator());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.helper.ProjectHelper2.MainHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */