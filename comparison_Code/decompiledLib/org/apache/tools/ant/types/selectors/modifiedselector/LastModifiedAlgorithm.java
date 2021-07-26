package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.File;

public class LastModifiedAlgorithm
  implements Algorithm
{
  public boolean isValid()
  {
    return true;
  }
  
  public String getValue(File file)
  {
    long lastModified = file.lastModified();
    if (lastModified == 0L) {
      return null;
    }
    return Long.toString(lastModified);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.modifiedselector.LastModifiedAlgorithm
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */