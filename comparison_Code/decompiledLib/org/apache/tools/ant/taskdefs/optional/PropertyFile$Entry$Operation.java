package org.apache.tools.ant.taskdefs.optional;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class PropertyFile$Entry$Operation
  extends EnumeratedAttribute
{
  public static final int INCREMENT_OPER = 0;
  public static final int DECREMENT_OPER = 1;
  public static final int EQUALS_OPER = 2;
  public static final int DELETE_OPER = 3;
  
  public String[] getValues()
  {
    return new String[] { "+", "-", "=", "del" };
  }
  
  public static int toOperation(String oper)
  {
    if ("+".equals(oper)) {
      return 0;
    }
    if ("-".equals(oper)) {
      return 1;
    }
    if ("del".equals(oper)) {
      return 3;
    }
    return 2;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.PropertyFile.Entry.Operation
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */