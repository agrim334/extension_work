package org.apache.tools.ant.listener;

import java.io.File;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.SubBuildListener;

public class BigProjectLogger
  extends SimpleBigProjectLogger
  implements SubBuildListener
{
  private volatile boolean subBuildStartedRaised = false;
  private final Object subBuildLock = new Object();
  public static final String HEADER = "======================================================================";
  public static final String FOOTER = "======================================================================";
  
  protected String getBuildFailedMessage()
  {
    return super.getBuildFailedMessage() + " - at " + getTimestamp();
  }
  
  protected String getBuildSuccessfulMessage()
  {
    return super.getBuildSuccessfulMessage() + " - at " + getTimestamp();
  }
  
  public void targetStarted(BuildEvent event)
  {
    maybeRaiseSubBuildStarted(event);
    super.targetStarted(event);
  }
  
  public void taskStarted(BuildEvent event)
  {
    maybeRaiseSubBuildStarted(event);
    super.taskStarted(event);
  }
  
  public void buildFinished(BuildEvent event)
  {
    maybeRaiseSubBuildStarted(event);
    subBuildFinished(event);
    super.buildFinished(event);
  }
  
  public void messageLogged(BuildEvent event)
  {
    maybeRaiseSubBuildStarted(event);
    super.messageLogged(event);
  }
  
  public void subBuildStarted(BuildEvent event)
  {
    Project project = event.getProject();
    
    String path = "In " + project.getBaseDir().getAbsolutePath();
    printMessage(String.format("%n%s%nEntering project %s%n%s%n%s", new Object[] { getHeader(), 
      extractNameOrDefault(event), path, getFooter() }), out, event
      
      .getPriority());
  }
  
  protected String extractNameOrDefault(BuildEvent event)
  {
    String name = extractProjectName(event);
    if (name == null) {
      name = "";
    } else {
      name = '"' + name + '"';
    }
    return name;
  }
  
  public void subBuildFinished(BuildEvent event)
  {
    printMessage(String.format("%n%s%nExiting %sproject %s%n%s", new Object[] {
      getHeader(), event.getException() != null ? "failing " : "", 
      extractNameOrDefault(event), getFooter() }), out, event
      
      .getPriority());
  }
  
  protected String getHeader()
  {
    return "======================================================================";
  }
  
  protected String getFooter()
  {
    return "======================================================================";
  }
  
  private void maybeRaiseSubBuildStarted(BuildEvent event)
  {
    if (!subBuildStartedRaised) {
      synchronized (subBuildLock)
      {
        if (!subBuildStartedRaised)
        {
          subBuildStartedRaised = true;
          subBuildStarted(event);
        }
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.listener.BigProjectLogger
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */