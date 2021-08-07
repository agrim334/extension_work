package org.codehaus.plexus.util.cli;

import java.io.PrintWriter;
import java.io.Writer;

public class WriterStreamConsumer
  implements StreamConsumer
{
  private PrintWriter writer;
  
  public WriterStreamConsumer(Writer writer)
  {
    this.writer = new PrintWriter(writer);
  }
  
  public void consumeLine(String line)
  {
    writer.println(line);
    
    writer.flush();
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.cli.WriterStreamConsumer
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */