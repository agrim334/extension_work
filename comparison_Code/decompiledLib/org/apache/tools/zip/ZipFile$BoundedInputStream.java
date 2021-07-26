package org.apache.tools.zip;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

class ZipFile$BoundedInputStream
  extends InputStream
{
  private long remaining;
  private long loc;
  private boolean addDummyByte = false;
  
  ZipFile$BoundedInputStream(ZipFile paramZipFile, long start, long remaining)
  {
    this.remaining = remaining;
    loc = start;
  }
  
  public int read()
    throws IOException
  {
    if (remaining-- <= 0L)
    {
      if (addDummyByte)
      {
        addDummyByte = false;
        return 0;
      }
      return -1;
    }
    synchronized (ZipFile.access$600(this$0))
    {
      ZipFile.access$600(this$0).seek(loc++);
      return ZipFile.access$600(this$0).read();
    }
  }
  
  public int read(byte[] b, int off, int len)
    throws IOException
  {
    if (remaining <= 0L)
    {
      if (addDummyByte)
      {
        addDummyByte = false;
        b[off] = 0;
        return 1;
      }
      return -1;
    }
    if (len <= 0) {
      return 0;
    }
    if (len > remaining) {
      len = (int)remaining;
    }
    int ret;
    synchronized (ZipFile.access$600(this$0))
    {
      ZipFile.access$600(this$0).seek(loc);
      ret = ZipFile.access$600(this$0).read(b, off, len);
    }
    int ret;
    if (ret > 0)
    {
      loc += ret;
      remaining -= ret;
    }
    return ret;
  }
  
  void addDummy()
  {
    addDummyByte = true;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.ZipFile.BoundedInputStream
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */