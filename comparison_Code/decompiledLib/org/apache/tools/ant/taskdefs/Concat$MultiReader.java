package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;

final class Concat$MultiReader<S>
  extends Reader
{
  private Reader reader = null;
  private int lastPos = 0;
  private char[] lastChars = new char[Concat.access$000(this$0).length()];
  private boolean needAddSeparator = false;
  private Iterator<S> readerSources;
  private Concat.ReaderFactory<S> factory;
  
  private Concat$MultiReader(Iterator<S> arg1, Concat.ReaderFactory<S> readerSources)
  {
    this.readerSources = readerSources;
    this.factory = factory;
  }
  
  private Reader getReader()
    throws IOException
  {
    if ((reader == null) && (readerSources.hasNext()))
    {
      reader = factory.getReader(readerSources.next());
      Arrays.fill(lastChars, '\000');
    }
    return reader;
  }
  
  private void nextReader()
    throws IOException
  {
    close();
    reader = null;
  }
  
  public int read()
    throws IOException
  {
    if (needAddSeparator) {
      if (lastPos >= Concat.access$000(this$0).length())
      {
        lastPos = 0;
        needAddSeparator = false;
      }
      else
      {
        return Concat.access$000(this$0).charAt(lastPos++);
      }
    }
    while (getReader() != null)
    {
      int ch = getReader().read();
      if (ch == -1)
      {
        nextReader();
        if ((isFixLastLine()) && (isMissingEndOfLine()))
        {
          needAddSeparator = true;
          lastPos = 1;
          return Concat.access$000(this$0).charAt(0);
        }
      }
      else
      {
        addLastChar((char)ch);
        return ch;
      }
    }
    return -1;
  }
  
  public int read(char[] cbuf, int off, int len)
    throws IOException
  {
    int amountRead = 0;
    while ((getReader() != null) || (needAddSeparator)) {
      if (needAddSeparator)
      {
        cbuf[off] = Concat.access$000(this$0).charAt(lastPos++);
        if (lastPos >= Concat.access$000(this$0).length())
        {
          lastPos = 0;
          needAddSeparator = false;
        }
        len--;
        off++;
        amountRead++;
        if (len == 0) {
          return amountRead;
        }
      }
      else
      {
        int nRead = getReader().read(cbuf, off, len);
        if ((nRead == -1) || (nRead == 0))
        {
          nextReader();
          if ((isFixLastLine()) && (isMissingEndOfLine()))
          {
            needAddSeparator = true;
            lastPos = 0;
          }
        }
        else
        {
          if (isFixLastLine()) {
            for (int i = nRead; i > nRead - lastChars.length; i--)
            {
              if (i <= 0) {
                break;
              }
              addLastChar(cbuf[(off + i - 1)]);
            }
          }
          len -= nRead;
          off += nRead;
          amountRead += nRead;
          if (len == 0) {
            return amountRead;
          }
        }
      }
    }
    if (amountRead == 0) {
      return -1;
    }
    return amountRead;
  }
  
  public void close()
    throws IOException
  {
    if (reader != null) {
      reader.close();
    }
  }
  
  private void addLastChar(char ch)
  {
    System.arraycopy(lastChars, 1, lastChars, 0, lastChars.length - 2 + 1);
    lastChars[(lastChars.length - 1)] = ch;
  }
  
  private boolean isMissingEndOfLine()
  {
    for (int i = 0; i < lastChars.length; i++) {
      if (lastChars[i] != Concat.access$000(this$0).charAt(i)) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isFixLastLine()
  {
    return (Concat.access$100(this$0)) && (Concat.access$200(this$0) == null);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Concat.MultiReader
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */