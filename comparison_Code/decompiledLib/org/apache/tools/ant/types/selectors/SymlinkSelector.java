package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.nio.file.Files;

public class SymlinkSelector
  implements FileSelector
{
  public boolean isSelected(File basedir, String filename, File file)
  {
    return (file != null) && (Files.isSymbolicLink(file.toPath()));
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.SymlinkSelector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */