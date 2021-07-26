package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.TimeComparison;
import org.apache.tools.ant.util.FileUtils;

public class DateSelector
  extends BaseExtendSelector
{
  public static final String MILLIS_KEY = "millis";
  public static final String DATETIME_KEY = "datetime";
  public static final String CHECKDIRS_KEY = "checkdirs";
  public static final String GRANULARITY_KEY = "granularity";
  public static final String WHEN_KEY = "when";
  public static final String PATTERN_KEY = "pattern";
  private static final FileUtils FILE_UTILS = ;
  private long millis = -1L;
  private String dateTime = null;
  private boolean includeDirs = false;
  private long granularity = FILE_UTILS.getFileTimestampGranularity();
  private String pattern;
  private TimeComparison when = TimeComparison.EQUAL;
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder("{dateselector date: ");
    buf.append(dateTime);
    buf.append(" compare: ").append(when.getValue());
    buf.append(" granularity: ").append(granularity);
    if (pattern != null) {
      buf.append(" pattern: ").append(pattern);
    }
    buf.append("}");
    return buf.toString();
  }
  
  public void setMillis(long millis)
  {
    this.millis = millis;
  }
  
  public long getMillis()
  {
    if (dateTime != null) {
      validate();
    }
    return millis;
  }
  
  public void setDatetime(String dateTime)
  {
    this.dateTime = dateTime;
    millis = -1L;
  }
  
  public void setCheckdirs(boolean includeDirs)
  {
    this.includeDirs = includeDirs;
  }
  
  public void setGranularity(int granularity)
  {
    this.granularity = granularity;
  }
  
  public void setWhen(TimeComparisons tcmp)
  {
    setWhen(tcmp);
  }
  
  public void setWhen(TimeComparison t)
  {
    when = t;
  }
  
  public void setPattern(String pattern)
  {
    this.pattern = pattern;
  }
  
  public void setParameters(Parameter... parameters)
  {
    super.setParameters(parameters);
    if (parameters != null) {
      for (Parameter parameter : parameters)
      {
        String paramname = parameter.getName();
        if ("millis".equalsIgnoreCase(paramname)) {
          try
          {
            setMillis(Long.parseLong(parameter.getValue()));
          }
          catch (NumberFormatException nfe)
          {
            setError("Invalid millisecond setting " + parameter
              .getValue());
          }
        } else if ("datetime".equalsIgnoreCase(paramname)) {
          setDatetime(parameter.getValue());
        } else if ("checkdirs".equalsIgnoreCase(paramname)) {
          setCheckdirs(Project.toBoolean(parameter.getValue()));
        } else if ("granularity".equalsIgnoreCase(paramname)) {
          try
          {
            setGranularity(Integer.parseInt(parameter.getValue()));
          }
          catch (NumberFormatException nfe)
          {
            setError("Invalid granularity setting " + parameter
              .getValue());
          }
        } else if ("when".equalsIgnoreCase(paramname)) {
          setWhen(new TimeComparison(parameter.getValue()));
        } else if ("pattern".equalsIgnoreCase(paramname)) {
          setPattern(parameter.getValue());
        } else {
          setError("Invalid parameter " + paramname);
        }
      }
    }
  }
  
  public void verifySettings()
  {
    if ((dateTime == null) && (millis < 0L))
    {
      setError("You must provide a datetime or the number of milliseconds.");
    }
    else if ((millis < 0L) && (dateTime != null))
    {
      String p = pattern == null ? "MM/dd/yyyy hh:mm a" : pattern;
      
      DateFormat df = pattern == null ? new SimpleDateFormat(p, Locale.US) : new SimpleDateFormat(p);
      try
      {
        setMillis(df.parse(dateTime).getTime());
        if (millis < 0L) {
          setError("Date of " + dateTime + " results in negative milliseconds value relative to epoch (January 1, 1970, 00:00:00 GMT).");
        }
      }
      catch (ParseException pe)
      {
        setError("Date of " + dateTime + " Cannot be parsed correctly. It should be in '" + p + "' format.", pe);
      }
    }
  }
  
  public boolean isSelected(File basedir, String filename, File file)
  {
    validate();
    return ((file.isDirectory()) && (!includeDirs)) || 
      (when.evaluate(file.lastModified(), millis, granularity));
  }
  
  public static class TimeComparisons
    extends TimeComparison
  {}
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.DateSelector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */