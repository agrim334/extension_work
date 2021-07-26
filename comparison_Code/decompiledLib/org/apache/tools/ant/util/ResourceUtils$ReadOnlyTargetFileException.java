package org.apache.tools.ant.util;

import java.io.File;
import java.io.IOException;

public class ResourceUtils$ReadOnlyTargetFileException
  extends IOException
{
  private static final long serialVersionUID = 1L;
  
  public ResourceUtils$ReadOnlyTargetFileException(File destFile)
  {
    super("can't write to read-only destination file " + destFile);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.ResourceUtils.ReadOnlyTargetFileException
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */