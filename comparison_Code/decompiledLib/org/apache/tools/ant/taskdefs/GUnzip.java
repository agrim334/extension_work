package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.zip.GZIPInputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;

public class GUnzip
  extends Unpack
{
  private static final int BUFFER_SIZE = 8192;
  private static final String DEFAULT_EXTENSION = ".gz";
  
  protected String getDefaultExtension()
  {
    return ".gz";
  }
  
  protected void extract()
  {
    if (srcResource.getLastModified() > dest.lastModified())
    {
      log("Expanding " + srcResource.getName() + " to " + dest
        .getAbsolutePath());
      try
      {
        OutputStream out = Files.newOutputStream(dest.toPath(), new OpenOption[0]);
        try
        {
          GZIPInputStream zIn = new GZIPInputStream(srcResource.getInputStream());
          try
          {
            byte[] buffer = new byte[' '];
            int count = 0;
            do
            {
              out.write(buffer, 0, count);
              count = zIn.read(buffer, 0, buffer.length);
            } while (count != -1);
            zIn.close();
          }
          catch (Throwable localThrowable) {}
          try
          {
            zIn.close();
          }
          catch (Throwable localThrowable1)
          {
            localThrowable.addSuppressed(localThrowable1);
          }
          throw localThrowable;
        }
        catch (Throwable localThrowable2)
        {
          if (out == null) {
            break label179;
          }
        }
        if (out != null)
        {
          out.close(); return;
          try
          {
            out.close();
          }
          catch (Throwable localThrowable3)
          {
            localThrowable2.addSuppressed(localThrowable3);
          }
          label179:
          throw localThrowable2;
        }
      }
      catch (IOException ioe)
      {
        String msg = "Problem expanding gzip " + ioe.getMessage();
        throw new BuildException(msg, ioe, getLocation());
      }
    }
  }
  
  protected boolean supportsNonFileResources()
  {
    return getClass().equals(GUnzip.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.GUnzip
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */