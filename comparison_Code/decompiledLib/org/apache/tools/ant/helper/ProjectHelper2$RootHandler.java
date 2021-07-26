package org.apache.tools.ant.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Stack;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.FileUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class ProjectHelper2$RootHandler
  extends DefaultHandler
{
  private Stack<ProjectHelper2.AntHandler> antHandlers = new Stack();
  private ProjectHelper2.AntHandler currentHandler = null;
  private AntXMLContext context;
  
  public ProjectHelper2$RootHandler(AntXMLContext context, ProjectHelper2.AntHandler rootHandler)
  {
    currentHandler = rootHandler;
    antHandlers.push(currentHandler);
    this.context = context;
  }
  
  public ProjectHelper2.AntHandler getCurrentAntHandler()
  {
    return currentHandler;
  }
  
  public InputSource resolveEntity(String publicId, String systemId)
  {
    context.getProject().log("resolving systemId: " + systemId, 3);
    if (systemId.startsWith("file:"))
    {
      String path = ProjectHelper2.access$100().fromURI(systemId);
      
      File file = new File(path);
      if (!file.isAbsolute())
      {
        file = ProjectHelper2.access$100().resolveFile(context.getBuildFileParent(), path);
        context.getProject().log("Warning: '" + systemId + "' in " + context
          .getBuildFile() + " should be expressed simply as '" + path
          .replace('\\', '/') + "' for compliance with other XML tools", 1);
      }
      context.getProject().log("file=" + file, 4);
      try
      {
        InputSource inputSource = new InputSource(Files.newInputStream(file.toPath(), new OpenOption[0]));
        inputSource.setSystemId(ProjectHelper2.access$100().toURI(file.getAbsolutePath()));
        return inputSource;
      }
      catch (IOException fne)
      {
        context.getProject().log(file.getAbsolutePath() + " could not be found", 1);
      }
    }
    context.getProject().log("could not resolve systemId", 4);
    return null;
  }
  
  public void startElement(String uri, String tag, String qname, Attributes attrs)
    throws SAXParseException
  {
    ProjectHelper2.AntHandler next = currentHandler.onStartChild(uri, tag, qname, attrs, context);
    antHandlers.push(currentHandler);
    currentHandler = next;
    currentHandler.onStartElement(uri, tag, qname, attrs, context);
  }
  
  public void setDocumentLocator(Locator locator)
  {
    context.setLocator(locator);
  }
  
  public void endElement(String uri, String name, String qName)
    throws SAXException
  {
    currentHandler.onEndElement(uri, name, context);
    currentHandler = ((ProjectHelper2.AntHandler)antHandlers.pop());
    if (currentHandler != null) {
      currentHandler.onEndChild(uri, name, qName, context);
    }
  }
  
  public void characters(char[] buf, int start, int count)
    throws SAXParseException
  {
    currentHandler.characters(buf, start, count, context);
  }
  
  public void startPrefixMapping(String prefix, String uri)
  {
    context.startPrefixMapping(prefix, uri);
  }
  
  public void endPrefixMapping(String prefix)
  {
    context.endPrefixMapping(prefix);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.helper.ProjectHelper2.RootHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */