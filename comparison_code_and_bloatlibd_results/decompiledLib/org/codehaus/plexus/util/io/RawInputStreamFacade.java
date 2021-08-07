package org.codehaus.plexus.util.io;

import java.io.IOException;
import java.io.InputStream;

public class RawInputStreamFacade
  implements InputStreamFacade
{
  final InputStream stream;
  
  public RawInputStreamFacade(InputStream stream)
  {
    this.stream = stream;
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    return stream;
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.io.RawInputStreamFacade
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */