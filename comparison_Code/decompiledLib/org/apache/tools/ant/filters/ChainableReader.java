package org.apache.tools.ant.filters;

import java.io.Reader;

public abstract interface ChainableReader
{
  public abstract Reader chain(Reader paramReader);
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.ChainableReader
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */