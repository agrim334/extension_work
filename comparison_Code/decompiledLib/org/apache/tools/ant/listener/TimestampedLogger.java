package org.apache.tools.ant.listener;

import org.apache.tools.ant.DefaultLogger;

public class TimestampedLogger
  extends DefaultLogger
{
  public static final String SPACER = " - at ";
  
  protected String getBuildFailedMessage()
  {
    return super.getBuildFailedMessage() + " - at " + getTimestamp();
  }
  
  protected String getBuildSuccessfulMessage()
  {
    return super.getBuildSuccessfulMessage() + " - at " + getTimestamp();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.listener.TimestampedLogger
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */