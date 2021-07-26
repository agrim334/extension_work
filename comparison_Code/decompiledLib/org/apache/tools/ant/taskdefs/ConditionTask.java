package org.apache.tools.ant.taskdefs;

import java.util.Enumeration;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;

public class ConditionTask
  extends ConditionBase
{
  private String property = null;
  private Object value = "true";
  private Object alternative = null;
  
  public ConditionTask()
  {
    super("condition");
  }
  
  public void setProperty(String p)
  {
    property = p;
  }
  
  public void setValue(Object value)
  {
    this.value = value;
  }
  
  public void setValue(String v)
  {
    setValue(v);
  }
  
  public void setElse(Object alt)
  {
    alternative = alt;
  }
  
  public void setElse(String e)
  {
    setElse(e);
  }
  
  public void execute()
    throws BuildException
  {
    if (countConditions() > 1) {
      throw new BuildException("You must not nest more than one condition into <%s>", new Object[] {getTaskName() });
    }
    if (countConditions() < 1) {
      throw new BuildException("You must nest a condition into <%s>", new Object[] {getTaskName() });
    }
    if (property == null) {
      throw new BuildException("The property attribute is required.");
    }
    Condition c = (Condition)getConditions().nextElement();
    if (c.eval())
    {
      log("Condition true; setting " + property + " to " + value, 4);
      PropertyHelper.getPropertyHelper(getProject()).setNewProperty(property, value);
    }
    else if (alternative != null)
    {
      log("Condition false; setting " + property + " to " + alternative, 4);
      PropertyHelper.getPropertyHelper(getProject()).setNewProperty(property, alternative);
    }
    else
    {
      log("Condition false; not setting " + property, 4);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.ConditionTask
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */