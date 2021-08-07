package org.codehaus.plexus.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @deprecated
 */
public class StringOutputStream
  extends OutputStream
{
  private StringBuffer buf = new StringBuffer();
  
  public void write(byte[] b)
    throws IOException
  {
    buf.append(new String(b));
  }
  
  public void write(byte[] b, int off, int len)
    throws IOException
  {
    buf.append(new String(b, off, len));
  }
  
  public void write(int b)
    throws IOException
  {
    byte[] bytes = new byte[1];
    bytes[0] = ((byte)b);
    buf.append(new String(bytes));
  }
  
  public String toString()
  {
    return buf.toString();
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.StringOutputStream
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */