package org.apache.tools.ant.filters;

public class TokenFilter$Trim
  extends TokenFilter.ChainableReaderFilter
{
  public String filter(String line)
  {
    return line.trim();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.TokenFilter.Trim
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */