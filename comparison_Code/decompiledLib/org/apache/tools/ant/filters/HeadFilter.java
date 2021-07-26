package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.util.LineTokenizer;

public final class HeadFilter
  extends BaseParamFilterReader
  implements ChainableReader
{
  private static final String LINES_KEY = "lines";
  private static final String SKIP_KEY = "skip";
  private long linesRead = 0L;
  private static final int DEFAULT_NUM_LINES = 10;
  private long lines = 10L;
  private long skip = 0L;
  private LineTokenizer lineTokenizer = null;
  private String line = null;
  private int linePos = 0;
  private boolean eof;
  
  public HeadFilter() {}
  
  public HeadFilter(Reader in)
  {
    super(in);
    lineTokenizer = new LineTokenizer();
    lineTokenizer.setIncludeDelims(true);
  }
  
  public int read()
    throws IOException
  {
    if (!getInitialized())
    {
      initialize();
      setInitialized(true);
    }
    while ((line == null) || (line.isEmpty()))
    {
      line = lineTokenizer.getToken(in);
      if (line == null) {
        return -1;
      }
      line = headFilter(line);
      if (eof) {
        return -1;
      }
      linePos = 0;
    }
    int ch = line.charAt(linePos);
    linePos += 1;
    if (linePos == line.length()) {
      line = null;
    }
    return ch;
  }
  
  public void setLines(long lines)
  {
    this.lines = lines;
  }
  
  private long getLines()
  {
    return lines;
  }
  
  public void setSkip(long skip)
  {
    this.skip = skip;
  }
  
  private long getSkip()
  {
    return skip;
  }
  
  public Reader chain(Reader rdr)
  {
    HeadFilter newFilter = new HeadFilter(rdr);
    newFilter.setLines(getLines());
    newFilter.setSkip(getSkip());
    newFilter.setInitialized(true);
    return newFilter;
  }
  
  private void initialize()
  {
    Parameter[] params = getParameters();
    if (params != null) {
      for (Parameter param : params)
      {
        String paramName = param.getName();
        if ("lines".equals(paramName)) {
          lines = Long.parseLong(param.getValue());
        } else if ("skip".equals(paramName)) {
          skip = Long.parseLong(param.getValue());
        }
      }
    }
  }
  
  private String headFilter(String line)
  {
    linesRead += 1L;
    if ((skip > 0L) && 
      (linesRead - 1L < skip)) {
      return null;
    }
    if ((lines > 0L) && 
      (linesRead > lines + skip))
    {
      eof = true;
      return null;
    }
    return line;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.HeadFilter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */