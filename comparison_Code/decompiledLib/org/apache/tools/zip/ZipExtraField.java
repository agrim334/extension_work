package org.apache.tools.zip;

import java.util.zip.ZipException;

public abstract interface ZipExtraField
{
  public abstract ZipShort getHeaderId();
  
  public abstract ZipShort getLocalFileDataLength();
  
  public abstract ZipShort getCentralDirectoryLength();
  
  public abstract byte[] getLocalFileDataData();
  
  public abstract byte[] getCentralDirectoryData();
  
  public abstract void parseFromLocalFileData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ZipException;
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.ZipExtraField
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */