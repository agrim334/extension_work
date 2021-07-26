package org.apache.tools.ant.taskdefs;

import java.io.Reader;
import org.apache.tools.ant.filters.ChainableReader;

class VerifyJar$BufferingOutputFilter
  implements ChainableReader
{
  private VerifyJar.BufferingOutputFilterReader buffer;
  
  public Reader chain(Reader rdr)
  {
    buffer = new VerifyJar.BufferingOutputFilterReader(rdr);
    return buffer;
  }
  
  public String toString()
  {
    return buffer.toString();
  }
  
  public void clear()
  {
    if (buffer != null) {
      buffer.clear();
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.VerifyJar.BufferingOutputFilter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */