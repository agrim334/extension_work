package org.apache.tools.ant.taskdefs;

import java.io.PrintStream;
import org.apache.tools.ant.types.Resource;

class Length$EachHandler
  extends Length.Handler
{
  Length$EachHandler(Length paramLength, PrintStream ps)
  {
    super(paramLength, ps);
  }
  
  protected void handle(Resource r)
  {
    getPs().print(r.toString());
    getPs().print(" : ");
    
    long size = r.getSize();
    if (size == -1L) {
      getPs().println("unknown");
    } else {
      getPs().println(size);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Length.EachHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */