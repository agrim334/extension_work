package org.apache.tools.ant.filters;

import java.io.Reader;
import org.apache.tools.ant.ProjectComponent;

public abstract class TokenFilter$ChainableReaderFilter
  extends ProjectComponent
  implements ChainableReader, TokenFilter.Filter
{
  private boolean byLine = true;
  
  public void setByLine(boolean byLine)
  {
    this.byLine = byLine;
  }
  
  public Reader chain(Reader reader)
  {
    TokenFilter tokenFilter = new TokenFilter(reader);
    if (!byLine) {
      tokenFilter.add(new TokenFilter.FileTokenizer());
    }
    tokenFilter.add(this);
    return tokenFilter;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.TokenFilter.ChainableReaderFilter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */