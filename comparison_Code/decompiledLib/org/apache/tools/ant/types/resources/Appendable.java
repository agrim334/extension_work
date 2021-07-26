package org.apache.tools.ant.types.resources;

import java.io.IOException;
import java.io.OutputStream;

public abstract interface Appendable
{
  public abstract OutputStream getAppendOutputStream()
    throws IOException;
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.Appendable
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */