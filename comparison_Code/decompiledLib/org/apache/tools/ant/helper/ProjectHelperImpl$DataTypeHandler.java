package org.apache.tools.ant.helper;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXParseException;

class ProjectHelperImpl$DataTypeHandler
  extends ProjectHelperImpl.AbstractHandler
{
  private Target target;
  private Object element;
  private RuntimeConfigurable wrapper = null;
  
  public ProjectHelperImpl$DataTypeHandler(ProjectHelperImpl helperImpl, DocumentHandler parentHandler, Target target)
  {
    super(helperImpl, parentHandler);
    this.target = target;
  }
  
  public void init(String propType, AttributeList attrs)
    throws SAXParseException
  {
    try
    {
      element = ProjectHelperImpl.access$200(helperImpl).createDataType(propType);
      if (element == null) {
        throw new BuildException("Unknown data type " + propType);
      }
      wrapper = new RuntimeConfigurable(element, propType);
      wrapper.setAttributes(attrs);
      target.addDataType(wrapper);
    }
    catch (BuildException exc)
    {
      throw new SAXParseException(exc.getMessage(), ProjectHelperImpl.access$100(helperImpl), exc);
    }
  }
  
  public void characters(char[] buf, int start, int count)
  {
    wrapper.addText(buf, start, count);
  }
  
  public void startElement(String name, AttributeList attrs)
    throws SAXParseException
  {
    new ProjectHelperImpl.NestedElementHandler(helperImpl, this, element, wrapper, target).init(name, attrs);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.helper.ProjectHelperImpl.DataTypeHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */