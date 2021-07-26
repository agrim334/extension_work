package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.ProjectComponent;

public class TokenFilter$DeleteCharacters
  extends ProjectComponent
  implements TokenFilter.Filter, ChainableReader
{
  private String deleteChars = "";
  
  public void setChars(String deleteChars)
  {
    this.deleteChars = TokenFilter.resolveBackSlash(deleteChars);
  }
  
  public String filter(String string)
  {
    StringBuffer output = new StringBuffer(string.length());
    for (int i = 0; i < string.length(); i++)
    {
      char ch = string.charAt(i);
      if (!isDeleteCharacter(ch)) {
        output.append(ch);
      }
    }
    return output.toString();
  }
  
  public Reader chain(Reader reader)
  {
    new BaseFilterReader(reader)
    {
      public int read()
        throws IOException
      {
        for (;;)
        {
          int c = in.read();
          if (c == -1) {
            return c;
          }
          if (!TokenFilter.DeleteCharacters.this.isDeleteCharacter((char)c)) {
            return c;
          }
        }
      }
    };
  }
  
  private boolean isDeleteCharacter(char c)
  {
    for (int d = 0; d < deleteChars.length(); d++) {
      if (deleteChars.charAt(d) == c) {
        return true;
      }
    }
    return false;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.TokenFilter.DeleteCharacters
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */