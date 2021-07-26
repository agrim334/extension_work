package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.types.resources.FileResource;

public class LoadFile
  extends LoadResource
{
  public final void setSrcFile(File srcFile)
  {
    addConfigured(new FileResource(srcFile));
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.LoadFile
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */