package org.apache.tools.zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

class ZipFile$1
  extends InflaterInputStream
{
  ZipFile$1(ZipFile this$0, InputStream arg0, Inflater arg1, Inflater paramInflater1)
  {
    super(arg0, arg1);
  }
  
  public void close()
    throws IOException
  {
    super.close();
    val$inflater.end();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.ZipFile.1
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */