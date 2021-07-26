package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.types.Parameter;

public final class TabsToSpaces
  extends BaseParamFilterReader
  implements ChainableReader
{
  private static final int DEFAULT_TAB_LENGTH = 8;
  private static final String TAB_LENGTH_KEY = "tablength";
  private int tabLength = 8;
  private int spacesRemaining = 0;
  
  public TabsToSpaces() {}
  
  public TabsToSpaces(Reader in)
  {
    super(in);
  }
  
  public int read()
    throws IOException
  {
    if (!getInitialized())
    {
      initialize();
      setInitialized(true);
    }
    int ch = -1;
    if (spacesRemaining > 0)
    {
      spacesRemaining -= 1;
      ch = 32;
    }
    else
    {
      ch = in.read();
      if (ch == 9)
      {
        spacesRemaining = (tabLength - 1);
        ch = 32;
      }
    }
    return ch;
  }
  
  public void setTablength(int tabLength)
  {
    this.tabLength = tabLength;
  }
  
  private int getTablength()
  {
    return tabLength;
  }
  
  public Reader chain(Reader rdr)
  {
    TabsToSpaces newFilter = new TabsToSpaces(rdr);
    newFilter.setTablength(getTablength());
    newFilter.setInitialized(true);
    return newFilter;
  }
  
  private void initialize()
  {
    Parameter[] params = getParameters();
    if (params != null) {
      for (Parameter param : params) {
        if ((param != null) && 
          ("tablength".equals(param.getName())))
        {
          tabLength = Integer.parseInt(param.getValue());
          break;
        }
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.TabsToSpaces
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */