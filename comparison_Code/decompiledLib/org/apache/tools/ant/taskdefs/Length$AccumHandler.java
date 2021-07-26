package org.apache.tools.ant.taskdefs;

import java.io.PrintStream;
import org.apache.tools.ant.types.Resource;

class Length$AccumHandler
  extends Length.Handler
{
  private long accum = 0L;
  
  Length$AccumHandler(Length paramLength)
  {
    super(paramLength, null);
  }
  
  protected Length$AccumHandler(Length paramLength, PrintStream ps)
  {
    super(paramLength, ps);
  }
  
  protected long getAccum()
  {
    return accum;
  }
  
  protected synchronized void handle(Resource r)
  {
    long size = r.getSize();
    if (size == -1L) {
      this$0.log("Size unknown for " + r.toString(), 1);
    } else {
      accum += size;
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Length.AccumHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */