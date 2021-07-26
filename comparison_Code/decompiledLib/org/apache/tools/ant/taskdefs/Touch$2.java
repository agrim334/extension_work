package org.apache.tools.ant.taskdefs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

class Touch$2
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
 * Qualified Name:     org.apache.tools.ant.taskdefs.Touch.2
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */