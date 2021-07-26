package org.apache.tools.tar;

import java.io.IOException;

public class TarArchiveSparseEntry
  implements TarConstants
{
  private boolean isExtended;
  
  public TarArchiveSparseEntry(byte[] headerBuf)
    throws IOException
  {
    int offset = 0;
    offset += 504;
    isExtended = TarUtils.parseBoolean(headerBuf, offset);
  }
  
  public boolean isExtended()
  {
    return isExtended;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.tar.TarArchiveSparseEntry
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */