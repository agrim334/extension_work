package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.OutputStream;

public class LineOrientedOutputStreamRedirector
  extends LineOrientedOutputStream
{
  private OutputStream stream;
  
  public LineOrientedOutputStreamRedirector(OutputStream stream)
  {
    this.stream = stream;
  }
  
  protected void processLine(byte[] b)
    throws IOException
  {
    stream.write(b);
    stream.write(System.lineSeparator().getBytes());
  }
  
  protected void processLine(String line)
    throws IOException
  {
    stream.write(String.format("%s%n", new Object[] { line }).getBytes());
  }
  
  public void close()
    throws IOException
  {
    super.close();
    stream.close();
  }
  
  public void flush()
    throws IOException
  {
    super.flush();
    stream.flush();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.LineOrientedOutputStreamRedirector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */