package org.apache.tools.ant.taskdefs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.bzip2.CBZip2InputStream;

public class BUnzip2
  extends Unpack
{
  private static final int BUFFER_SIZE = 8192;
  private static final String DEFAULT_EXTENSION = ".bz2";
  
  protected String getDefaultExtension()
  {
    return ".bz2";
  }
  
  protected void extract()
  {
    if (srcResource.getLastModified() > dest.lastModified())
    {
      log("Expanding " + srcResource.getName() + " to " + dest
        .getAbsolutePath());
      
      OutputStream out = null;
      CBZip2InputStream zIn = null;
      InputStream fis = null;
      BufferedInputStream bis = null;
      try
      {
        out = Files.newOutputStream(dest.toPath(), new OpenOption[0]);
        fis = srcResource.getInputStream();
        bis = new BufferedInputStream(fis);
        int b = bis.read();
        if (b != 66) {
          throw new BuildException("Invalid bz2 file.", getLocation());
        }
        b = bis.read();
        if (b != 90) {
          throw new BuildException("Invalid bz2 file.", getLocation());
        }
        zIn = new CBZip2InputStream(bis, true);
        byte[] buffer = new byte[' '];
        int count = 0;
        do
        {
          out.write(buffer, 0, count);
          count = zIn.read(buffer, 0, buffer.length);
        } while (count != -1);
      }
      catch (IOException ioe)
      {
        String msg = "Problem expanding bzip2 " + ioe.getMessage();
        throw new BuildException(msg, ioe, getLocation());
      }
      finally
      {
        FileUtils.close(bis);
        FileUtils.close(fis);
        FileUtils.close(out);
        FileUtils.close(zIn);
      }
    }
  }
  
  protected boolean supportsNonFileResources()
  {
    return getClass().equals(BUnzip2.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.BUnzip2
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */