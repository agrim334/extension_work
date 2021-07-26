package org.apache.tools.zip;

import java.util.zip.ZipException;

public class Zip64ExtendedInformationExtraField
  implements CentralDirectoryParsingZipExtraField
{
  static final ZipShort HEADER_ID = new ZipShort(1);
  private static final String LFH_MUST_HAVE_BOTH_SIZES_MSG = "Zip64 extended information must contain both size values in the local file header.";
  private static final byte[] EMPTY = new byte[0];
  private ZipEightByteInteger size;
  private ZipEightByteInteger compressedSize;
  private ZipEightByteInteger relativeHeaderOffset;
  private ZipLong diskStart;
  private byte[] rawCentralDirectoryData;
  
  public Zip64ExtendedInformationExtraField() {}
  
  public Zip64ExtendedInformationExtraField(ZipEightByteInteger size, ZipEightByteInteger compressedSize)
  {
    this(size, compressedSize, null, null);
  }
  
  public Zip64ExtendedInformationExtraField(ZipEightByteInteger size, ZipEightByteInteger compressedSize, ZipEightByteInteger relativeHeaderOffset, ZipLong diskStart)
  {
    this.size = size;
    this.compressedSize = compressedSize;
    this.relativeHeaderOffset = relativeHeaderOffset;
    this.diskStart = diskStart;
  }
  
  public ZipShort getHeaderId()
  {
    return HEADER_ID;
  }
  
  public ZipShort getLocalFileDataLength()
  {
    return new ZipShort(size != null ? 16 : 0);
  }
  
  public ZipShort getCentralDirectoryLength()
  {
    return new ZipShort((size != null ? 8 : 0) + (
      compressedSize != null ? 8 : 0) + (
      relativeHeaderOffset != null ? 8 : 0) + (
      diskStart != null ? 4 : 0));
  }
  
  public byte[] getLocalFileDataData()
  {
    if ((size != null) || (compressedSize != null))
    {
      if ((size == null) || (compressedSize == null)) {
        throw new IllegalArgumentException("Zip64 extended information must contain both size values in the local file header.");
      }
      byte[] data = new byte[16];
      addSizes(data);
      return data;
    }
    return EMPTY;
  }
  
  public byte[] getCentralDirectoryData()
  {
    byte[] data = new byte[getCentralDirectoryLength().getValue()];
    int off = addSizes(data);
    if (relativeHeaderOffset != null)
    {
      System.arraycopy(relativeHeaderOffset.getBytes(), 0, data, off, 8);
      off += 8;
    }
    if (diskStart != null)
    {
      System.arraycopy(diskStart.getBytes(), 0, data, off, 4);
      off += 4;
    }
    return data;
  }
  
  public void parseFromLocalFileData(byte[] buffer, int offset, int length)
    throws ZipException
  {
    if (length == 0) {
      return;
    }
    if (length < 16) {
      throw new ZipException("Zip64 extended information must contain both size values in the local file header.");
    }
    size = new ZipEightByteInteger(buffer, offset);
    offset += 8;
    compressedSize = new ZipEightByteInteger(buffer, offset);
    offset += 8;
    int remaining = length - 16;
    if (remaining >= 8)
    {
      relativeHeaderOffset = new ZipEightByteInteger(buffer, offset);
      offset += 8;
      remaining -= 8;
    }
    if (remaining >= 4)
    {
      diskStart = new ZipLong(buffer, offset);
      offset += 4;
      remaining -= 4;
    }
  }
  
  public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length)
    throws ZipException
  {
    rawCentralDirectoryData = new byte[length];
    System.arraycopy(buffer, offset, rawCentralDirectoryData, 0, length);
    if (length >= 28)
    {
      parseFromLocalFileData(buffer, offset, length);
    }
    else if (length == 24)
    {
      size = new ZipEightByteInteger(buffer, offset);
      offset += 8;
      compressedSize = new ZipEightByteInteger(buffer, offset);
      offset += 8;
      relativeHeaderOffset = new ZipEightByteInteger(buffer, offset);
    }
    else if (length % 8 == 4)
    {
      diskStart = new ZipLong(buffer, offset + length - 4);
    }
  }
  
  public void reparseCentralDirectoryData(boolean hasUncompressedSize, boolean hasCompressedSize, boolean hasRelativeHeaderOffset, boolean hasDiskStart)
    throws ZipException
  {
    if (rawCentralDirectoryData != null)
    {
      int expectedLength = (hasUncompressedSize ? 8 : 0) + (hasCompressedSize ? 8 : 0) + (hasRelativeHeaderOffset ? 8 : 0) + (hasDiskStart ? 4 : 0);
      if (rawCentralDirectoryData.length < expectedLength) {
        throw new ZipException("central directory zip64 extended information extra field's length doesn't match central directory data.  Expected length " + expectedLength + " but is " + rawCentralDirectoryData.length);
      }
      int offset = 0;
      if (hasUncompressedSize)
      {
        size = new ZipEightByteInteger(rawCentralDirectoryData, offset);
        offset += 8;
      }
      if (hasCompressedSize)
      {
        compressedSize = new ZipEightByteInteger(rawCentralDirectoryData, offset);
        
        offset += 8;
      }
      if (hasRelativeHeaderOffset)
      {
        relativeHeaderOffset = new ZipEightByteInteger(rawCentralDirectoryData, offset);
        
        offset += 8;
      }
      if (hasDiskStart)
      {
        diskStart = new ZipLong(rawCentralDirectoryData, offset);
        offset += 4;
      }
    }
  }
  
  public ZipEightByteInteger getSize()
  {
    return size;
  }
  
  public void setSize(ZipEightByteInteger size)
  {
    this.size = size;
  }
  
  public ZipEightByteInteger getCompressedSize()
  {
    return compressedSize;
  }
  
  public void setCompressedSize(ZipEightByteInteger compressedSize)
  {
    this.compressedSize = compressedSize;
  }
  
  public ZipEightByteInteger getRelativeHeaderOffset()
  {
    return relativeHeaderOffset;
  }
  
  public void setRelativeHeaderOffset(ZipEightByteInteger rho)
  {
    relativeHeaderOffset = rho;
  }
  
  public ZipLong getDiskStartNumber()
  {
    return diskStart;
  }
  
  public void setDiskStartNumber(ZipLong ds)
  {
    diskStart = ds;
  }
  
  private int addSizes(byte[] data)
  {
    int off = 0;
    if (size != null)
    {
      System.arraycopy(size.getBytes(), 0, data, 0, 8);
      off += 8;
    }
    if (compressedSize != null)
    {
      System.arraycopy(compressedSize.getBytes(), 0, data, off, 8);
      off += 8;
    }
    return off;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.Zip64ExtendedInformationExtraField
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */