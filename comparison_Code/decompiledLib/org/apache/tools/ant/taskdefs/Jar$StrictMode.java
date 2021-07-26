package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class Jar$StrictMode
  extends EnumeratedAttribute
{
  public Jar$StrictMode() {}
  
  public Jar$StrictMode(String value)
  {
    setValue(value);
  }
  
  public String[] getValues()
  {
    return new String[] { "fail", "warn", "ignore" };
  }
  
  public int getLogLevel()
  {
    return "ignore".equals(getValue()) ? 3 : 1;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Jar.StrictMode
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */