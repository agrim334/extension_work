package org.apache.tools.ant.taskdefs;

import java.text.DateFormat;
import org.apache.tools.ant.util.DateUtils;

class Touch$1
  implements Touch.DateFormatFactory
{
  public DateFormat getPrimaryFormat()
  {
    return (DateFormat)DateUtils.EN_US_DATE_FORMAT_MIN.get();
  }
  
  public DateFormat getFallbackFormat()
  {
    return (DateFormat)DateUtils.EN_US_DATE_FORMAT_SEC.get();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Touch.1
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */