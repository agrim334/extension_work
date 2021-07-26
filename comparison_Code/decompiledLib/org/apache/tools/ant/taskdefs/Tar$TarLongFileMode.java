package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class Tar$TarLongFileMode
  extends EnumeratedAttribute
{
  public static final String WARN = "warn";
  public static final String FAIL = "fail";
  public static final String TRUNCATE = "truncate";
  public static final String GNU = "gnu";
  public static final String POSIX = "posix";
  public static final String OMIT = "omit";
  private static final String[] VALID_MODES = { "warn", "fail", "truncate", "gnu", "posix", "omit" };
  
  public Tar$TarLongFileMode()
  {
    setValue("warn");
  }
  
  public String[] getValues()
  {
    return VALID_MODES;
  }
  
  public boolean isTruncateMode()
  {
    return "truncate".equalsIgnoreCase(getValue());
  }
  
  public boolean isWarnMode()
  {
    return "warn".equalsIgnoreCase(getValue());
  }
  
  public boolean isGnuMode()
  {
    return "gnu".equalsIgnoreCase(getValue());
  }
  
  public boolean isFailMode()
  {
    return "fail".equalsIgnoreCase(getValue());
  }
  
  public boolean isOmitMode()
  {
    return "omit".equalsIgnoreCase(getValue());
  }
  
  public boolean isPosixMode()
  {
    return "posix".equalsIgnoreCase(getValue());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Tar.TarLongFileMode
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */