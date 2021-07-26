package org.apache.tools.ant.util;

public class Base64Converter
{
  private static final int BYTE = 8;
  private static final int WORD = 16;
  private static final int BYTE_MASK = 255;
  private static final int POS_0_MASK = 63;
  private static final int POS_1_MASK = 4032;
  private static final int POS_1_SHIFT = 6;
  private static final int POS_2_MASK = 258048;
  private static final int POS_2_SHIFT = 12;
  private static final int POS_3_MASK = 16515072;
  private static final int POS_3_SHIFT = 18;
  private static final char[] ALPHABET = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
  public static final char[] alphabet = ALPHABET;
  
  public String encode(String s)
  {
    return encode(s.getBytes());
  }
  
  public String encode(byte[] octetString)
  {
    char[] out = new char[((octetString.length - 1) / 3 + 1) * 4];
    
    int outIndex = 0;
    int i = 0;
    while (i + 3 <= octetString.length)
    {
      int bits24 = (octetString[(i++)] & 0xFF) << 16;
      bits24 |= (octetString[(i++)] & 0xFF) << 8;
      bits24 |= octetString[(i++)] & 0xFF;
      
      int bits6 = (bits24 & 0xFC0000) >> 18;
      out[(outIndex++)] = ALPHABET[bits6];
      bits6 = (bits24 & 0x3F000) >> 12;
      out[(outIndex++)] = ALPHABET[bits6];
      bits6 = (bits24 & 0xFC0) >> 6;
      out[(outIndex++)] = ALPHABET[bits6];
      bits6 = bits24 & 0x3F;
      out[(outIndex++)] = ALPHABET[bits6];
    }
    if (octetString.length - i == 2)
    {
      int bits24 = (octetString[i] & 0xFF) << 16;
      bits24 |= (octetString[(i + 1)] & 0xFF) << 8;
      int bits6 = (bits24 & 0xFC0000) >> 18;
      out[(outIndex++)] = ALPHABET[bits6];
      bits6 = (bits24 & 0x3F000) >> 12;
      out[(outIndex++)] = ALPHABET[bits6];
      bits6 = (bits24 & 0xFC0) >> 6;
      out[(outIndex++)] = ALPHABET[bits6];
      
      out[(outIndex++)] = '=';
    }
    else if (octetString.length - i == 1)
    {
      int bits24 = (octetString[i] & 0xFF) << 16;
      int bits6 = (bits24 & 0xFC0000) >> 18;
      out[(outIndex++)] = ALPHABET[bits6];
      bits6 = (bits24 & 0x3F000) >> 12;
      out[(outIndex++)] = ALPHABET[bits6];
      
      out[(outIndex++)] = '=';
      out[(outIndex++)] = '=';
    }
    return new String(out);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.Base64Converter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */