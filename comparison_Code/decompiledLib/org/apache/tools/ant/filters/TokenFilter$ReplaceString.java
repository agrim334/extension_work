package org.apache.tools.ant.filters;

import org.apache.tools.ant.BuildException;

public class TokenFilter$ReplaceString
  extends TokenFilter.ChainableReaderFilter
{
  private String from;
  private String to;
  
  public void setFrom(String from)
  {
    this.from = from;
  }
  
  public void setTo(String to)
  {
    this.to = to;
  }
  
  public String filter(String line)
  {
    if (from == null) {
      throw new BuildException("Missing from in stringreplace");
    }
    StringBuffer ret = new StringBuffer();
    int start = 0;
    int found = line.indexOf(from);
    while (found >= 0)
    {
      if (found > start) {
        ret.append(line, start, found);
      }
      if (to != null) {
        ret.append(to);
      }
      start = found + from.length();
      found = line.indexOf(from, start);
    }
    if (line.length() > start) {
      ret.append(line, start, line.length());
    }
    return ret.toString();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.TokenFilter.ReplaceString
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */