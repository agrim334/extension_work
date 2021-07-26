package org.apache.tools.zip;

public final class ExtraFieldUtils$UnparseableExtraField
{
  public static final int THROW_KEY = 0;
  public static final int SKIP_KEY = 1;
  public static final int READ_KEY = 2;
  public static final UnparseableExtraField THROW = new UnparseableExtraField(0);
  public static final UnparseableExtraField SKIP = new UnparseableExtraField(1);
  public static final UnparseableExtraField READ = new UnparseableExtraField(2);
  private final int key;
  
  private ExtraFieldUtils$UnparseableExtraField(int k)
  {
    key = k;
  }
  
  public int getKey()
  {
    return key;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.ExtraFieldUtils.UnparseableExtraField
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */