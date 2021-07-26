package org.apache.tools.ant.helper;

import java.util.Locale;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.TypeAdapter;
import org.apache.tools.ant.UnknownElement;
import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXParseException;

class ProjectHelperImpl$NestedElementHandler
  extends ProjectHelperImpl.AbstractHandler
{
  private Object parent;
  private Object child;
  private RuntimeConfigurable parentWrapper;
  private RuntimeConfigurable childWrapper = null;
  private Target target;
  
  public ProjectHelperImpl$NestedElementHandler(ProjectHelperImpl helperImpl, DocumentHandler parentHandler, Object parent, RuntimeConfigurable parentWrapper, Target target)
  {
    super(helperImpl, parentHandler);
    if ((parent instanceof TypeAdapter)) {
      this.parent = ((TypeAdapter)parent).getProxy();
    } else {
      this.parent = parent;
    }
    this.parentWrapper = parentWrapper;
    this.target = target;
  }
  
  public void init(String propType, AttributeList attrs)
    throws SAXParseException
  {
    Class<?> parentClass = parent.getClass();
    IntrospectionHelper ih = IntrospectionHelper.getHelper(ProjectHelperImpl.access$200(helperImpl), parentClass);
    try
    {
      String elementName = propType.toLowerCase(Locale.ENGLISH);
      if ((parent instanceof UnknownElement))
      {
        UnknownElement uc = new UnknownElement(elementName);
        uc.setProject(ProjectHelperImpl.access$200(helperImpl));
        ((UnknownElement)parent).addChild(uc);
        child = uc;
      }
      else
      {
        child = ih.createElement(ProjectHelperImpl.access$200(helperImpl), parent, elementName);
      }
      ProjectHelperImpl.access$800(helperImpl, child, attrs);
      
      childWrapper = new RuntimeConfigurable(child, propType);
      childWrapper.setAttributes(attrs);
      parentWrapper.addChild(childWrapper);
    }
    catch (BuildException exc)
    {
      throw new SAXParseException(exc.getMessage(), ProjectHelperImpl.access$100(helperImpl), exc);
    }
  }
  
  public void characters(char[] buf, int start, int count)
  {
    childWrapper.addText(buf, start, count);
  }
  
  public void startElement(String name, AttributeList attrs)
    throws SAXParseException
  {
    if ((child instanceof TaskContainer)) {
      new ProjectHelperImpl.TaskHandler(helperImpl, this, (TaskContainer)child, childWrapper, target).init(name, attrs);
    } else {
      new NestedElementHandler(helperImpl, this, child, childWrapper, target).init(name, attrs);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.helper.ProjectHelperImpl.NestedElementHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */