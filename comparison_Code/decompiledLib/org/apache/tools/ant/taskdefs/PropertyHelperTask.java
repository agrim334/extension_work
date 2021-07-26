package org.apache.tools.ant.taskdefs;

import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.PropertyHelper.Delegate;
import org.apache.tools.ant.Task;

public class PropertyHelperTask
  extends Task
{
  private PropertyHelper propertyHelper;
  private List<Object> delegates;
  
  public final class DelegateElement
  {
    private String refid;
    
    private DelegateElement() {}
    
    public String getRefid()
    {
      return refid;
    }
    
    public void setRefid(String refid)
    {
      this.refid = refid;
    }
    
    private PropertyHelper.Delegate resolve()
    {
      if (refid == null) {
        throw new BuildException("refid required for generic delegate");
      }
      return (PropertyHelper.Delegate)getProject().getReference(refid);
    }
  }
  
  public synchronized void addConfigured(PropertyHelper propertyHelper)
  {
    if (this.propertyHelper != null) {
      throw new BuildException("Only one PropertyHelper can be installed");
    }
    this.propertyHelper = propertyHelper;
  }
  
  public synchronized void addConfigured(PropertyHelper.Delegate delegate)
  {
    getAddDelegateList().add(delegate);
  }
  
  public DelegateElement createDelegate()
  {
    DelegateElement result = new DelegateElement(null);
    getAddDelegateList().add(result);
    return result;
  }
  
  public void execute()
    throws BuildException
  {
    if (getProject() == null) {
      throw new BuildException("Project instance not set");
    }
    if ((propertyHelper == null) && (delegates == null)) {
      throw new BuildException("Either a new PropertyHelper or one or more PropertyHelper delegates are required");
    }
    PropertyHelper ph = propertyHelper;
    if (ph == null) {
      ph = PropertyHelper.getPropertyHelper(getProject());
    } else {
      ph = propertyHelper;
    }
    synchronized (ph)
    {
      if (delegates != null) {
        for (Object o : delegates)
        {
          PropertyHelper.Delegate delegate = (o instanceof DelegateElement) ? ((DelegateElement)o).resolve() : (PropertyHelper.Delegate)o;
          log("Adding PropertyHelper delegate " + delegate, 4);
          ph.add(delegate);
        }
      }
    }
    if (propertyHelper != null)
    {
      log("Installing PropertyHelper " + propertyHelper, 4);
      
      getProject().addReference("ant.PropertyHelper", propertyHelper);
    }
  }
  
  private synchronized List<Object> getAddDelegateList()
  {
    if (delegates == null) {
      delegates = new ArrayList();
    }
    return delegates;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.PropertyHelperTask
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */