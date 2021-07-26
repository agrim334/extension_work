package org.apache.tools.ant.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.FileUtils;
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

class ProjectHelperImpl$RootHandler
  extends HandlerBase
{
  ProjectHelperImpl helperImpl;
  
  public ProjectHelperImpl$RootHandler(ProjectHelperImpl helperImpl)
  {
    this.helperImpl = helperImpl;
  }
  
  public InputSource resolveEntity(String publicId, String systemId)
  {
    ProjectHelperImpl.access$200(helperImpl).log("resolving systemId: " + systemId, 3);
    if (systemId.startsWith("file:"))
    {
      String path = ProjectHelperImpl.access$300().fromURI(systemId);
      
      File file = new File(path);
      if (!file.isAbsolute())
      {
        file = ProjectHelperImpl.access$300().resolveFile(ProjectHelperImpl.access$400(helperImpl), path);
        ProjectHelperImpl.access$200(helperImpl).log("Warning: '" + systemId + "' in " + ProjectHelperImpl.access$500(helperImpl) + " should be expressed simply as '" + path
          .replace('\\', '/') + "' for compliance with other XML tools", 1);
      }
      try
      {
        InputSource inputSource = new InputSource(Files.newInputStream(file.toPath(), new OpenOption[0]));
        inputSource.setSystemId(ProjectHelperImpl.access$300().toURI(file.getAbsolutePath()));
        return inputSource;
      }
      catch (IOException fne)
      {
        ProjectHelperImpl.access$200(helperImpl).log(file.getAbsolutePath() + " could not be found", 1);
      }
    }
    return null;
  }
  
  public void startElement(String tag, AttributeList attrs)
    throws SAXParseException
  {
    if ("project".equals(tag)) {
      new ProjectHelperImpl.ProjectHandler(helperImpl, this).init(tag, attrs);
    } else {
      throw new SAXParseException("Config file is not of expected XML type", ProjectHelperImpl.access$100(helperImpl));
    }
  }
  
  public void setDocumentLocator(Locator locator)
  {
    ProjectHelperImpl.access$102(helperImpl, locator);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.helper.ProjectHelperImpl.RootHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */