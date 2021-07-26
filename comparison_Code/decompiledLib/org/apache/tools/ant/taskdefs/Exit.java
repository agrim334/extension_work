package org.apache.tools.ant.taskdefs;

import java.util.Enumeration;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ExitStatusException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;

public class Exit
  extends Task
{
  private String message;
  private Object ifCondition;
  private Object unlessCondition;
  private NestedCondition nestedCondition;
  private Integer status;
  
  private static class NestedCondition
    extends ConditionBase
    implements Condition
  {
    public boolean eval()
    {
      if (countConditions() != 1) {
        throw new BuildException("A single nested condition is required.");
      }
      return ((Condition)getConditions().nextElement()).eval();
    }
  }
  
  public void setMessage(String value)
  {
    message = value;
  }
  
  public void setIf(Object c)
  {
    ifCondition = c;
  }
  
  public void setIf(String c)
  {
    setIf(c);
  }
  
  public void setUnless(Object c)
  {
    unlessCondition = c;
  }
  
  public void setUnless(String c)
  {
    setUnless(c);
  }
  
  public void setStatus(int i)
  {
    status = Integer.valueOf(i);
  }
  
  public void execute()
    throws BuildException
  {
    boolean fail = (testIfCondition()) && (testUnlessCondition()) ? true : nestedConditionPresent() ? testNestedCondition() : false;
    if (fail)
    {
      String text = null;
      if ((message != null) && (!message.trim().isEmpty()))
      {
        text = message.trim();
      }
      else
      {
        if ((!isNullOrEmpty(ifCondition)) && (testIfCondition())) {
          text = "if=" + ifCondition;
        }
        if ((!isNullOrEmpty(unlessCondition)) && (testUnlessCondition()))
        {
          if (text == null) {
            text = "";
          } else {
            text = text + " and ";
          }
          text = text + "unless=" + unlessCondition;
        }
        if (nestedConditionPresent()) {
          text = "condition satisfied";
        } else if (text == null) {
          text = "No message";
        }
      }
      log("failing due to " + text, 4);
      
      throw (status == null ? new BuildException(text) : new ExitStatusException(text, status.intValue()));
    }
  }
  
  private boolean isNullOrEmpty(Object value)
  {
    return (value == null) || ("".equals(value));
  }
  
  public void addText(String msg)
  {
    if (message == null) {
      message = "";
    }
    message += getProject().replaceProperties(msg);
  }
  
  public ConditionBase createCondition()
  {
    if (nestedCondition != null) {
      throw new BuildException("Only one nested condition is allowed.");
    }
    nestedCondition = new NestedCondition(null);
    return nestedCondition;
  }
  
  private boolean testIfCondition()
  {
    return 
      PropertyHelper.getPropertyHelper(getProject()).testIfCondition(ifCondition);
  }
  
  private boolean testUnlessCondition()
  {
    return 
      PropertyHelper.getPropertyHelper(getProject()).testUnlessCondition(unlessCondition);
  }
  
  private boolean testNestedCondition()
  {
    boolean result = nestedConditionPresent();
    if (((result) && (ifCondition != null)) || (unlessCondition != null)) {
      throw new BuildException("Nested conditions not permitted in conjunction with if/unless attributes");
    }
    return (result) && (nestedCondition.eval());
  }
  
  private boolean nestedConditionPresent()
  {
    return nestedCondition != null;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Exit
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */