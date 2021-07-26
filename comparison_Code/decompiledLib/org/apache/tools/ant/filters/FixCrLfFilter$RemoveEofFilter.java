package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;

class FixCrLfFilter$RemoveEofFilter
  extends FixCrLfFilter.SimpleFilterReader
{
  private int lookAhead = -1;
  
  public FixCrLfFilter$RemoveEofFilter(Reader in)
  {
    super(in);
    try
    {
      lookAhead = in.read();
    }
    catch (IOException e)
    {
      lookAhead = -1;
    }
  }
  
  public int read()
    throws IOException
  {
    int lookAhead2 = super.read();
    if ((lookAhead2 == -1) && (lookAhead == 26)) {
      return -1;
    }
    int i = lookAhead;
    lookAhead = lookAhead2;
    return i;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.FixCrLfFilter.RemoveEofFilter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */