package org.apache.tools.ant.taskdefs.condition;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.Touch;
import org.apache.tools.ant.taskdefs.Touch.DateFormatFactory;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Resource;

public class IsLastModified
  extends ProjectComponent
  implements Condition
{
  private long millis = -1L;
  private String dateTime = null;
  private Touch.DateFormatFactory dfFactory = Touch.DEFAULT_DF_FACTORY;
  private Resource resource;
  private CompareMode mode = CompareMode.EQUALS;
  
  public void setMillis(long millis)
  {
    this.millis = millis;
  }
  
  public void setDatetime(String dateTime)
  {
    this.dateTime = dateTime;
  }
  
  public void setPattern(final String pattern)
  {
    dfFactory = new Touch.DateFormatFactory()
    {
      public DateFormat getPrimaryFormat()
      {
        return new SimpleDateFormat(pattern);
      }
      
      public DateFormat getFallbackFormat()
      {
        return null;
      }
    };
  }
  
  public void add(Resource r)
  {
    if (resource != null) {
      throw new BuildException("only one resource can be tested");
    }
    resource = r;
  }
  
  public void setMode(CompareMode mode)
  {
    this.mode = mode;
  }
  
  protected void validate()
    throws BuildException
  {
    if ((millis >= 0L) && (dateTime != null)) {
      throw new BuildException("Only one of dateTime and millis can be set");
    }
    if ((millis < 0L) && (dateTime == null)) {
      throw new BuildException("millis or dateTime is required");
    }
    if (resource == null) {
      throw new BuildException("resource is required");
    }
  }
  
  protected long getMillis()
    throws BuildException
  {
    if (millis >= 0L) {
      return millis;
    }
    if ("now".equalsIgnoreCase(dateTime)) {
      return System.currentTimeMillis();
    }
    DateFormat df = dfFactory.getPrimaryFormat();
    try
    {
      return df.parse(dateTime).getTime();
    }
    catch (ParseException peOne)
    {
      df = dfFactory.getFallbackFormat();
      ParseException pe;
      ParseException pe;
      if (df == null) {
        pe = peOne;
      } else {
        try
        {
          return df.parse(dateTime).getTime();
        }
        catch (ParseException peTwo)
        {
          pe = peTwo;
        }
      }
      throw new BuildException(pe.getMessage(), pe, getLocation());
    }
  }
  
  public boolean eval()
    throws BuildException
  {
    validate();
    long expected = getMillis();
    long actual = resource.getLastModified();
    log("expected timestamp: " + expected + " (" + new Date(expected) + "), actual timestamp: " + actual + " (" + new Date(actual) + ")", 3);
    if ("equals".equals(mode.getValue())) {
      return expected == actual;
    }
    if ("before".equals(mode.getValue())) {
      return expected > actual;
    }
    if ("not-before".equals(mode.getValue())) {
      return expected <= actual;
    }
    if ("after".equals(mode.getValue())) {
      return expected < actual;
    }
    if ("not-after".equals(mode.getValue())) {
      return expected >= actual;
    }
    throw new BuildException("Unknown mode " + mode.getValue());
  }
  
  public static class CompareMode
    extends EnumeratedAttribute
  {
    private static final String EQUALS_TEXT = "equals";
    private static final String BEFORE_TEXT = "before";
    private static final String AFTER_TEXT = "after";
    private static final String NOT_BEFORE_TEXT = "not-before";
    private static final String NOT_AFTER_TEXT = "not-after";
    private static final CompareMode EQUALS = new CompareMode("equals");
    
    public CompareMode()
    {
      this("equals");
    }
    
    public CompareMode(String s)
    {
      setValue(s);
    }
    
    public String[] getValues()
    {
      return new String[] { "equals", "before", "after", "not-before", "not-after" };
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.condition.IsLastModified
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */