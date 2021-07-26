package org.apache.tools.ant.filters;

public class TokenFilter$IgnoreBlank
  extends TokenFilter.ChainableReaderFilter
{
  public String filter(String line)
  {
    if (line.trim().isEmpty()) {
      return null;
    }
    return line;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.TokenFilter.IgnoreBlank
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */