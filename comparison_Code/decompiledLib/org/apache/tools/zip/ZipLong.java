package org.apache.tools.zip;

public final class ZipLong
  implements Cloneable
{
  private static final int BYTE_1 = 1;
  private static final int BYTE_1_MASK = 65280;
  private static final int BYTE_1_SHIFT = 8;
  private static final int BYTE_2 = 2;
  private static final int BYTE_2_MASK = 16711680;
  private static final int BYTE_2_SHIFT = 16;
  private static final int BYTE_3 = 3;
  private static final long BYTE_3_MASK = 4278190080L;
  private static final int BYTE_3_SHIFT = 24;
  private final long value;
  public static final ZipLong CFH_SIG = new ZipLong(33639248L);
  public static final ZipLong LFH_SIG = new ZipLong(67324752L);
  public static final ZipLong DD_SIG = new ZipLong(134695760L);
  static final ZipLong ZIP64_MAGIC = new ZipLong(4294967295L);
  
  public ZipLong(long value)
  {
    this.value = value;
  }
  
  public ZipLong(byte[] bytes)
  {
    this(bytes, 0);
  }
  
  public ZipLong(byte[] bytes, int offset)
  {
    value = getValue(bytes, offset);
  }
  
  public byte[] getBytes()
  {
    return getBytes(value);
  }
  
  public long getValue()
  {
    return value;
  }
  
  public static byte[] getBytes(long value)
  {
    byte[] result = new byte[4];
    putLong(value, result, 0);
    return result;
  }
  
  public static void putLong(long value, byte[] buf, int offset)
  {
    buf[(offset++)] = ((byte)(int)(value & 0xFF));
    buf[(offset++)] = ((byte)(int)((value & 0xFF00) >> 8));
    buf[(offset++)] = ((byte)(int)((value & 0xFF0000) >> 16));
    buf[offset] = ((byte)(int)((value & 0xFF000000) >> 24));
  }
  
  public void putLong(byte[] buf, int offset)
  {
    putLong(value, buf, offset);
  }
  
  public static long getValue(byte[] bytes, int offset)
  {
    long value = bytes[(offset + 3)] << 24 & 0xFF000000;
    value += (bytes[(offset + 2)] << 16 & 0xFF0000);
    value += (bytes[(offset + 1)] << 8 & 0xFF00);
    value += (bytes[offset] & 0xFF);
    return value;
  }
  
  public static long getValue(byte[] bytes)
  {
    return getValue(bytes, 0);
  }
  
  public boolean equals(Object o)
  {
    return ((o instanceof ZipLong)) && (value == ((ZipLong)o).getValue());
  }
  
  public int hashCode()
  {
    return (int)value;
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
    return "ZipLong value: " + value;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.ZipLong
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */