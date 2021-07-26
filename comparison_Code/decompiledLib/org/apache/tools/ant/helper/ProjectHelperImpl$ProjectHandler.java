package org.apache.tools.ant.helper;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.FileUtils;
import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXParseException;

class ProjectHelperImpl$ProjectHandler
  extends ProjectHelperImpl.AbstractHandler
{
  public ProjectHelperImpl$ProjectHandler(ProjectHelperImpl helperImpl, DocumentHandler parentHandler)
  {
    super(helperImpl, parentHandler);
  }
  
  public void init(String tag, AttributeList attrs)
    throws SAXParseException
  {
    String def = null;
    String name = null;
    String id = null;
    String baseDir = null;
    for (int i = 0; i < attrs.getLength(); i++)
    {
      String key = attrs.getName(i);
      String value = attrs.getValue(i);
      switch (key)
      {
      case "default": 
        def = value;
        break;
      case "name": 
        name = value;
        break;
      case "id": 
        id = value;
        break;
      case "basedir": 
        baseDir = value;
        break;
      default: 
        throw new SAXParseException("Unexpected attribute \"" + key + "\"", ProjectHelperImpl.access$100(helperImpl));
      }
    }
    if ((def != null) && (!def.isEmpty())) {
      ProjectHelperImpl.access$200(helperImpl).setDefault(def);
    } else {
      throw new BuildException("The default attribute is required");
    }
    if (name != null)
    {
      ProjectHelperImpl.access$200(helperImpl).setName(name);
      ProjectHelperImpl.access$200(helperImpl).addReference(name, ProjectHelperImpl.access$200(helperImpl));
    }
    if (id != null) {
      ProjectHelperImpl.access$200(helperImpl).addReference(id, ProjectHelperImpl.access$200(helperImpl));
    }
    if (ProjectHelperImpl.access$200(helperImpl).getProperty("basedir") != null)
    {
      ProjectHelperImpl.access$200(helperImpl).setBasedir(ProjectHelperImpl.access$200(helperImpl).getProperty("basedir"));
    }
    else if (baseDir == null)
    {
      ProjectHelperImpl.access$200(helperImpl).setBasedir(ProjectHelperImpl.access$400(helperImpl).getAbsolutePath());
    }
    else if (new File(baseDir).isAbsolute())
    {
      ProjectHelperImpl.access$200(helperImpl).setBasedir(baseDir);
    }
    else
    {
      File resolvedBaseDir = ProjectHelperImpl.access$300().resolveFile(ProjectHelperImpl.access$400(helperImpl), baseDir);
      
      ProjectHelperImpl.access$200(helperImpl).setBaseDir(resolvedBaseDir);
    }
    ProjectHelperImpl.access$200(helperImpl).addTarget("", ProjectHelperImpl.access$600(helperImpl));
  }
  
  public void startElement(String name, AttributeList attrs)
    throws SAXParseException
  {
    if ("target".equals(name)) {
      handleTarget(name, attrs);
    } else {
      ProjectHelperImpl.access$700(helperImpl, this, ProjectHelperImpl.access$600(helperImpl), name, attrs);
    }
  }
  
  private void handleTarget(String tag, AttributeList attrs)
    throws SAXParseException
  {
    new ProjectHelperImpl.TargetHandler(helperImpl, this).init(tag, attrs);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.helper.ProjectHelperImpl.ProjectHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */