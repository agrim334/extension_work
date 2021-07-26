package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper.Delegate;

public final class PropertyHelperTask$DelegateElement
{
  private String refid;
  
  private PropertyHelperTask$DelegateElement(PropertyHelperTask this$0) {}
  
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
    return (PropertyHelper.Delegate)this$0.getProject().getReference(refid);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.PropertyHelperTask.DelegateElement
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */