package org.apache.tools.ant.filters;

public class UniqFilter
  extends TokenFilter.ChainableReaderFilter
{
  private String lastLine = null;
  
  public String filter(String string)
  {
    if ((lastLine == null) || (!lastLine.equals(string)))
    {
      lastLine = string;
      return lastLine;
    }
    return null;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.UniqFilter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */