package org.apache.tools.ant;

import java.io.PrintStream;

public class NoBannerLogger
  extends DefaultLogger
{
  protected String targetName;
  
  public synchronized void targetStarted(BuildEvent event)
  {
    targetName = extractTargetName(event);
  }
  
  protected String extractTargetName(BuildEvent event)
  {
    return event.getTarget().getName();
  }
  
  public synchronized void targetFinished(BuildEvent event)
  {
    targetName = null;
  }
  
  public void messageLogged(BuildEvent event)
  {
    if ((event.getPriority() > msgOutputLevel) || 
      (null == event.getMessage()) || 
      (event.getMessage().trim().isEmpty())) {
      return;
    }
    synchronized (this)
    {
      if (null != targetName)
      {
        out.println(String.format("%n%s:", new Object[] { targetName }));
        targetName = null;
      }
    }
    super.messageLogged(event);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.NoBannerLogger
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */