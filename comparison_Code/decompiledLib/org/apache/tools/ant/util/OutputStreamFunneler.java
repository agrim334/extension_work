package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamFunneler
{
  public static final long DEFAULT_TIMEOUT_MILLIS = 1000L;
  private OutputStream out;
  
  private final class Funnel
    extends OutputStream
  {
    private boolean closed = false;
    
    private Funnel()
    {
      synchronized (OutputStreamFunneler.this)
      {
        OutputStreamFunneler.access$004(OutputStreamFunneler.this);
      }
    }
    
    public void flush()
      throws IOException
    {
      synchronized (OutputStreamFunneler.this)
      {
        OutputStreamFunneler.this.dieIfClosed();
        out.flush();
      }
    }
    
    public void write(int b)
      throws IOException
    {
      synchronized (OutputStreamFunneler.this)
      {
        OutputStreamFunneler.this.dieIfClosed();
        out.write(b);
      }
    }
    
    public void write(byte[] b)
      throws IOException
    {
      synchronized (OutputStreamFunneler.this)
      {
        OutputStreamFunneler.this.dieIfClosed();
        out.write(b);
      }
    }
    
    public void write(byte[] b, int off, int len)
      throws IOException
    {
      synchronized (OutputStreamFunneler.this)
      {
        OutputStreamFunneler.this.dieIfClosed();
        out.write(b, off, len);
      }
    }
    
    public void close()
      throws IOException
    {
      OutputStreamFunneler.this.release(this);
    }
  }
  
  private int count = 0;
  private boolean closed;
  private long timeoutMillis;
  
  public OutputStreamFunneler(OutputStream out)
  {
    this(out, 1000L);
  }
  
  public OutputStreamFunneler(OutputStream out, long timeoutMillis)
  {
    if (out == null) {
      throw new IllegalArgumentException("OutputStreamFunneler.<init>:  out == null");
    }
    this.out = out;
    closed = false;
    setTimeout(timeoutMillis);
  }
  
  public synchronized void setTimeout(long timeoutMillis)
  {
    this.timeoutMillis = timeoutMillis;
  }
  
  public synchronized OutputStream getFunnelInstance()
    throws IOException
  {
    dieIfClosed();
    try
    {
      return new Funnel(null);
    }
    finally
    {
      notifyAll();
    }
  }
  
  private synchronized void release(Funnel funnel)
    throws IOException
  {
    if (!closed) {
      try
      {
        if (timeoutMillis > 0L)
        {
          long start = System.currentTimeMillis();
          long end = start + timeoutMillis;
          long now = System.currentTimeMillis();
          try
          {
            while (now < end)
            {
              wait(end - now);
              now = System.currentTimeMillis();
            }
          }
          catch (InterruptedException localInterruptedException) {}
        }
        if (--count == 0) {
          close();
        }
      }
      finally
      {
        closed = true;
      }
    }
  }
  
  /* Error */
  private synchronized void close()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 11	org/apache/tools/ant/util/OutputStreamFunneler:dieIfClosed	()V
    //   4: aload_0
    //   5: getfield 7	org/apache/tools/ant/util/OutputStreamFunneler:out	Ljava/io/OutputStream;
    //   8: invokevirtual 78	java/io/OutputStream:close	()V
    //   11: aload_0
    //   12: iconst_1
    //   13: putfield 36	org/apache/tools/ant/util/OutputStreamFunneler:closed	Z
    //   16: goto +11 -> 27
    //   19: astore_1
    //   20: aload_0
    //   21: iconst_1
    //   22: putfield 36	org/apache/tools/ant/util/OutputStreamFunneler:closed	Z
    //   25: aload_1
    //   26: athrow
    //   27: return
    // Line number table:
    //   Java source line #174	-> byte code offset #0
    //   Java source line #175	-> byte code offset #4
    //   Java source line #177	-> byte code offset #11
    //   Java source line #178	-> byte code offset #16
    //   Java source line #177	-> byte code offset #19
    //   Java source line #178	-> byte code offset #25
    //   Java source line #179	-> byte code offset #27
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	28	0	this	OutputStreamFunneler
    //   19	7	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	11	19	finally
  }
  
  private synchronized void dieIfClosed()
    throws IOException
  {
    if (closed) {
      throw new IOException("The funneled OutputStream has been closed.");
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.OutputStreamFunneler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */