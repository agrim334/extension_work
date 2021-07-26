package org.apache.tools.ant.taskdefs;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.tools.ant.types.EnumeratedAttribute;

public class WaitFor$Unit
  extends EnumeratedAttribute
{
  public static final String MILLISECOND = "millisecond";
  public static final String SECOND = "second";
  public static final String MINUTE = "minute";
  public static final String HOUR = "hour";
  public static final String DAY = "day";
  public static final String WEEK = "week";
  private static final String[] UNITS = { "millisecond", "second", "minute", "hour", "day", "week" };
  private Map<String, Long> timeTable = new HashMap();
  
  public WaitFor$Unit()
  {
    timeTable.put("millisecond", Long.valueOf(1L));
    timeTable.put("second", Long.valueOf(1000L));
    timeTable.put("minute", Long.valueOf(60000L));
    timeTable.put("hour", Long.valueOf(3600000L));
    timeTable.put("day", Long.valueOf(86400000L));
    timeTable.put("week", Long.valueOf(604800000L));
  }
  
  public long getMultiplier()
  {
    String key = getValue().toLowerCase(Locale.ENGLISH);
    return ((Long)timeTable.get(key)).longValue();
  }
  
  public String[] getValues()
  {
    return UNITS;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.WaitFor.Unit
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */