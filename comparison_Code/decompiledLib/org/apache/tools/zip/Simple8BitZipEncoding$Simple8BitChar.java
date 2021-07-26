package org.apache.tools.zip;

final class Simple8BitZipEncoding$Simple8BitChar
  implements Comparable<Simple8BitChar>
{
  public final char unicode;
  public final byte code;
  
  Simple8BitZipEncoding$Simple8BitChar(byte code, char unicode)
  {
    this.code = code;
    this.unicode = unicode;
  }
  
  public int compareTo(Simple8BitChar a)
  {
    return unicode - unicode;
  }
  
  public String toString()
  {
    return 
      "0x" + Integer.toHexString(0xFFFF & unicode) + "->0x" + Integer.toHexString(0xFF & code);
  }
  
  public boolean equals(Object o)
  {
    if ((o instanceof Simple8BitChar))
    {
      Simple8BitChar other = (Simple8BitChar)o;
      return (unicode == unicode) && (code == code);
    }
    return false;
  }
  
  public int hashCode()
  {
    return unicode;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.Simple8BitZipEncoding.Simple8BitChar
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */