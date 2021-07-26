package org.apache.tools.zip;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;
import java.util.zip.ZipException;

public abstract class AbstractUnicodeExtraField
  implements ZipExtraField
{
  private long nameCRC32;
  private byte[] unicodeName;
  private byte[] data;
  
  protected AbstractUnicodeExtraField() {}
  
  protected AbstractUnicodeExtraField(String text, byte[] bytes, int off, int len)
  {
    CRC32 crc32 = new CRC32();
    crc32.update(bytes, off, len);
    nameCRC32 = crc32.getValue();
    
    unicodeName = text.getBytes(StandardCharsets.UTF_8);
  }
  
  protected AbstractUnicodeExtraField(String text, byte[] bytes)
  {
    this(text, bytes, 0, bytes.length);
  }
  
  private void assembleData()
  {
    if (unicodeName == null) {
      return;
    }
    data = new byte[5 + unicodeName.length];
    
    data[0] = 1;
    System.arraycopy(ZipLong.getBytes(nameCRC32), 0, data, 1, 4);
    System.arraycopy(unicodeName, 0, data, 5, unicodeName.length);
  }
  
  public long getNameCRC32()
  {
    return nameCRC32;
  }
  
  public void setNameCRC32(long nameCRC32)
  {
    this.nameCRC32 = nameCRC32;
    data = null;
  }
  
  public byte[] getUnicodeName()
  {
    byte[] b = null;
    if (unicodeName != null)
    {
      b = new byte[unicodeName.length];
      System.arraycopy(unicodeName, 0, b, 0, b.length);
    }
    return b;
  }
  
  public void setUnicodeName(byte[] unicodeName)
  {
    if (unicodeName != null)
    {
      this.unicodeName = new byte[unicodeName.length];
      System.arraycopy(unicodeName, 0, this.unicodeName, 0, unicodeName.length);
    }
    else
    {
      this.unicodeName = null;
    }
    data = null;
  }
  
  public byte[] getCentralDirectoryData()
  {
    if (data == null) {
      assembleData();
    }
    byte[] b = null;
    if (data != null)
    {
      b = new byte[data.length];
      System.arraycopy(data, 0, b, 0, b.length);
    }
    return b;
  }
  
  public ZipShort getCentralDirectoryLength()
  {
    if (data == null) {
      assembleData();
    }
    return new ZipShort(data.length);
  }
  
  public byte[] getLocalFileDataData()
  {
    return getCentralDirectoryData();
  }
  
  public ZipShort getLocalFileDataLength()
  {
    return getCentralDirectoryLength();
  }
  
  public void parseFromLocalFileData(byte[] buffer, int offset, int length)
    throws ZipException
  {
    if (length < 5) {
      throw new ZipException("UniCode path extra data must have at least 5 bytes.");
    }
    int version = buffer[offset];
    if (version != 1) {
      throw new ZipException("Unsupported version [" + version + "] for UniCode path extra data.");
    }
    nameCRC32 = ZipLong.getValue(buffer, offset + 1);
    unicodeName = new byte[length - 5];
    System.arraycopy(buffer, offset + 5, unicodeName, 0, length - 5);
    data = null;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.AbstractUnicodeExtraField
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */