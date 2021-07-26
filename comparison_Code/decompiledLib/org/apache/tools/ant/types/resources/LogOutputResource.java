package org.apache.tools.ant.types.resources;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.types.Resource;

public class LogOutputResource
  extends Resource
  implements Appendable
{
  private static final String NAME = "[Ant log]";
  private LogOutputStream outputStream;
  
  public LogOutputResource(ProjectComponent managingComponent)
  {
    super("[Ant log]");
    outputStream = new LogOutputStream(managingComponent);
  }
  
  public LogOutputResource(ProjectComponent managingComponent, int level)
  {
    super("[Ant log]");
    outputStream = new LogOutputStream(managingComponent, level);
  }
  
  public OutputStream getAppendOutputStream()
    throws IOException
  {
    return outputStream;
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    return outputStream;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.LogOutputResource
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */