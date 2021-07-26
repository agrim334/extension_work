package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.zip.GZIPOutputStream;
import org.apache.tools.ant.BuildException;

public class GZip
  extends Pack
{
  protected void pack()
  {
    try
    {
      GZIPOutputStream zOut = new GZIPOutputStream(Files.newOutputStream(zipFile.toPath(), new OpenOption[0]));
      try
      {
        zipResource(getSrcResource(), zOut);
        zOut.close();
      }
      catch (Throwable localThrowable) {}
      try
      {
        zOut.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      throw localThrowable;
    }
    catch (IOException ioe)
    {
      String msg = "Problem creating gzip " + ioe.getMessage();
      throw new BuildException(msg, ioe, getLocation());
    }
  }
  
  protected boolean supportsNonFileResources()
  {
    return getClass().equals(GZip.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.GZip
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */