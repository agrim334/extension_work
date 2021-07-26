package org.apache.tools.zip;

final class ZipOutputStream$CurrentEntry
{
  private final ZipEntry entry;
  
  private ZipOutputStream$CurrentEntry(ZipEntry entry)
  {
    this.entry = entry;
  }
  
  private long localDataStart = 0L;
  private long dataStart = 0L;
  private long bytesRead = 0L;
  private boolean causedUseOfZip64 = false;
  private boolean hasWritten;
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.ZipOutputStream.CurrentEntry
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */