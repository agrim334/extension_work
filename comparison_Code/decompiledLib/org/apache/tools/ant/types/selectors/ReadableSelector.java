package org.apache.tools.ant.types.selectors;

import java.io.File;

public class ReadableSelector
  implements FileSelector
{
  public boolean isSelected(File basedir, String filename, File file)
  {
    return (file != null) && (file.canRead());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.ReadableSelector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */