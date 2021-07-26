package org.apache.tools.ant.listener;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.NoBannerLogger;

public class SimpleBigProjectLogger
  extends NoBannerLogger
{
  protected String extractTargetName(BuildEvent event)
  {
    String targetName = super.extractTargetName(event);
    String projectName = extractProjectName(event);
    if ((projectName == null) || (targetName == null)) {
      return targetName;
    }
    return projectName + '.' + targetName;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.listener.SimpleBigProjectLogger
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */