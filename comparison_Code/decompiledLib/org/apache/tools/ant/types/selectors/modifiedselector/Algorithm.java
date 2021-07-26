package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.File;

public abstract interface Algorithm
{
  public abstract boolean isValid();
  
  public abstract String getValue(File paramFile);
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.modifiedselector.Algorithm
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */