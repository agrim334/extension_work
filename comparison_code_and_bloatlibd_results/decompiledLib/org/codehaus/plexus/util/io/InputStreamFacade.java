package org.codehaus.plexus.util.io;

import java.io.IOException;
import java.io.InputStream;

public abstract interface InputStreamFacade
{
  public abstract InputStream getInputStream()
    throws IOException;
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.io.InputStreamFacade
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */