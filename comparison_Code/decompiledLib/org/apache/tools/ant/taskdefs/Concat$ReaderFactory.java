package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.Reader;

abstract interface Concat$ReaderFactory<S>
{
  public abstract Reader getReader(S paramS)
    throws IOException;
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Concat.ReaderFactory
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */