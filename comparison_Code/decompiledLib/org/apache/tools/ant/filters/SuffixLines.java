package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.types.Parameter;

public final class SuffixLines
  extends BaseParamFilterReader
  implements ChainableReader
{
  private static final String SUFFIX_KEY = "suffix";
  private String suffix = null;
  private String queuedData = null;
  
  public SuffixLines() {}
  
  public SuffixLines(Reader in)
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
    if (queuedData == null)
    {
      queuedData = readLine();
      if (queuedData == null)
      {
        ch = -1;
      }
      else
      {
        if (suffix != null)
        {
          String lf = "";
          if (queuedData.endsWith("\r\n")) {
            lf = "\r\n";
          } else if (queuedData.endsWith("\n")) {
            lf = "\n";
          }
          queuedData = (queuedData.substring(0, queuedData
            .length() - lf.length()) + suffix + lf);
        }
        return read();
      }
    }
    else
    {
      ch = queuedData.charAt(0);
      queuedData = queuedData.substring(1);
      if (queuedData.isEmpty()) {
        queuedData = null;
      }
    }
    return ch;
  }
  
  public void setSuffix(String suffix)
  {
    this.suffix = suffix;
  }
  
  private String getSuffix()
  {
    return suffix;
  }
  
  public Reader chain(Reader rdr)
  {
    SuffixLines newFilter = new SuffixLines(rdr);
    newFilter.setSuffix(getSuffix());
    newFilter.setInitialized(true);
    return newFilter;
  }
  
  private void initialize()
  {
    Parameter[] params = getParameters();
    if (params != null) {
      for (Parameter param : params) {
        if ("suffix".equals(param.getName()))
        {
          suffix = param.getValue();
          break;
        }
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.SuffixLines
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */