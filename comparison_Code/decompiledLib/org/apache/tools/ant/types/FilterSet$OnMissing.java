package org.apache.tools.ant.types;

public class FilterSet$OnMissing
  extends EnumeratedAttribute
{
  private static final String[] VALUES = { "fail", "warn", "ignore" };
  public static final OnMissing FAIL = new OnMissing("fail");
  public static final OnMissing WARN = new OnMissing("warn");
  public static final OnMissing IGNORE = new OnMissing("ignore");
  private static final int FAIL_INDEX = 0;
  private static final int WARN_INDEX = 1;
  private static final int IGNORE_INDEX = 2;
  
  public FilterSet$OnMissing() {}
  
  public FilterSet$OnMissing(String value)
  {
    setValue(value);
  }
  
  public String[] getValues()
  {
    return VALUES;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.FilterSet.OnMissing
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */