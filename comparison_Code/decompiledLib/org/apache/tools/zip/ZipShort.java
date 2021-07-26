package org.apache.tools.zip;

public final class ZipShort
  implements Cloneable
{
  private static final int BYTE_1_MASK = 65280;
  private static final int BYTE_1_SHIFT = 8;
  private final int value;
  
  public ZipShort(int value)
  {
    this.value = value;
  }
  
  public ZipShort(byte[] bytes)
  {
    this(bytes, 0);
  }
  
  public ZipShort(byte[] bytes, int offset)
  {
    value = getValue(bytes, offset);
  }
  
  public byte[] getBytes()
  {
    byte[] result = new byte[2];
    putShort(value, result, 0);
    return result;
  }
  
  public static void putShort(int value, byte[] buf, int offset)
  {
    buf[offset] = ((byte)(value & 0xFF));
    buf[(offset + 1)] = ((byte)((value & 0xFF00) >> 8));
  }
  
  public int getValue()
  {
    return value;
  }
  
  public static byte[] getBytes(int value)
  {
    byte[] result = new byte[2];
    result[0] = ((byte)(value & 0xFF));
    result[1] = ((byte)((value & 0xFF00) >> 8));
    return result;
  }
  
  public static int getValue(byte[] bytes, int offset)
  {
    int value = bytes[(offset + 1)] << 8 & 0xFF00;
    value += (bytes[offset] & 0xFF);
    return value;
  }
  
  public static int getValue(byte[] bytes)
  {
    return getValue(bytes, 0);
  }
  
  public boolean equals(Object o)
  {
    return ((o instanceof ZipShort)) && (value == ((ZipShort)o).getValue());
  }
  
  public int hashCode()
  {
    return value;
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException cnfe)
    {
      throw new RuntimeException(cnfe);
    }
  }
  
  public String toString()
  {
    return "ZipShort value: " + value;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.ZipShort
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */