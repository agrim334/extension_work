package org.apache.tools.ant.filters;

import org.apache.tools.ant.util.Native2AsciiUtils;

public class Native2AsciiFilter
  extends TokenFilter.ChainableReaderFilter
{
  private boolean reverse;
  
  public void setReverse(boolean reverse)
  {
    this.reverse = reverse;
  }
  
  public String filter(String line)
  {
    return reverse ? 
      Native2AsciiUtils.ascii2native(line) : 
      Native2AsciiUtils.native2ascii(line);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.Native2AsciiFilter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */