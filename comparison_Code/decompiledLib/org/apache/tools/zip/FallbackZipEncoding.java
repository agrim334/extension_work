package org.apache.tools.zip;

import java.io.IOException;
import java.nio.ByteBuffer;

class FallbackZipEncoding
  implements ZipEncoding
{
  private final String charset;
  
  public FallbackZipEncoding()
  {
    charset = null;
  }
  
  public FallbackZipEncoding(String charset)
  {
    this.charset = charset;
  }
  
  public boolean canEncode(String name)
  {
    return true;
  }
  
  public ByteBuffer encode(String name)
    throws IOException
  {
    if (charset == null) {
      return ByteBuffer.wrap(name.getBytes());
    }
    return ByteBuffer.wrap(name.getBytes(charset));
  }
  
  public String decode(byte[] data)
    throws IOException
  {
    if (charset == null) {
      return new String(data);
    }
    return new String(data, charset);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.FallbackZipEncoding
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */