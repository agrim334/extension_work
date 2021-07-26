package org.apache.tools.zip;

class ZipFile$Entry
  extends ZipEntry
{
  private final ZipFile.OffsetEntry offsetEntry;
  
  ZipFile$Entry(ZipFile.OffsetEntry offset)
  {
    offsetEntry = offset;
  }
  
  ZipFile.OffsetEntry getOffsetEntry()
  {
    return offsetEntry;
  }
  
  public int hashCode()
  {
    return 
      3 * super.hashCode() + (int)(ZipFile.OffsetEntry.access$200(offsetEntry) % 2147483647L);
  }
  
  public boolean equals(Object other)
  {
    if (super.equals(other))
    {
      Entry otherEntry = (Entry)other;
      if (ZipFile.OffsetEntry.access$200(offsetEntry) == ZipFile.OffsetEntry.access$200(offsetEntry)) {}
      return 
      
        ZipFile.OffsetEntry.access$000(offsetEntry) == ZipFile.OffsetEntry.access$000(offsetEntry);
    }
    return false;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.ZipFile.Entry
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */