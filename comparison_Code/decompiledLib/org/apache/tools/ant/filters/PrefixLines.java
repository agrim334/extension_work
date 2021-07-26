package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.types.Parameter;

public final class PrefixLines
  extends BaseParamFilterReader
  implements ChainableReader
{
  private static final String PREFIX_KEY = "prefix";
  private String prefix = null;
  private String queuedData = null;
  
  public PrefixLines() {}
  
  public PrefixLines(Reader in)
  {
    super(in);
  }
  
  public int read()
    throws IOException
  {
    if (!getInitialized())
    {
      initialize();
      setInitialized(true);
    }
    int ch = -1;
    if ((queuedData != null) && (queuedData.isEmpty())) {
      queuedData = null;
    }
    if (queuedData != null)
    {
      ch = queuedData.charAt(0);
      queuedData = queuedData.substring(1);
      if (queuedData.isEmpty()) {
        queuedData = null;
      }
    }
    else
    {
      queuedData = readLine();
      if (queuedData == null)
      {
        ch = -1;
      }
      else
      {
        if (prefix != null) {
          queuedData = (prefix + queuedData);
        }
        return read();
      }
    }
    return ch;
  }
  
  public void setPrefix(String prefix)
  {
    this.prefix = prefix;
  }
  
  private String getPrefix()
  {
    return prefix;
  }
  
  public Reader chain(Reader rdr)
  {
    PrefixLines newFilter = new PrefixLines(rdr);
    newFilter.setPrefix(getPrefix());
    newFilter.setInitialized(true);
    return newFilter;
  }
  
  private void initialize()
  {
    Parameter[] params = getParameters();
    if (params != null) {
      for (Parameter param : params) {
        if ("prefix".equals(param.getName()))
        {
          prefix = param.getValue();
          break;
        }
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.PrefixLines
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */