package org.apache.tools.ant.helper;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;
import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXParseException;

class ProjectHelperImpl$TaskHandler
  extends ProjectHelperImpl.AbstractHandler
{
  private Target target;
  private TaskContainer container;
  private Task task;
  private RuntimeConfigurable parentWrapper;
  private RuntimeConfigurable wrapper = null;
  
  public ProjectHelperImpl$TaskHandler(ProjectHelperImpl helperImpl, DocumentHandler parentHandler, TaskContainer container, RuntimeConfigurable parentWrapper, Target target)
  {
    super(helperImpl, parentHandler);
    this.container = container;
    this.parentWrapper = parentWrapper;
    this.target = target;
  }
  
  public void init(String tag, AttributeList attrs)
    throws SAXParseException
  {
    try
    {
      task = ProjectHelperImpl.access$200(helperImpl).createTask(tag);
    }
    catch (BuildException localBuildException) {}
    if (task == null)
    {
      task = new UnknownElement(tag);
      task.setProject(ProjectHelperImpl.access$200(helperImpl));
      
      task.setTaskName(tag);
    }
    task.setLocation(new Location(ProjectHelperImpl.access$100(helperImpl)));
    ProjectHelperImpl.access$800(helperImpl, task, attrs);
    
    task.setOwningTarget(target);
    container.addTask(task);
    task.init();
    wrapper = task.getRuntimeConfigurableWrapper();
    wrapper.setAttributes(attrs);
    if (parentWrapper != null) {
      parentWrapper.addChild(wrapper);
    }
  }
  
  public void characters(char[] buf, int start, int count)
  {
    wrapper.addText(buf, start, count);
  }
  
  public void startElement(String name, AttributeList attrs)
    throws SAXParseException
  {
    if ((task instanceof TaskContainer)) {
      new TaskHandler(helperImpl, this, (TaskContainer)task, wrapper, target).init(name, attrs);
    } else {
      new ProjectHelperImpl.NestedElementHandler(helperImpl, this, task, wrapper, target).init(name, attrs);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.helper.ProjectHelperImpl.TaskHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */