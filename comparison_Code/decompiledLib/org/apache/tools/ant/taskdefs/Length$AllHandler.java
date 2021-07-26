package org.apache.tools.ant.taskdefs;

import java.io.PrintStream;

class Length$AllHandler
  extends Length.AccumHandler
{
  Length$AllHandler(Length paramLength, PrintStream ps)
  {
    super(paramLength, ps);
  }
  
  void complete()
  {
    getPs().print(getAccum());
    super.complete();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Length.AllHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */