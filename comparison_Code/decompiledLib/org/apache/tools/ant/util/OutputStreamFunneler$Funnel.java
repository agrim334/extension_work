package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.OutputStream;

final class OutputStreamFunneler$Funnel
  extends OutputStream
{
  private boolean closed = false;
  
  private OutputStreamFunneler$Funnel(OutputStreamFunneler paramOutputStreamFunneler)
  {
    synchronized (paramOutputStreamFunneler)
    {
      OutputStreamFunneler.access$004(paramOutputStreamFunneler);
    }
  }
  
  public void flush()
    throws IOException
  {
    synchronized (this$0)
    {
      OutputStreamFunneler.access$100(this$0);
      OutputStreamFunneler.access$200(this$0).flush();
    }
  }
  
  public void write(int b)
    throws IOException
  {
    synchronized (this$0)
    {
      OutputStreamFunneler.access$100(this$0);
      OutputStreamFunneler.access$200(this$0).write(b);
    }
  }
  
  public void write(byte[] b)
    throws IOException
  {
    synchronized (this$0)
    {
      OutputStreamFunneler.access$100(this$0);
      OutputStreamFunneler.access$200(this$0).write(b);
    }
  }
  
  public void write(byte[] b, int off, int len)
    throws IOException
  {
    synchronized (this$0)
    {
      OutputStreamFunneler.access$100(this$0);
      OutputStreamFunneler.access$200(this$0).write(b, off, len);
    }
  }
  
  public void close()
    throws IOException
  {
    OutputStreamFunneler.access$300(this$0, this);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.OutputStreamFunneler.Funnel
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */