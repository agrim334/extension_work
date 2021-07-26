package org.apache.tools.zip;

public final class GeneralPurposeBit
  implements Cloneable
{
  private static final int ENCRYPTION_FLAG = 1;
  private static final int DATA_DESCRIPTOR_FLAG = 8;
  private static final int STRONG_ENCRYPTION_FLAG = 64;
  public static final int UFT8_NAMES_FLAG = 2048;
  private boolean languageEncodingFlag = false;
  private boolean dataDescriptorFlag = false;
  private boolean encryptionFlag = false;
  private boolean strongEncryptionFlag = false;
  
  public boolean usesUTF8ForNames()
  {
    return languageEncodingFlag;
  }
  
  public void useUTF8ForNames(boolean b)
  {
    languageEncodingFlag = b;
  }
  
  public boolean usesDataDescriptor()
  {
    return dataDescriptorFlag;
  }
  
  public void useDataDescriptor(boolean b)
  {
    dataDescriptorFlag = b;
  }
  
  public boolean usesEncryption()
  {
    return encryptionFlag;
  }
  
  public void useEncryption(boolean b)
  {
    encryptionFlag = b;
  }
  
  public boolean usesStrongEncryption()
  {
    return (encryptionFlag) && (strongEncryptionFlag);
  }
  
  public void useStrongEncryption(boolean b)
  {
    strongEncryptionFlag = b;
    if (b) {
      useEncryption(true);
    }
  }
  
  public byte[] encode()
  {
    byte[] result = new byte[2];
    encode(result, 0);
    return result;
  }
  
  public void encode(byte[] buf, int offset)
  {
    ZipShort.putShort(
    
      (dataDescriptorFlag ? 8 : 0) | (languageEncodingFlag ? 2048 : 0) | (encryptionFlag ? 1 : 0) | (strongEncryptionFlag ? 64 : 0), buf, offset);
  }
  
  public static GeneralPurposeBit parse(byte[] data, int offset)
  {
    int generalPurposeFlag = ZipShort.getValue(data, offset);
    GeneralPurposeBit b = new GeneralPurposeBit();
    b.useDataDescriptor((generalPurposeFlag & 0x8) != 0);
    b.useUTF8ForNames((generalPurposeFlag & 0x800) != 0);
    b.useStrongEncryption((generalPurposeFlag & 0x40) != 0);
    
    b.useEncryption((generalPurposeFlag & 0x1) != 0);
    return b;
  }
  
  public int hashCode()
  {
    return 
    
      3 * (7 * (13 * (17 * (encryptionFlag ? 1 : 0) + (strongEncryptionFlag ? 1 : 0)) + (languageEncodingFlag ? 1 : 0)) + (dataDescriptorFlag ? 1 : 0));
  }
  
  public boolean equals(Object o)
  {
    if ((o instanceof GeneralPurposeBit))
    {
      GeneralPurposeBit g = (GeneralPurposeBit)o;
      return (encryptionFlag == encryptionFlag) && (strongEncryptionFlag == strongEncryptionFlag) && (languageEncodingFlag == languageEncodingFlag) && (dataDescriptorFlag == dataDescriptorFlag);
    }
    return false;
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      throw new RuntimeException("GeneralPurposeBit is not Cloneable?", ex);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.GeneralPurposeBit
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */