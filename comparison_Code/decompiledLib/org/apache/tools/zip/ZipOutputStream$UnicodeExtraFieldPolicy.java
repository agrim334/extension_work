package org.apache.tools.zip;

public final class ZipOutputStream$UnicodeExtraFieldPolicy
{
  public static final UnicodeExtraFieldPolicy ALWAYS = new UnicodeExtraFieldPolicy("always");
  public static final UnicodeExtraFieldPolicy NEVER = new UnicodeExtraFieldPolicy("never");
  public static final UnicodeExtraFieldPolicy NOT_ENCODEABLE = new UnicodeExtraFieldPolicy("not encodeable");
  private final String name;
  
  private ZipOutputStream$UnicodeExtraFieldPolicy(String n)
  {
    name = n;
  }
  
  public String toString()
  {
    return name;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.ZipOutputStream.UnicodeExtraFieldPolicy
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */