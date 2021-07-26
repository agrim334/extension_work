package org.apache.tools.ant.taskdefs.condition;

import java.util.Enumeration;
import org.apache.tools.ant.BuildException;

public class Not
  extends ConditionBase
  implements Condition
{
  public boolean eval()
    throws BuildException
  {
    if (countConditions() > 1) {
      throw new BuildException("You must not nest more than one condition into <not>");
    }
    if (countConditions() < 1) {
      throw new BuildException("You must nest a condition into <not>");
    }
    return !((Condition)getConditions().nextElement()).eval();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.condition.Not
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */