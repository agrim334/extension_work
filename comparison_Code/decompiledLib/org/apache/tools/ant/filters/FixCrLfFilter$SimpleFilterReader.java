package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;

class FixCrLfFilter$SimpleFilterReader
  extends Reader
{
  private static final int PREEMPT_BUFFER_LENGTH = 16;
  private Reader in;
  private int[] preempt = new int[16];
  private int preemptIndex = 0;
  
  public FixCrLfFilter$SimpleFilterReader(Reader in)
  {
    this.in = in;
  }
  
  public void push(char c)
  {
    push(c);
  }
  
  public void push(int c)
  {
    try
    {
      preempt[(preemptIndex++)] = c;
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
      int[] p2 = new int[preempt.length * 2];
      System.arraycopy(preempt, 0, p2, 0, preempt.length);
      preempt = p2;
      push(c);
    }
  }
  
  public void push(char[] cs, int start, int length)
  {
    for (int i = start + length - 1; i >= start;) {
      push(cs[(i--)]);
    }
  }
  
  public void push(char[] cs)
  {
    push(cs, 0, cs.length);
  }
  
  public boolean editsBlocked()
  {
    return ((in instanceof SimpleFilterReader)) && (((SimpleFilterReader)in).editsBlocked());
  }
  
  public int read()
    throws IOException
  {
    return preemptIndex > 0 ? preempt[(--preemptIndex)] : in.read();
  }
  
  public void close()
    throws IOException
  {
    in.close();
  }
  
  public void reset()
    throws IOException
  {
    in.reset();
  }
  
  public boolean markSupported()
  {
    return in.markSupported();
  }
  
  public boolean ready()
    throws IOException
  {
    return in.ready();
  }
  
  public void mark(int i)
    throws IOException
  {
    in.mark(i);
  }
  
  public long skip(long i)
    throws IOException
  {
    return in.skip(i);
  }
  
  public int read(char[] buf)
    throws IOException
  {
    return read(buf, 0, buf.length);
  }
  
  public int read(char[] buf, int start, int length)
    throws IOException
  {
    int count = 0;
    int c = 0;
    while ((length-- > 0) && ((c = read()) != -1))
    {
      buf[(start++)] = ((char)c);
      count++;
    }
    return (count == 0) && (c == -1) ? -1 : count;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.FixCrLfFilter.SimpleFilterReader
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */