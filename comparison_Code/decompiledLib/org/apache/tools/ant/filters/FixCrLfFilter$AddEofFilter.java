package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;

class FixCrLfFilter$AddEofFilter
  extends FixCrLfFilter.SimpleFilterReader
{
  private int lastChar = -1;
  
  public FixCrLfFilter$AddEofFilter(Reader in)
  {
    super(in);
  }
  
  public int read()
    throws IOException
  {
    int thisChar = super.read();
    if (thisChar == -1)
    {
      if (lastChar != 26)
      {
        lastChar = 26;
        return lastChar;
      }
    }
    else {
      lastChar = thisChar;
    }
    return thisChar;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.FixCrLfFilter.AddEofFilter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */