package org.apache.tools.ant.taskdefs;

import java.util.Enumeration;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;

class Exit$NestedCondition
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

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Exit.NestedCondition
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */