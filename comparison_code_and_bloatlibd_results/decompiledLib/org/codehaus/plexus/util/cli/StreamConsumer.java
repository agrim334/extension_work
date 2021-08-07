package org.codehaus.plexus.util.cli;

import java.io.IOException;

public abstract interface StreamConsumer
{
  public abstract void consumeLine(String paramString)
    throws IOException;
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.cli.StreamConsumer
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */