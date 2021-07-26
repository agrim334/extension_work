package org.apache.tools.ant.util;

import java.io.IOException;

public abstract interface Retryable
{
  public static final int RETRY_FOREVER = -1;
  
  public abstract void execute()
    throws IOException;
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.Retryable
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */