package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TypeAdapter;

public class AugmentReference
  extends Task
  implements TypeAdapter
{
  private String id;
  
  public void checkProxyClass(Class<?> proxyClass) {}
  
  public synchronized Object getProxy()
  {
    if (getProject() == null) {
      throw new IllegalStateException(getTaskName() + "Project owner unset");
    }
    hijackId();
    if (getProject().hasReference(id))
    {
      Object result = getProject().getReference(id);
      log("project reference " + id + "=" + String.valueOf(result), 4);
      return result;
    }
    throw new BuildException("Unknown reference \"" + id + "\"");
  }
  
  public void setProxy(Object o)
  {
    throw new UnsupportedOperationException();
  }
  
  private synchronized void hijackId()
  {
    if (id == null)
    {
      RuntimeConfigurable wrapper = getWrapper();
      id = wrapper.getId();
      if (id == null) {
        throw new BuildException(getTaskName() + " attribute 'id' unset");
      }
      wrapper.setAttribute("id", null);
      wrapper.removeAttribute("id");
      wrapper.setElementTag("augmented reference \"" + id + "\"");
    }
  }
  
  public void execute()
  {
    restoreWrapperId();
  }
  
  private synchronized void restoreWrapperId()
  {
    if (id != null)
    {
      log("restoring augment wrapper " + id, 4);
      RuntimeConfigurable wrapper = getWrapper();
      wrapper.setAttribute("id", id);
      wrapper.setElementTag(getTaskName());
      id = null;
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.AugmentReference
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */