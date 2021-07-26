package org.apache.tools.ant.helper;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXParseException;

class ProjectHelperImpl$TargetHandler
  extends ProjectHelperImpl.AbstractHandler
{
  private Target target;
  
  public ProjectHelperImpl$TargetHandler(ProjectHelperImpl helperImpl, DocumentHandler parentHandler)
  {
    super(helperImpl, parentHandler);
  }
  
  public void init(String tag, AttributeList attrs)
    throws SAXParseException
  {
    String name = null;
    String depends = "";
    String ifCond = null;
    String unlessCond = null;
    String id = null;
    String description = null;
    for (int i = 0; i < attrs.getLength(); i++)
    {
      String key = attrs.getName(i);
      String value = attrs.getValue(i);
      switch (key)
      {
      case "name": 
        name = value;
        if (name.isEmpty()) {
          throw new BuildException("name attribute must not be empty", new Location(ProjectHelperImpl.access$100(helperImpl)));
        }
        break;
      case "depends": 
        depends = value;
        break;
      case "if": 
        ifCond = value;
        break;
      case "unless": 
        unlessCond = value;
        break;
      case "id": 
        id = value;
        break;
      case "description": 
        description = value;
        break;
      default: 
        throw new SAXParseException("Unexpected attribute \"" + key + "\"", ProjectHelperImpl.access$100(helperImpl));
      }
    }
    if (name == null) {
      throw new SAXParseException("target element appears without a name attribute", ProjectHelperImpl.access$100(helperImpl));
    }
    target = new Target();
    
    target.addDependency("");
    
    target.setName(name);
    target.setIf(ifCond);
    target.setUnless(unlessCond);
    target.setDescription(description);
    ProjectHelperImpl.access$200(helperImpl).addTarget(name, target);
    if ((id != null) && (!id.isEmpty())) {
      ProjectHelperImpl.access$200(helperImpl).addReference(id, target);
    }
    if (!depends.isEmpty()) {
      target.setDepends(depends);
    }
  }
  
  public void startElement(String name, AttributeList attrs)
    throws SAXParseException
  {
    ProjectHelperImpl.access$700(helperImpl, this, target, name, attrs);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.helper.ProjectHelperImpl.TargetHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */