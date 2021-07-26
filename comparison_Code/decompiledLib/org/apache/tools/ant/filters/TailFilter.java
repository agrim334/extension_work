package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.util.LineTokenizer;

public final class TailFilter
  extends BaseParamFilterReader
  implements ChainableReader
{
  private static final String LINES_KEY = "lines";
  private static final String SKIP_KEY = "skip";
  private static final int DEFAULT_NUM_LINES = 10;
  private long lines = 10L;
  private long skip = 0L;
  private boolean completedReadAhead = false;
  private LineTokenizer lineTokenizer = null;
  private String line = null;
  private int linePos = 0;
  private LinkedList<String> lineList = new LinkedList();
  
  public TailFilter() {}
  
  public TailFilter(Reader in)
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
      line = tailFilter(line);
      if (line == null) {
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
    TailFilter newFilter = new TailFilter(rdr);
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
          setLines(Long.parseLong(param.getValue()));
        } else if ("skip".equals(paramName)) {
          skip = Long.parseLong(param.getValue());
        }
      }
    }
  }
  
  private String tailFilter(String line)
  {
    if (!completedReadAhead)
    {
      if (line != null)
      {
        lineList.add(line);
        if (lines == -1L)
        {
          if (lineList.size() > skip) {
            return (String)lineList.removeFirst();
          }
        }
        else
        {
          long linesToKeep = lines + (skip > 0L ? skip : 0L);
          if (linesToKeep < lineList.size()) {
            lineList.removeFirst();
          }
        }
        return "";
      }
      completedReadAhead = true;
      if (skip > 0L) {
        for (int i = 0; i < skip; i++) {
          lineList.removeLast();
        }
      }
      if (lines > -1L) {
        while (lineList.size() > lines) {
          lineList.removeFirst();
        }
      }
    }
    if (lineList.size() > 0) {
      return (String)lineList.removeFirst();
    }
    return null;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.TailFilter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */