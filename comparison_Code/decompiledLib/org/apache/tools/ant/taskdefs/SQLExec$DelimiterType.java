package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class SQLExec$DelimiterType
  extends EnumeratedAttribute
{
  public static final String NORMAL = "normal";
  public static final String ROW = "row";
  
  public String[] getValues()
  {
    return new String[] { "normal", "row" };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.SQLExec.DelimiterType
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */