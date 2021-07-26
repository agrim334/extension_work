package org.apache.tools.zip;

import java.math.BigInteger;

public final class ZipEightByteInteger
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
  private static final int BYTE_4 = 4;
  private static final long BYTE_4_MASK = 1095216660480L;
  private static final int BYTE_4_SHIFT = 32;
  private static final int BYTE_5 = 5;
  private static final long BYTE_5_MASK = 280375465082880L;
  private static final int BYTE_5_SHIFT = 40;
  private static final int BYTE_6 = 6;
  private static final long BYTE_6_MASK = 71776119061217280L;
  private static final int BYTE_6_SHIFT = 48;
  private static final int BYTE_7 = 7;
  private static final long BYTE_7_MASK = 9151314442816847872L;
  private static final int BYTE_7_SHIFT = 56;
  private static final int LEFTMOST_BIT_SHIFT = 63;
  private static final byte LEFTMOST_BIT = -128;
  private final BigInteger value;
  public static final ZipEightByteInteger ZERO = new ZipEightByteInteger(0L);
  
  public ZipEightByteInteger(long value)
  {
    this(BigInteger.valueOf(value));
  }
  
  public ZipEightByteInteger(BigInteger value)
  {
    this.value = value;
  }
  
  public ZipEightByteInteger(byte[] bytes)
  {
    this(bytes, 0);
  }
  
  public ZipEightByteInteger(byte[] bytes, int offset)
  {
    value = getValue(bytes, offset);
  }
  
  public byte[] getBytes()
  {
    return getBytes(value);
  }
  
  public long getLongValue()
  {
    return value.longValue();
  }
  
  public BigInteger getValue()
  {
    return value;
  }
  
  public static byte[] getBytes(long value)
  {
    return getBytes(BigInteger.valueOf(value));
  }
  
  public static byte[] getBytes(BigInteger value)
  {
    byte[] result = new byte[8];
    long val = value.longValue();
    result[0] = ((byte)(int)(val & 0xFF));
    result[1] = ((byte)(int)((val & 0xFF00) >> 8));
    result[2] = ((byte)(int)((val & 0xFF0000) >> 16));
    result[3] = ((byte)(int)((val & 0xFF000000) >> 24));
    result[4] = ((byte)(int)((val & 0xFF00000000) >> 32));
    result[5] = ((byte)(int)((val & 0xFF0000000000) >> 40));
    result[6] = ((byte)(int)((val & 0xFF000000000000) >> 48));
    result[7] = ((byte)(int)((val & 0x7F00000000000000) >> 56));
    if (value.testBit(63))
    {
      byte[] tmp125_122 = result;tmp125_122[7] = ((byte)(tmp125_122[7] | 0xFFFFFF80));
    }
    return result;
  }
  
  public static long getLongValue(byte[] bytes, int offset)
  {
    return getValue(bytes, offset).longValue();
  }
  
  public static BigInteger getValue(byte[] bytes, int offset)
  {
    long value = bytes[(offset + 7)] << 56 & 0x7F00000000000000;
    value += (bytes[(offset + 6)] << 48 & 0xFF000000000000);
    value += (bytes[(offset + 5)] << 40 & 0xFF0000000000);
    value += (bytes[(offset + 4)] << 32 & 0xFF00000000);
    value += (bytes[(offset + 3)] << 24 & 0xFF000000);
    value += (bytes[(offset + 2)] << 16 & 0xFF0000);
    value += (bytes[(offset + 1)] << 8 & 0xFF00);
    value += (bytes[offset] & 0xFF);
    BigInteger val = BigInteger.valueOf(value);
    return (bytes[(offset + 7)] & 0xFFFFFF80) == Byte.MIN_VALUE ? 
      val.setBit(63) : val;
  }
  
  public static long getLongValue(byte[] bytes)
  {
    return getLongValue(bytes, 0);
  }
  
  public static BigInteger getValue(byte[] bytes)
  {
    return getValue(bytes, 0);
  }
  
  public boolean equals(Object o)
  {
    return ((o instanceof ZipEightByteInteger)) && 
      (value.equals(((ZipEightByteInteger)o).getValue()));
  }
  
  public int hashCode()
  {
    return value.hashCode();
  }
  
  public String toString()
  {
    return "ZipEightByteInteger value: " + value;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.ZipEightByteInteger
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */