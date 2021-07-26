package org.apache.tools.ant.taskdefs.optional;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.EnumeratedAttribute;

public class PropertyFile$Entry
{
  private static final int DEFAULT_INT_VALUE = 0;
  private static final String DEFAULT_DATE_VALUE = "now";
  private static final String DEFAULT_STRING_VALUE = "";
  private String key = null;
  private int type = 2;
  private int operation = 2;
  private String value = null;
  private String defaultValue = null;
  private String newValue = null;
  private String pattern = null;
  private int field = 5;
  
  public void setKey(String value)
  {
    key = value;
  }
  
  public void setValue(String value)
  {
    this.value = value;
  }
  
  public void setOperation(Operation value)
  {
    operation = Operation.toOperation(value.getValue());
  }
  
  public void setType(Type value)
  {
    type = Type.toType(value.getValue());
  }
  
  public void setDefault(String value)
  {
    defaultValue = value;
  }
  
  public void setPattern(String value)
  {
    pattern = value;
  }
  
  public void setUnit(PropertyFile.Unit unit)
  {
    field = unit.getCalendarField();
  }
  
  protected void executeOn(Properties props)
    throws BuildException
  {
    checkParameters();
    if (operation == 3)
    {
      props.remove(key);
      return;
    }
    String oldValue = (String)props.get(key);
    try
    {
      if (type == 0) {
        executeInteger(oldValue);
      } else if (type == 1) {
        executeDate(oldValue);
      } else if (type == 2) {
        executeString(oldValue);
      } else {
        throw new BuildException("Unknown operation type: %d", new Object[] { Integer.valueOf(type) });
      }
    }
    catch (NullPointerException npe)
    {
      npe.printStackTrace();
    }
    if (newValue == null) {
      newValue = "";
    }
    props.put(key, newValue);
  }
  
  private void executeDate(String oldValue)
    throws BuildException
  {
    Calendar currentValue = Calendar.getInstance();
    if (pattern == null) {
      pattern = "yyyy/MM/dd HH:mm";
    }
    DateFormat fmt = new SimpleDateFormat(pattern);
    
    String currentStringValue = getCurrentValue(oldValue);
    if (currentStringValue == null) {
      currentStringValue = "now";
    }
    if ("now".equals(currentStringValue)) {
      currentValue.setTime(new Date());
    } else {
      try
      {
        currentValue.setTime(fmt.parse(currentStringValue));
      }
      catch (ParseException localParseException) {}
    }
    if (operation != 2)
    {
      int offset = 0;
      try
      {
        offset = Integer.parseInt(value);
        if (operation == 1) {
          offset = -1 * offset;
        }
      }
      catch (NumberFormatException e)
      {
        throw new BuildException("Value not an integer on " + key);
      }
      currentValue.add(field, offset);
    }
    newValue = fmt.format(currentValue.getTime());
  }
  
  private void executeInteger(String oldValue)
    throws BuildException
  {
    int currentValue = 0;
    int newV = 0;
    
    DecimalFormat fmt = pattern != null ? new DecimalFormat(pattern) : new DecimalFormat();
    try
    {
      String curval = getCurrentValue(oldValue);
      if (curval != null) {
        currentValue = fmt.parse(curval).intValue();
      } else {
        currentValue = 0;
      }
    }
    catch (NumberFormatException|ParseException localNumberFormatException) {}
    if (operation == 2)
    {
      newV = currentValue;
    }
    else
    {
      int operationValue = 1;
      if (value != null) {
        try
        {
          operationValue = fmt.parse(value).intValue();
        }
        catch (NumberFormatException|ParseException localNumberFormatException1) {}
      }
      if (operation == 0) {
        newV = currentValue + operationValue;
      } else if (operation == 1) {
        newV = currentValue - operationValue;
      }
    }
    newValue = fmt.format(newV);
  }
  
  private void executeString(String oldValue)
    throws BuildException
  {
    String newV = "";
    
    String currentValue = getCurrentValue(oldValue);
    if (currentValue == null) {
      currentValue = "";
    }
    if (operation == 2) {
      newV = currentValue;
    } else if (operation == 0) {
      newV = currentValue + value;
    }
    newValue = newV;
  }
  
  private void checkParameters()
    throws BuildException
  {
    if ((type == 2) && (operation == 1)) {
      throw new BuildException("- is not supported for string properties (key:" + key + ")");
    }
    if ((value == null) && (defaultValue == null) && (operation != 3)) {
      throw new BuildException("\"value\" and/or \"default\" attribute must be specified (key: %s)", new Object[] { key });
    }
    if (key == null) {
      throw new BuildException("key is mandatory");
    }
    if ((type == 2) && (pattern != null)) {
      throw new BuildException("pattern is not supported for string properties (key: %s)", new Object[] { key });
    }
  }
  
  private String getCurrentValue(String oldValue)
  {
    String ret = null;
    if (operation == 2)
    {
      if ((value != null) && (defaultValue == null)) {
        ret = value;
      }
      if ((value == null) && (defaultValue != null) && (oldValue != null)) {
        ret = oldValue;
      }
      if ((value == null) && (defaultValue != null) && (oldValue == null)) {
        ret = defaultValue;
      }
      if ((value != null) && (defaultValue != null) && (oldValue != null)) {
        ret = value;
      }
      if ((value != null) && (defaultValue != null) && (oldValue == null)) {
        ret = defaultValue;
      }
    }
    else
    {
      ret = oldValue == null ? defaultValue : oldValue;
    }
    return ret;
  }
  
  public static class Operation
    extends EnumeratedAttribute
  {
    public static final int INCREMENT_OPER = 0;
    public static final int DECREMENT_OPER = 1;
    public static final int EQUALS_OPER = 2;
    public static final int DELETE_OPER = 3;
    
    public String[] getValues()
    {
      return new String[] { "+", "-", "=", "del" };
    }
    
    public static int toOperation(String oper)
    {
      if ("+".equals(oper)) {
        return 0;
      }
      if ("-".equals(oper)) {
        return 1;
      }
      if ("del".equals(oper)) {
        return 3;
      }
      return 2;
    }
  }
  
  public static class Type
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
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.PropertyFile.Entry
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */