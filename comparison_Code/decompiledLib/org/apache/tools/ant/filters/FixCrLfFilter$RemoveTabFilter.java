package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;

class FixCrLfFilter$RemoveTabFilter
  extends FixCrLfFilter.SimpleFilterReader
{
  private int columnNumber = 0;
  private int tabLength = 0;
  
  public FixCrLfFilter$RemoveTabFilter(Reader in, int tabLength)
  {
    super(in);
    
    this.tabLength = tabLength;
  }
  
  public int read()
    throws IOException
  {
    int c = super.read();
    switch (c)
    {
    case 10: 
    case 13: 
      columnNumber = 0;
      break;
    case 9: 
      int width = tabLength - columnNumber % tabLength;
      if (!editsBlocked())
      {
        for (; width > 1; width--) {
          push(' ');
        }
        c = 32;
      }
      columnNumber += width;
      break;
    case 11: 
    case 12: 
    default: 
      columnNumber += 1;
    }
    return c;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.FixCrLfFilter.RemoveTabFilter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */