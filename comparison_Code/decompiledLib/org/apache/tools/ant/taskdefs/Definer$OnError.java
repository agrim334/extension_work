package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class Definer$OnError
  extends EnumeratedAttribute
{
  public static final int FAIL = 0;
  public static final int REPORT = 1;
  public static final int IGNORE = 2;
  public static final int FAIL_ALL = 3;
  public static final String POLICY_FAIL = "fail";
  public static final String POLICY_REPORT = "report";
  public static final String POLICY_IGNORE = "ignore";
  public static final String POLICY_FAILALL = "failall";
  
  public Definer$OnError() {}
  
  public Definer$OnError(String value)
  {
    setValue(value);
  }
  
  public String[] getValues()
  {
    return new String[] { "fail", "report", "ignore", "failall" };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Definer.OnError
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */