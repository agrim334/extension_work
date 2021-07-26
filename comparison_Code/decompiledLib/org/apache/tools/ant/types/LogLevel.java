package org.apache.tools.ant.types;

public class LogLevel
  extends EnumeratedAttribute
{
  public static final LogLevel ERR = new LogLevel("error");
  public static final LogLevel WARN = new LogLevel("warn");
  public static final LogLevel INFO = new LogLevel("info");
  public static final LogLevel VERBOSE = new LogLevel("verbose");
  public static final LogLevel DEBUG = new LogLevel("debug");
  
  public LogLevel() {}
  
  private LogLevel(String value)
  {
    this();
    setValue(value);
  }
  
  public String[] getValues()
  {
    return new String[] { "error", "warn", "warning", "info", "verbose", "debug" };
  }
  
  private static int[] levels = { 0, 1, 1, 2, 3, 4 };
  
  public int getLevel()
  {
    return levels[getIndex()];
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.LogLevel
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */