package org.apache.tools.zip;

public class UnrecognizedExtraField
  implements CentralDirectoryParsingZipExtraField
{
  private ZipShort headerId;
  private byte[] localData;
  private byte[] centralData;
  
  public void setHeaderId(ZipShort headerId)
  {
    this.headerId = headerId;
  }
  
  public ZipShort getHeaderId()
  {
    return headerId;
  }
  
  public void setLocalFileDataData(byte[] data)
  {
    localData = ZipUtil.copy(data);
  }
  
  public ZipShort getLocalFileDataLength()
  {
    return new ZipShort(localData.length);
  }
  
  public byte[] getLocalFileDataData()
  {
    return ZipUtil.copy(localData);
  }
  
  public void setCentralDirectoryData(byte[] data)
  {
    centralData = ZipUtil.copy(data);
  }
  
  public ZipShort getCentralDirectoryLength()
  {
    if (centralData != null) {
      return new ZipShort(centralData.length);
    }
    return getLocalFileDataLength();
  }
  
  public byte[] getCentralDirectoryData()
  {
    if (centralData != null) {
      return ZipUtil.copy(centralData);
    }
    return getLocalFileDataData();
  }
  
  public void parseFromLocalFileData(byte[] data, int offset, int length)
  {
    byte[] tmp = new byte[length];
    System.arraycopy(data, offset, tmp, 0, length);
    setLocalFileDataData(tmp);
  }
  
  public void parseFromCentralDirectoryData(byte[] data, int offset, int length)
  {
    byte[] tmp = new byte[length];
    System.arraycopy(data, offset, tmp, 0, length);
    setCentralDirectoryData(tmp);
    if (localData == null) {
      setLocalFileDataData(tmp);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.UnrecognizedExtraField
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */