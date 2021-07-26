package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;

public final class StripJavaComments
  extends BaseFilterReader
  implements ChainableReader
{
  private int readAheadCh = -1;
  private boolean inString = false;
  private boolean quoted = false;
  
  public StripJavaComments() {}
  
  public StripJavaComments(Reader in)
  {
    super(in);
  }
  
  public int read()
    throws IOException
  {
    int ch = -1;
    if (readAheadCh != -1)
    {
      ch = readAheadCh;
      readAheadCh = -1;
    }
    else
    {
      ch = in.read();
      if ((ch == 34) && (!quoted))
      {
        inString = (!inString);
        quoted = false;
      }
      else if (ch == 92)
      {
        quoted = (!quoted);
      }
      else
      {
        quoted = false;
        if ((!inString) && 
          (ch == 47))
        {
          ch = in.read();
          if (ch == 47) {
            while ((ch != 10) && (ch != -1) && (ch != 13)) {
              ch = in.read();
            }
          }
          if (ch == 42)
          {
            do
            {
              do
              {
                if (ch == -1) {
                  break;
                }
                ch = in.read();
              } while (ch != 42);
              ch = in.read();
              while (ch == 42) {
                ch = in.read();
              }
            } while (ch != 47);
            ch = read();
          }
          else
          {
            readAheadCh = ch;
            ch = 47;
          }
        }
      }
    }
    return ch;
  }
  
  public Reader chain(Reader rdr)
  {
    return new StripJavaComments(rdr);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.StripJavaComments
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */