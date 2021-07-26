package org.apache.tools.ant.taskdefs;

import java.io.PrintStream;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.FileUtils;

abstract class Length$Handler
{
  private PrintStream ps;
  
  Length$Handler(Length paramLength, PrintStream ps)
  {
    this.ps = ps;
  }
  
  protected PrintStream getPs()
  {
    return ps;
  }
  
  protected abstract void handle(Resource paramResource);
  
  void complete()
  {
    FileUtils.close(ps);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Length.Handler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */