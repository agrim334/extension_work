package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.Reader;

class VerifyJar$BufferingOutputFilterReader
  extends Reader
{
  private Reader next;
  private StringBuffer buffer = new StringBuffer();
  
  public VerifyJar$BufferingOutputFilterReader(Reader next)
  {
    this.next = next;
  }
  
  public int read(char[] cbuf, int off, int len)
    throws IOException
  {
    int result = next.read(cbuf, off, len);
    
    buffer.append(cbuf, off, len);
    
    return result;
  }
  
  public void close()
    throws IOException
  {
    next.close();
  }
  
  public String toString()
  {
    return buffer.toString();
  }
  
  public void clear()
  {
    buffer = new StringBuffer();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.VerifyJar.BufferingOutputFilterReader
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */