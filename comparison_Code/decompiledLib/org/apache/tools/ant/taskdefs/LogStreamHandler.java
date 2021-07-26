package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;

public class LogStreamHandler
  extends PumpStreamHandler
{
  public LogStreamHandler(Task task, int outlevel, int errlevel)
  {
    this(task, outlevel, errlevel);
  }
  
  public LogStreamHandler(ProjectComponent pc, int outlevel, int errlevel)
  {
    super(new LogOutputStream(pc, outlevel), new LogOutputStream(pc, errlevel));
  }
  
  public void stop()
  {
    super.stop();
    FileUtils.close(getErr());
    FileUtils.close(getOut());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.LogStreamHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */