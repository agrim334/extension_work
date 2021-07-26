package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.ProjectComponent;

public class LineTokenizer
  extends ProjectComponent
  implements Tokenizer
{
  private static final int NOT_A_CHAR = -2;
  private String lineEnd = "";
  private int pushed = -2;
  private boolean includeDelims = false;
  
  public void setIncludeDelims(boolean includeDelims)
  {
    this.includeDelims = includeDelims;
  }
  
  public String getToken(Reader in)
    throws IOException
  {
    int ch;
    int ch;
    if (pushed == -2)
    {
      ch = in.read();
    }
    else
    {
      ch = pushed;
      pushed = -2;
    }
    if (ch == -1) {
      return null;
    }
    lineEnd = "";
    StringBuilder line = new StringBuilder();
    
    int state = 0;
    while (ch != -1)
    {
      if (state == 0)
      {
        if (ch == 13)
        {
          state = 1;
        }
        else
        {
          if (ch == 10)
          {
            lineEnd = "\n";
            break;
          }
          line.append((char)ch);
        }
      }
      else
      {
        state = 0;
        if (ch == 10)
        {
          lineEnd = "\r\n"; break;
        }
        pushed = ch;
        lineEnd = "\r";
        
        break;
      }
      ch = in.read();
    }
    if ((ch == -1) && (state == 1)) {
      lineEnd = "\r";
    }
    if (includeDelims) {
      line.append(lineEnd);
    }
    return line.toString();
  }
  
  public String getPostToken()
  {
    return includeDelims ? "" : lineEnd;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.LineTokenizer
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */