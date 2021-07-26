package org.apache.tools.zip;

public final class UnparseableExtraFieldData
  implements CentralDirectoryParsingZipExtraField
{
  private static final ZipShort HEADER_ID = new ZipShort(44225);
  private byte[] localFileData;
  private byte[] centralDirectoryData;
  
  public ZipShort getHeaderId()
  {
    return HEADER_ID;
  }
  
  public ZipShort getLocalFileDataLength()
  {
    return new ZipShort(localFileData == null ? 0 : localFileData.length);
  }
  
  public ZipShort getCentralDirectoryLength()
  {
    return centralDirectoryData == null ? 
      getLocalFileDataLength() : 
      new ZipShort(centralDirectoryData.length);
  }
  
  public byte[] getLocalFileDataData()
  {
    return ZipUtil.copy(localFileData);
  }
  
  public byte[] getCentralDirectoryData()
  {
    return centralDirectoryData == null ? 
      getLocalFileDataData() : ZipUtil.copy(centralDirectoryData);
  }
  
  public void parseFromLocalFileData(byte[] buffer, int offset, int length)
  {
    localFileData = new byte[length];
    System.arraycopy(buffer, offset, localFileData, 0, length);
  }
  
  public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length)
  {
    centralDirectoryData = new byte[length];
    System.arraycopy(buffer, offset, centralDirectoryData, 0, length);
    if (localFileData == null) {
      parseFromLocalFileData(buffer, offset, length);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.UnparseableExtraFieldData
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */