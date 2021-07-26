package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.LineOrientedOutputStream;

public class LogOutputStream
  extends LineOrientedOutputStream
{
  private ProjectComponent pc;
  private int level = 2;
  
  public LogOutputStream(ProjectComponent pc)
  {
    this.pc = pc;
  }
  
  public LogOutputStream(Task task, int level)
  {
    this(task, level);
  }
  
  public LogOutputStream(ProjectComponent pc, int level)
  {
    this(pc);
  }
  
  protected void processBuffer()
  {
    try
    {
      super.processBuffer();
    }
    catch (IOException e)
    {
      throw new RuntimeException("Impossible IOException caught: " + e);
    }
  }
  
  protected void processLine(String line)
  {
    processLine(line, level);
  }
  
  protected void processLine(String line, int level)
  {
    pc.log(line, level);
  }
  
  public int getMessageLevel()
  {
    return level;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.LogOutputStream
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */