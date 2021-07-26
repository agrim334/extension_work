package org.apache.tools.ant.taskdefs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

class Redirector$PropertyOutputStream
  extends ByteArrayOutputStream
{
  private final String property;
  private boolean closed = false;
  
  Redirector$PropertyOutputStream(Redirector paramRedirector, String property)
  {
    this.property = property;
  }
  
  public void close()
    throws IOException
  {
    synchronized (Redirector.access$000(this$0))
    {
      if ((!closed) && ((!Redirector.access$100(this$0)) || (!Redirector.access$200(this$0))))
      {
        Redirector.access$300(this$0, this, property);
        closed = true;
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Redirector.PropertyOutputStream
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */