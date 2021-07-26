package org.apache.tools.ant.taskdefs.optional;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class PropertyFile$Entry$Type
  extends EnumeratedAttribute
{
  public static final int INTEGER_TYPE = 0;
  public static final int DATE_TYPE = 1;
  public static final int STRING_TYPE = 2;
  
  public String[] getValues()
  {
    return new String[] { "int", "date", "string" };
  }
  
  public static int toType(String type)
  {
    if ("int".equals(type)) {
      return 0;
    }
    if ("date".equals(type)) {
      return 1;
    }
    return 2;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.PropertyFile.Entry.Type
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */