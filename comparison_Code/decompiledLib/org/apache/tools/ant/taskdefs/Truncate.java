package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileUtils;

public class Truncate
  extends Task
{
  private static final int BUFFER_SIZE = 1024;
  private static final Long ZERO = Long.valueOf(0L);
  private static final String NO_CHILD = "No files specified.";
  private static final String INVALID_LENGTH = "Cannot truncate to length ";
  private static final String READ_WRITE = "rw";
  private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
  private static final byte[] FILL_BUFFER = new byte['Ѐ'];
  private Path path;
  private boolean create = true;
  private boolean mkdirs = false;
  private Long length;
  private Long adjust;
  
  public void setFile(File f)
  {
    add(new FileResource(f));
  }
  
  public void add(ResourceCollection rc)
  {
    getPath().add(rc);
  }
  
  public void setAdjust(Long adjust)
  {
    this.adjust = adjust;
  }
  
  public void setLength(Long length)
  {
    this.length = length;
    if ((length != null) && (length.longValue() < 0L)) {
      throw new BuildException("Cannot truncate to length " + length);
    }
  }
  
  public void setCreate(boolean create)
  {
    this.create = create;
  }
  
  public void setMkdirs(boolean mkdirs)
  {
    this.mkdirs = mkdirs;
  }
  
  public void execute()
  {
    if ((length != null) && (adjust != null)) {
      throw new BuildException("length and adjust are mutually exclusive options");
    }
    if ((length == null) && (adjust == null)) {
      length = ZERO;
    }
    if (path == null) {
      throw new BuildException("No files specified.");
    }
    for (Resource r : path)
    {
      File f = ((FileProvider)r.as(FileProvider.class)).getFile();
      if (shouldProcess(f)) {
        process(f);
      }
    }
  }
  
  private boolean shouldProcess(File f)
  {
    if (f.isFile()) {
      return true;
    }
    if (!create) {
      return false;
    }
    Exception exception = null;
    try
    {
      if (FILE_UTILS.createNewFile(f, mkdirs)) {
        return true;
      }
    }
    catch (IOException e)
    {
      exception = e;
    }
    String msg = "Unable to create " + f;
    if (exception == null)
    {
      log(msg, 1);
      return false;
    }
    throw new BuildException(msg, exception);
  }
  
  private void process(File f)
  {
    long len = f.length();
    
    long newLength = length == null ? len + adjust.longValue() : length.longValue();
    if (len == newLength) {
      return;
    }
    RandomAccessFile raf = null;
    try
    {
      raf = new RandomAccessFile(f, "rw");
    }
    catch (Exception e)
    {
      throw new BuildException("Could not open " + f + " for writing", e);
    }
    try
    {
      if (newLength > len)
      {
        long pos = len;
        raf.seek(pos);
        while (pos < newLength)
        {
          long writeCount = Math.min(FILL_BUFFER.length, newLength - pos);
          
          raf.write(FILL_BUFFER, 0, (int)writeCount);
          pos += writeCount;
        }
      }
      else
      {
        raf.setLength(newLength);
      }
      return;
    }
    catch (IOException e)
    {
      throw new BuildException("Exception working with " + raf, e);
    }
    finally
    {
      try
      {
        raf.close();
      }
      catch (IOException e)
      {
        log("Caught " + e + " closing " + raf, 1);
      }
    }
  }
  
  private synchronized Path getPath()
  {
    if (path == null) {
      path = new Path(getProject());
    }
    return path;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Truncate
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */