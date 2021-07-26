package org.apache.tools.ant.taskdefs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TimeZone;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;

public class Tstamp$CustomFormat
{
  private TimeZone timeZone;
  private String propertyName;
  private String pattern;
  private String language;
  private String country;
  private String variant;
  private int offset = 0;
  private int field = 5;
  
  public Tstamp$CustomFormat(Tstamp this$0) {}
  
  public void setProperty(String propertyName)
  {
    this.propertyName = propertyName;
  }
  
  public void setPattern(String pattern)
  {
    this.pattern = pattern;
  }
  
  public void setLocale(String locale)
  {
    StringTokenizer st = new StringTokenizer(locale, " \t\n\r\f,");
    try
    {
      language = st.nextToken();
      if (st.hasMoreElements())
      {
        country = st.nextToken();
        if (st.hasMoreElements())
        {
          variant = st.nextToken();
          if (st.hasMoreElements()) {
            throw new BuildException("bad locale format", this$0.getLocation());
          }
        }
      }
      else
      {
        country = "";
      }
    }
    catch (NoSuchElementException e)
    {
      throw new BuildException("bad locale format", e, this$0.getLocation());
    }
  }
  
  public void setTimezone(String id)
  {
    timeZone = TimeZone.getTimeZone(id);
  }
  
  public void setOffset(int offset)
  {
    this.offset = offset;
  }
  
  @Deprecated
  public void setUnit(String unit)
  {
    this$0.log("DEPRECATED - The setUnit(String) method has been deprecated. Use setUnit(Tstamp.Unit) instead.");
    Tstamp.Unit u = new Tstamp.Unit();
    u.setValue(unit);
    field = u.getCalendarField();
  }
  
  public void setUnit(Tstamp.Unit unit)
  {
    field = unit.getCalendarField();
  }
  
  public void execute(Project project, Date date, Location location)
  {
    if (propertyName == null) {
      throw new BuildException("property attribute must be provided", location);
    }
    if (pattern == null) {
      throw new BuildException("pattern attribute must be provided", location);
    }
    SimpleDateFormat sdf;
    SimpleDateFormat sdf;
    if (language == null)
    {
      sdf = new SimpleDateFormat(pattern);
    }
    else
    {
      SimpleDateFormat sdf;
      if (variant == null) {
        sdf = new SimpleDateFormat(pattern, new Locale(language, country));
      } else {
        sdf = new SimpleDateFormat(pattern, new Locale(language, country, variant));
      }
    }
    if (offset != 0)
    {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      calendar.add(field, offset);
      date = calendar.getTime();
    }
    if (timeZone != null) {
      sdf.setTimeZone(timeZone);
    }
    Tstamp.access$000(this$0, propertyName, sdf.format(date));
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Tstamp.CustomFormat
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */