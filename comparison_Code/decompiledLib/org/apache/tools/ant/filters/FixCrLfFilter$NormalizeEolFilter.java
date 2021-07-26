package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;

class FixCrLfFilter$NormalizeEolFilter
  extends FixCrLfFilter.SimpleFilterReader
{
  private boolean previousWasEOL;
  private boolean fixLast;
  private int normalizedEOL = 0;
  private char[] eol = null;
  
  public FixCrLfFilter$NormalizeEolFilter(Reader in, String eolString, boolean fixLast)
  {
    super(in);
    eol = eolString.toCharArray();
    this.fixLast = fixLast;
  }
  
  public int read()
    throws IOException
  {
    int thisChar = super.read();
    if (normalizedEOL == 0)
    {
      int numEOL = 0;
      boolean atEnd = false;
      switch (thisChar)
      {
      case 26: 
        int c = super.read();
        if (c == -1)
        {
          atEnd = true;
          if ((fixLast) && (!previousWasEOL))
          {
            numEOL = 1;
            push(thisChar);
          }
        }
        else
        {
          push(c);
        }
        break;
      case -1: 
        atEnd = true;
        if ((fixLast) && (!previousWasEOL)) {
          numEOL = 1;
        }
        break;
      case 10: 
        numEOL = 1;
        break;
      case 13: 
        numEOL = 1;
        int c1 = super.read();
        int c2 = super.read();
        if ((c1 != 13) || (c2 != 10)) {
          if (c1 == 13)
          {
            numEOL = 2;
            push(c2);
          }
          else if (c1 == 10)
          {
            push(c2);
          }
          else
          {
            push(c2);
            push(c1);
          }
        }
        break;
      }
      if (numEOL > 0)
      {
        while (numEOL-- > 0)
        {
          push(eol);
          normalizedEOL += eol.length;
        }
        previousWasEOL = true;
        thisChar = read();
      }
      else if (!atEnd)
      {
        previousWasEOL = false;
      }
    }
    else
    {
      normalizedEOL -= 1;
    }
    return thisChar;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.FixCrLfFilter.NormalizeEolFilter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */