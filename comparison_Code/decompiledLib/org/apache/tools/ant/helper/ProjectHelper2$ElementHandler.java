package org.apache.tools.ant.helper;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.UnknownElement;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

public class ProjectHelper2$ElementHandler
  extends ProjectHelper2.AntHandler
{
  public void onStartElement(String uri, String tag, String qname, Attributes attrs, AntXMLContext context)
    throws SAXParseException
  {
    RuntimeConfigurable parentWrapper = context.currentWrapper();
    Object parent = null;
    if (parentWrapper != null) {
      parent = parentWrapper.getProxy();
    }
    UnknownElement task = new UnknownElement(tag);
    task.setProject(context.getProject());
    task.setNamespace(uri);
    task.setQName(qname);
    task.setTaskType(ProjectHelper.genComponentName(task.getNamespace(), tag));
    task.setTaskName(qname);
    
    Location location = new Location(context.getLocator().getSystemId(), context.getLocator().getLineNumber(), context.getLocator().getColumnNumber());
    task.setLocation(location);
    task.setOwningTarget(context.getCurrentTarget());
    if (parent != null) {
      ((UnknownElement)parent).addChild(task);
    } else {
      context.getCurrentTarget().addTask(task);
    }
    context.configureId(task, attrs);
    
    RuntimeConfigurable wrapper = new RuntimeConfigurable(task, task.getTaskName());
    for (int i = 0; i < attrs.getLength(); i++)
    {
      String name = attrs.getLocalName(i);
      String attrUri = attrs.getURI(i);
      if ((attrUri != null) && (!attrUri.isEmpty()) && (!attrUri.equals(uri))) {
        name = attrUri + ":" + attrs.getQName(i);
      }
      String value = attrs.getValue(i);
      if (("ant-type".equals(name)) || (
        ("antlib:org.apache.tools.ant".equals(attrUri)) && 
        ("ant-type".equals(attrs.getLocalName(i)))))
      {
        name = "ant-type";
        int index = value.indexOf(":");
        if (index >= 0)
        {
          String prefix = value.substring(0, index);
          String mappedUri = context.getPrefixMapping(prefix);
          if (mappedUri == null) {
            throw new BuildException("Unable to find XML NS prefix \"" + prefix + "\"");
          }
          value = ProjectHelper.genComponentName(mappedUri, value
            .substring(index + 1));
        }
      }
      wrapper.setAttribute(name, value);
    }
    if (parentWrapper != null) {
      parentWrapper.addChild(wrapper);
    }
    context.pushWrapper(wrapper);
  }
  
  public void characters(char[] buf, int start, int count, AntXMLContext context)
    throws SAXParseException
  {
    RuntimeConfigurable wrapper = context.currentWrapper();
    wrapper.addText(buf, start, count);
  }
  
  public ProjectHelper2.AntHandler onStartChild(String uri, String tag, String qname, Attributes attrs, AntXMLContext context)
    throws SAXParseException
  {
    return ProjectHelper2.access$400();
  }
  
  public void onEndElement(String uri, String tag, AntXMLContext context)
  {
    context.popWrapper();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.helper.ProjectHelper2.ElementHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */