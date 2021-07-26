package org.apache.tools.ant.taskdefs.condition;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.tools.ant.taskdefs.Touch.DateFormatFactory;

class IsLastModified$1
  implements Touch.DateFormatFactory
{
  public DateFormat getPrimaryFormat()
  {
    return new SimpleDateFormat(val$pattern);
  }
  
  public DateFormat getFallbackFormat()
  {
    return null;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.condition.IsLastModified.1
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */