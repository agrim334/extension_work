package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;

class TokenFilter$DeleteCharacters$1
  extends BaseFilterReader
{
  TokenFilter$DeleteCharacters$1(TokenFilter.DeleteCharacters this$0, Reader in)
  {
    super(in);
  }
  
  public int read()
    throws IOException
  {
    for (;;)
    {
      int c = in.read();
      if (c == -1) {
        return c;
      }
      if (!TokenFilter.DeleteCharacters.access$000(this$0, (char)c)) {
        return c;
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.TokenFilter.DeleteCharacters.1
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */