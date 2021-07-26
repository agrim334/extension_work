package org.apache.tools.ant.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class KeepAliveOutputStream
  extends FilterOutputStream
{
  public KeepAliveOutputStream(OutputStream out)
  {
    super(out);
  }
  
  public void close()
    throws IOException
  {}
  
  public static PrintStream wrapSystemOut()
  {
    return wrap(System.out);
  }
  
  public static PrintStream wrapSystemErr()
  {
    return wrap(System.err);
  }
  
  private static PrintStream wrap(PrintStream ps)
  {
    return new PrintStream(new KeepAliveOutputStream(ps));
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.KeepAliveOutputStream
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */