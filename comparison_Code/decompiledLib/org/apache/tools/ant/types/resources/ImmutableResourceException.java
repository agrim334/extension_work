package org.apache.tools.ant.types.resources;

import java.io.IOException;

public class ImmutableResourceException
  extends IOException
{
  private static final long serialVersionUID = 1L;
  
  public ImmutableResourceException() {}
  
  public ImmutableResourceException(String s)
  {
    super(s);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.ImmutableResourceException
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */