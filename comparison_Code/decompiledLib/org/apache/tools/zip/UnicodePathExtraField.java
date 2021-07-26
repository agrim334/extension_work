package org.apache.tools.zip;

public class UnicodePathExtraField
  extends AbstractUnicodeExtraField
{
  public static final ZipShort UPATH_ID = new ZipShort(28789);
  
  public UnicodePathExtraField() {}
  
  public UnicodePathExtraField(String text, byte[] bytes, int off, int len)
  {
    super(text, bytes, off, len);
  }
  
  public UnicodePathExtraField(String name, byte[] bytes)
  {
    super(name, bytes);
  }
  
  public ZipShort getHeaderId()
  {
    return UPATH_ID;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.UnicodePathExtraField
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */