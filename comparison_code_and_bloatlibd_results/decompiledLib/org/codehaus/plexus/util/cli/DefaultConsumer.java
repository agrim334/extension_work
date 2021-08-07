package org.codehaus.plexus.util.cli;

import java.io.IOException;
import java.io.PrintStream;

public class DefaultConsumer
  implements StreamConsumer
{
  public void consumeLine(String line)
    throws IOException
  {
    System.out.println(line);
    if (System.out.checkError()) {
      throw new IOException(String.format("Failure printing line '%s' to stdout.", new Object[] { line }));
    }
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.cli.DefaultConsumer
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */