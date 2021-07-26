package org.apache.tools.ant.listener;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.DefaultLogger;

public class SilentLogger
  extends DefaultLogger
{
  public void buildStarted(BuildEvent event) {}
  
  public void buildFinished(BuildEvent event)
  {
    if (event.getException() != null) {
      super.buildFinished(event);
    }
  }
  
  public void targetStarted(BuildEvent event) {}
  
  public void targetFinished(BuildEvent event) {}
  
  public void taskStarted(BuildEvent event) {}
  
  public void taskFinished(BuildEvent event) {}
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.listener.SilentLogger
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */