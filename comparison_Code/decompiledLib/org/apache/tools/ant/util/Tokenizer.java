package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.Reader;

public abstract interface Tokenizer
{
  public abstract String getToken(Reader paramReader)
    throws IOException;
  
  public abstract String getPostToken();
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.Tokenizer
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */