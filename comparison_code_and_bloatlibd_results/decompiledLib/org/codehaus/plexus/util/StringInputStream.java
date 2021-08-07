package org.codehaus.plexus.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

/**
 * @deprecated
 */
public class StringInputStream
  extends InputStream
{
  private StringReader in;
  
  public StringInputStream(String source)
  {
    in = new StringReader(source);
  }
  
  public int read()
    throws IOException
  {
    return in.read();
  }
  
  public void close()
    throws IOException
  {
    in.close();
  }
  
  public synchronized void mark(int limit)
  {
    try
    {
      in.mark(limit);
    }
    catch (IOException ioe)
    {
      throw new RuntimeException(ioe.getMessage());
    }
  }
  
  public synchronized void reset()
    throws IOException
  {
    in.reset();
  }
  
  public boolean markSupported()
  {
    return in.markSupported();
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.StringInputStream
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */