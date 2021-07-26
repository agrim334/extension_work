package org.apache.tools.zip;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract interface ZipEncoding
{
  public abstract boolean canEncode(String paramString);
  
  public abstract ByteBuffer encode(String paramString)
    throws IOException;
  
  public abstract String decode(byte[] paramArrayOfByte)
    throws IOException;
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.ZipEncoding
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */