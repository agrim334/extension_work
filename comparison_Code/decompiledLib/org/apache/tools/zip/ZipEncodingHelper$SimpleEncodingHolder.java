package org.apache.tools.zip;

class ZipEncodingHelper$SimpleEncodingHolder
{
  private final char[] highChars;
  private Simple8BitZipEncoding encoding;
  
  ZipEncodingHelper$SimpleEncodingHolder(char[] highChars)
  {
    this.highChars = highChars;
  }
  
  public synchronized Simple8BitZipEncoding getEncoding()
  {
    if (encoding == null) {
      encoding = new Simple8BitZipEncoding(highChars);
    }
    return encoding;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.ZipEncodingHelper.SimpleEncodingHolder
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */