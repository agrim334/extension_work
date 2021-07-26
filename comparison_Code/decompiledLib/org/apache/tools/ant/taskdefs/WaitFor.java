package org.apache.tools.ant.taskdefs;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;
import org.apache.tools.ant.types.EnumeratedAttribute;

public class WaitFor
  extends ConditionBase
{
  public static final long ONE_MILLISECOND = 1L;
  public static final long ONE_SECOND = 1000L;
  public static final long ONE_MINUTE = 60000L;
  public static final long ONE_HOUR = 3600000L;
  public static final long ONE_DAY = 86400000L;
  public static final long ONE_WEEK = 604800000L;
  public static final long DEFAULT_MAX_WAIT_MILLIS = 180000L;
  public static final long DEFAULT_CHECK_MILLIS = 500L;
  private long maxWait = 180000L;
  private long maxWaitMultiplier = 1L;
  private long checkEvery = 500L;
  private long checkEveryMultiplier = 1L;
  private String timeoutProperty;
  
  public WaitFor()
  {
    super("waitfor");
  }
  
  public WaitFor(String taskName)
  {
    super(taskName);
  }
  
  public void setMaxWait(long time)
  {
    maxWait = time;
  }
  
  public void setMaxWaitUnit(Unit unit)
  {
    maxWaitMultiplier = unit.getMultiplier();
  }
  
  public void setCheckEvery(long time)
  {
    checkEvery = time;
  }
  
  public void setCheckEveryUnit(Unit unit)
  {
    checkEveryMultiplier = unit.getMultiplier();
  }
  
  public void setTimeoutProperty(String p)
  {
    timeoutProperty = p;
  }
  
  public void execute()
    throws BuildException
  {
    if (countConditions() > 1) {
      throw new BuildException("You must not nest more than one condition into %s", new Object[] {getTaskName() });
    }
    if (countConditions() < 1) {
      throw new BuildException("You must nest a condition into %s", new Object[] {getTaskName() });
    }
    Condition c = (Condition)getConditions().nextElement();
    try
    {
      long maxWaitMillis = calculateMaxWaitMillis();
      long checkEveryMillis = calculateCheckEveryMillis();
      long start = System.currentTimeMillis();
      long end = start + maxWaitMillis;
      while (System.currentTimeMillis() < end)
      {
        if (c.eval())
        {
          processSuccess();
          return;
        }
        Thread.sleep(checkEveryMillis);
      }
    }
    catch (InterruptedException e)
    {
      log("Task " + getTaskName() + " interrupted, treating as timed out.");
    }
    processTimeout();
  }
  
  public long calculateCheckEveryMillis()
  {
    return checkEvery * checkEveryMultiplier;
  }
  
  public long calculateMaxWaitMillis()
  {
    return maxWait * maxWaitMultiplier;
  }
  
  protected void processSuccess()
  {
    log(getTaskName() + ": condition was met", 3);
  }
  
  protected void processTimeout()
  {
    log(getTaskName() + ": timeout", 3);
    if (timeoutProperty != null) {
      getProject().setNewProperty(timeoutProperty, "true");
    }
  }
  
  public static class Unit
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
    
    public Unit()
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
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.WaitFor
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */