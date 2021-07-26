package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.types.Parameter;

public final class StripLineBreaks
  extends BaseParamFilterReader
  implements ChainableReader
{
  private static final String DEFAULT_LINE_BREAKS = "\r\n";
  private static final String LINE_BREAKS_KEY = "linebreaks";
  private String lineBreaks = "\r\n";
  
  public StripLineBreaks() {}
  
  public StripLineBreaks(Reader in)
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
    int ch = in.read();
    while ((ch != -1) && 
      (lineBreaks.indexOf(ch) != -1)) {
      ch = in.read();
    }
    return ch;
  }
  
  public void setLineBreaks(String lineBreaks)
  {
    this.lineBreaks = lineBreaks;
  }
  
  private String getLineBreaks()
  {
    return lineBreaks;
  }
  
  public Reader chain(Reader rdr)
  {
    StripLineBreaks newFilter = new StripLineBreaks(rdr);
    newFilter.setLineBreaks(getLineBreaks());
    newFilter.setInitialized(true);
    return newFilter;
  }
  
  private void initialize()
  {
    String userDefinedLineBreaks = null;
    Parameter[] params = getParameters();
    if (params != null) {
      for (Parameter param : params) {
        if ("linebreaks".equals(param.getName()))
        {
          userDefinedLineBreaks = param.getValue();
          break;
        }
      }
    }
    if (userDefinedLineBreaks != null) {
      lineBreaks = userDefinedLineBreaks;
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.StripLineBreaks
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */