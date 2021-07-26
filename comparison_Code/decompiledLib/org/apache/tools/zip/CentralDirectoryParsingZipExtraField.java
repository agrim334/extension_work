package org.apache.tools.zip;

import java.util.zip.ZipException;

public abstract interface CentralDirectoryParsingZipExtraField
  extends ZipExtraField
{
  public abstract void parseFromCentralDirectoryData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ZipException;
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.CentralDirectoryParsingZipExtraField
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */