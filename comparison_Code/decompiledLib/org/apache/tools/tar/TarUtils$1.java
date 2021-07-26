package org.apache.tools.tar;

import java.nio.ByteBuffer;
import org.apache.tools.zip.ZipEncoding;

class TarUtils$1
  implements ZipEncoding
{
  public boolean canEncode(String name)
  {
    return true;
  }
  
  public ByteBuffer encode(String name)
  {
    int length = name.length();
    byte[] buf = new byte[length];
    for (int i = 0; i < length; i++) {
      buf[i] = ((byte)name.charAt(i));
    }
    return ByteBuffer.wrap(buf);
  }
  
  public String decode(byte[] buffer)
  {
    StringBuilder result = new StringBuilder(buffer.length);
    for (byte b : buffer)
    {
      if (b == 0) {
        break;
      }
      result.append((char)(b & 0xFF));
    }
    return result.toString();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.tar.TarUtils.1
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */