package org.apache.tools.ant.taskdefs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.bzip2.CBZip2OutputStream;

public class BZip2
  extends Pack
{
  protected void pack()
  {
    CBZip2OutputStream zOut = null;
    try
    {
      BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(zipFile.toPath(), new OpenOption[0]));
      bos.write(66);
      bos.write(90);
      zOut = new CBZip2OutputStream(bos);
      zipResource(getSrcResource(), zOut);
    }
    catch (IOException ioe)
    {
      String msg = "Problem creating bzip2 " + ioe.getMessage();
      throw new BuildException(msg, ioe, getLocation());
    }
    finally
    {
      FileUtils.close(zOut);
    }
  }
  
  protected boolean supportsNonFileResources()
  {
    return getClass().equals(BZip2.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.BZip2
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */