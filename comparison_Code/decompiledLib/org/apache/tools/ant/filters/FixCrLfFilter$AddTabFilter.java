package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;

class FixCrLfFilter$AddTabFilter
  extends FixCrLfFilter.SimpleFilterReader
{
  private int columnNumber = 0;
  private int tabLength = 0;
  
  public FixCrLfFilter$AddTabFilter(Reader in, int tabLength)
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
    case 32: 
      columnNumber += 1;
      if (!editsBlocked())
      {
        int colNextTab = (columnNumber + tabLength - 1) / tabLength * tabLength;
        int countSpaces = 1;
        int numTabs = 0;
        while ((c = super.read()) != -1) {
          switch (c)
          {
          case 32: 
            if (++columnNumber == colNextTab)
            {
              numTabs++;
              countSpaces = 0;
              colNextTab += tabLength;
            }
            else
            {
              countSpaces++;
            }
            break;
          case 9: 
            columnNumber = colNextTab;
            numTabs++;
            countSpaces = 0;
            colNextTab += tabLength;
            break;
          default: 
            push(c);
          }
        }
        while (countSpaces-- > 0)
        {
          push(' ');
          columnNumber -= 1;
        }
        while (numTabs-- > 0)
        {
          push('\t');
          columnNumber -= tabLength;
        }
        c = super.read();
        switch (c)
        {
        case 32: 
          columnNumber += 1;
          break;
        case 9: 
          columnNumber += tabLength;
        }
      }
      break;
    case 9: 
      columnNumber = ((columnNumber + tabLength - 1) / tabLength * tabLength);
      break;
    default: 
      columnNumber += 1;
    }
    return c;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.FixCrLfFilter.AddTabFilter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */