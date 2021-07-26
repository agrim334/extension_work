package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class IsLastModified$CompareMode
  extends EnumeratedAttribute
{
  private static final String EQUALS_TEXT = "equals";
  private static final String BEFORE_TEXT = "before";
  private static final String AFTER_TEXT = "after";
  private static final String NOT_BEFORE_TEXT = "not-before";
  private static final String NOT_AFTER_TEXT = "not-after";
  private static final CompareMode EQUALS = new CompareMode("equals");
  
  public IsLastModified$CompareMode()
  {
    this("equals");
  }
  
  public IsLastModified$CompareMode(String s)
  {
    setValue(s);
  }
  
  public String[] getValues()
  {
    return new String[] { "equals", "before", "after", "not-before", "not-after" };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.condition.IsLastModified.CompareMode
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */