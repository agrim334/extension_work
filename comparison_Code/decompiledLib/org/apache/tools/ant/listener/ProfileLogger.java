package org.apache.tools.ant.listener;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

public class ProfileLogger
  extends DefaultLogger
{
  private Map<Object, Date> profileData = new ConcurrentHashMap();
  
  public void targetStarted(BuildEvent event)
  {
    Date now = new Date();
    String name = "Target " + event.getTarget().getName();
    logStart(event, now, name);
    profileData.put(event.getTarget(), now);
  }
  
  public void targetFinished(BuildEvent event)
  {
    Date start = (Date)profileData.remove(event.getTarget());
    String name = "Target " + event.getTarget().getName();
    logFinish(event, start, name);
  }
  
  public void taskStarted(BuildEvent event)
  {
    String name = event.getTask().getTaskName();
    Date now = new Date();
    logStart(event, now, name);
    profileData.put(event.getTask(), now);
  }
  
  public void taskFinished(BuildEvent event)
  {
    Date start = (Date)profileData.remove(event.getTask());
    String name = event.getTask().getTaskName();
    logFinish(event, start, name);
  }
  
  private void logFinish(BuildEvent event, Date start, String name)
  {
    Date now = new Date();
    String msg;
    String msg;
    if (start != null)
    {
      long diff = now.getTime() - start.getTime();
      msg = String.format("%n%s: finished %s (%d)", new Object[] { name, now, Long.valueOf(diff) });
    }
    else
    {
      msg = String.format("%n%s: finished %s (unknown duration, start not detected)", new Object[] { name, now });
    }
    printMessage(msg, out, event.getPriority());
    log(msg);
  }
  
  private void logStart(BuildEvent event, Date start, String name)
  {
    String msg = String.format("%n%s: started %s", new Object[] { name, start });
    printMessage(msg, out, event.getPriority());
    log(msg);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.listener.ProfileLogger
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */