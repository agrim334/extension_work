package org.apache.tools.ant.taskdefs.optional;

import java.util.HashMap;
import java.util.Map;
import org.apache.tools.ant.types.EnumeratedAttribute;

public class PropertyFile$Unit
  extends EnumeratedAttribute
{
  private static final String MILLISECOND = "millisecond";
  private static final String SECOND = "second";
  private static final String MINUTE = "minute";
  private static final String HOUR = "hour";
  private static final String DAY = "day";
  private static final String WEEK = "week";
  private static final String MONTH = "month";
  private static final String YEAR = "year";
  private static final String[] UNITS = { "millisecond", "second", "minute", "hour", "day", "week", "month", "year" };
  private Map<String, Integer> calendarFields = new HashMap();
  
  public PropertyFile$Unit()
  {
    calendarFields.put("millisecond", 
      Integer.valueOf(14));
    calendarFields.put("second", Integer.valueOf(13));
    calendarFields.put("minute", Integer.valueOf(12));
    calendarFields.put("hour", Integer.valueOf(11));
    calendarFields.put("day", Integer.valueOf(5));
    calendarFields.put("week", Integer.valueOf(3));
    calendarFields.put("month", Integer.valueOf(2));
    calendarFields.put("year", Integer.valueOf(1));
  }
  
  public int getCalendarField()
  {
    return ((Integer)calendarFields.get(getValue().toLowerCase())).intValue();
  }
  
  public String[] getValues()
  {
    return UNITS;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.PropertyFile.Unit
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */