package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.util.UnicodeUtil;

public class EscapeUnicode
  extends BaseParamFilterReader
  implements ChainableReader
{
  private StringBuffer unicodeBuf;
  
  public EscapeUnicode()
  {
    unicodeBuf = new StringBuffer();
  }
  
  public EscapeUnicode(Reader in)
  {
    super(in);
    unicodeBuf = new StringBuffer();
  }
  
  public final int read()
    throws IOException
  {
    if (!getInitialized())
    {
      initialize();
      setInitialized(true);
    }
    int ch = -1;
    if (unicodeBuf.length() > 0)
    {
      ch = unicodeBuf.charAt(0);
      unicodeBuf.deleteCharAt(0);
    }
    else
    {
      ch = in.read();
      if (ch != -1)
      {
        char achar = (char)ch;
        if (achar >= '')
        {
          unicodeBuf = UnicodeUtil.EscapeUnicode(achar);
          ch = 92;
        }
      }
    }
    return ch;
  }
  
  public final Reader chain(Reader rdr)
  {
    EscapeUnicode newFilter = new EscapeUnicode(rdr);
    newFilter.setInitialized(true);
    return newFilter;
  }
  
  private void initialize() {}
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.EscapeUnicode
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */