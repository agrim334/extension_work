package org.apache.tools.ant.filters;

import java.io.StringReader;
import org.apache.tools.ant.util.ReaderInputStream;

public class StringInputStream
  extends ReaderInputStream
{
  public StringInputStream(String source)
  {
    super(new StringReader(source));
  }
  
  public StringInputStream(String source, String encoding)
  {
    super(new StringReader(source), encoding);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.StringInputStream
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */