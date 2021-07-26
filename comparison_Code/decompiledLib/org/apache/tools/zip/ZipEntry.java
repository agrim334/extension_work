package org.apache.tools.zip;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.ZipException;

public class ZipEntry
  extends java.util.zip.ZipEntry
  implements Cloneable
{
  public static final int PLATFORM_UNIX = 3;
  public static final int PLATFORM_FAT = 0;
  public static final int CRC_UNKNOWN = -1;
  private static final int SHORT_MASK = 65535;
  private static final int SHORT_SHIFT = 16;
  private static final byte[] EMPTY = new byte[0];
  private int method = -1;
  private long size = -1L;
  private int internalAttributes = 0;
  private int platform = 0;
  private long externalAttributes = 0L;
  private ZipExtraField[] extraFields;
  private UnparseableExtraFieldData unparseableExtra = null;
  private String name = null;
  private byte[] rawName = null;
  private GeneralPurposeBit gpb = new GeneralPurposeBit();
  private static final ZipExtraField[] noExtraFields = new ZipExtraField[0];
  
  public ZipEntry(String name)
  {
    super(name);
    setName(name);
  }
  
  public ZipEntry(java.util.zip.ZipEntry entry)
    throws ZipException
  {
    super(entry);
    setName(entry.getName());
    byte[] extra = entry.getExtra();
    if (extra != null) {
      setExtraFields(ExtraFieldUtils.parse(extra, true, ExtraFieldUtils.UnparseableExtraField.READ));
    } else {
      setExtra();
    }
    setMethod(entry.getMethod());
    size = entry.getSize();
  }
  
  public ZipEntry(ZipEntry entry)
    throws ZipException
  {
    this(entry);
    setInternalAttributes(entry.getInternalAttributes());
    setExternalAttributes(entry.getExternalAttributes());
    setExtraFields(getAllExtraFieldsNoCopy());
    setPlatform(entry.getPlatform());
    GeneralPurposeBit other = entry.getGeneralPurposeBit();
    setGeneralPurposeBit(other == null ? null : (GeneralPurposeBit)other.clone());
  }
  
  protected ZipEntry()
  {
    this("");
  }
  
  public ZipEntry(File inputFile, String entryName)
  {
    this((inputFile.isDirectory()) && (!entryName.endsWith("/")) ? entryName + "/" : entryName);
    if (inputFile.isFile()) {
      setSize(inputFile.length());
    }
    setTime(inputFile.lastModified());
  }
  
  public Object clone()
  {
    ZipEntry e = (ZipEntry)super.clone();
    
    e.setInternalAttributes(getInternalAttributes());
    e.setExternalAttributes(getExternalAttributes());
    e.setExtraFields(getAllExtraFieldsNoCopy());
    return e;
  }
  
  public int getMethod()
  {
    return method;
  }
  
  public void setMethod(int method)
  {
    if (method < 0) {
      throw new IllegalArgumentException("ZIP compression method can not be negative: " + method);
    }
    this.method = method;
  }
  
  public int getInternalAttributes()
  {
    return internalAttributes;
  }
  
  public void setInternalAttributes(int value)
  {
    internalAttributes = value;
  }
  
  public long getExternalAttributes()
  {
    return externalAttributes;
  }
  
  public void setExternalAttributes(long value)
  {
    externalAttributes = value;
  }
  
  public void setUnixMode(int mode)
  {
    setExternalAttributes(mode << 16 | 
    
      ((mode & 0x80) == 0 ? 1 : 0) | 
      
      (isDirectory() ? 16 : 0));
    
    platform = 3;
  }
  
  public int getUnixMode()
  {
    return platform != 3 ? 0 : 
      (int)(getExternalAttributes() >> 16 & 0xFFFF);
  }
  
  public int getPlatform()
  {
    return platform;
  }
  
  protected void setPlatform(int platform)
  {
    this.platform = platform;
  }
  
  public void setExtraFields(ZipExtraField[] fields)
  {
    List<ZipExtraField> newFields = new ArrayList();
    for (ZipExtraField field : fields) {
      if ((field instanceof UnparseableExtraFieldData)) {
        unparseableExtra = ((UnparseableExtraFieldData)field);
      } else {
        newFields.add(field);
      }
    }
    extraFields = ((ZipExtraField[])newFields.toArray(new ZipExtraField[newFields.size()]));
    setExtra();
  }
  
  public ZipExtraField[] getExtraFields()
  {
    return getParseableExtraFields();
  }
  
  public ZipExtraField[] getExtraFields(boolean includeUnparseable)
  {
    return includeUnparseable ? getAllExtraFields() : getParseableExtraFields();
  }
  
  private ZipExtraField[] getParseableExtraFieldsNoCopy()
  {
    if (extraFields == null) {
      return noExtraFields;
    }
    return extraFields;
  }
  
  private ZipExtraField[] getParseableExtraFields()
  {
    ZipExtraField[] parseableExtraFields = getParseableExtraFieldsNoCopy();
    return parseableExtraFields == extraFields ? copyOf(parseableExtraFields) : 
      parseableExtraFields;
  }
  
  private ZipExtraField[] copyOf(ZipExtraField[] src)
  {
    return copyOf(src, src.length);
  }
  
  private ZipExtraField[] copyOf(ZipExtraField[] src, int length)
  {
    ZipExtraField[] cpy = new ZipExtraField[length];
    System.arraycopy(src, 0, cpy, 0, Math.min(src.length, length));
    return cpy;
  }
  
  private ZipExtraField[] getMergedFields()
  {
    ZipExtraField[] zipExtraFields = copyOf(extraFields, extraFields.length + 1);
    zipExtraFields[extraFields.length] = unparseableExtra;
    return zipExtraFields;
  }
  
  private ZipExtraField[] getUnparseableOnly()
  {
    return new ZipExtraField[] { unparseableExtra == null ? noExtraFields : unparseableExtra };
  }
  
  private ZipExtraField[] getAllExtraFields()
  {
    ZipExtraField[] allExtraFieldsNoCopy = getAllExtraFieldsNoCopy();
    return allExtraFieldsNoCopy == extraFields ? copyOf(allExtraFieldsNoCopy) : 
      allExtraFieldsNoCopy;
  }
  
  private ZipExtraField[] getAllExtraFieldsNoCopy()
  {
    if (extraFields == null) {
      return getUnparseableOnly();
    }
    return unparseableExtra != null ? getMergedFields() : extraFields;
  }
  
  public void addExtraField(ZipExtraField ze)
  {
    if ((ze instanceof UnparseableExtraFieldData))
    {
      unparseableExtra = ((UnparseableExtraFieldData)ze);
    }
    else if (extraFields == null)
    {
      extraFields = new ZipExtraField[] { ze };
    }
    else
    {
      if (getExtraField(ze.getHeaderId()) != null) {
        removeExtraField(ze.getHeaderId());
      }
      ZipExtraField[] zipExtraFields = copyOf(extraFields, extraFields.length + 1);
      zipExtraFields[extraFields.length] = ze;
      extraFields = zipExtraFields;
    }
    setExtra();
  }
  
  public void addAsFirstExtraField(ZipExtraField ze)
  {
    if ((ze instanceof UnparseableExtraFieldData))
    {
      unparseableExtra = ((UnparseableExtraFieldData)ze);
    }
    else
    {
      if (getExtraField(ze.getHeaderId()) != null) {
        removeExtraField(ze.getHeaderId());
      }
      ZipExtraField[] copy = extraFields;
      int newLen = extraFields != null ? extraFields.length + 1 : 1;
      extraFields = new ZipExtraField[newLen];
      extraFields[0] = ze;
      if (copy != null) {
        System.arraycopy(copy, 0, extraFields, 1, extraFields.length - 1);
      }
    }
    setExtra();
  }
  
  public void removeExtraField(ZipShort type)
  {
    if (extraFields == null) {
      throw new NoSuchElementException();
    }
    List<ZipExtraField> newResult = new ArrayList();
    for (ZipExtraField extraField : extraFields) {
      if (!type.equals(extraField.getHeaderId())) {
        newResult.add(extraField);
      }
    }
    if (extraFields.length == newResult.size()) {
      throw new NoSuchElementException();
    }
    extraFields = ((ZipExtraField[])newResult.toArray(new ZipExtraField[newResult.size()]));
    setExtra();
  }
  
  public void removeUnparseableExtraFieldData()
  {
    if (unparseableExtra == null) {
      throw new NoSuchElementException();
    }
    unparseableExtra = null;
    setExtra();
  }
  
  public ZipExtraField getExtraField(ZipShort type)
  {
    if (extraFields != null) {
      for (ZipExtraField extraField : extraFields) {
        if (type.equals(extraField.getHeaderId())) {
          return extraField;
        }
      }
    }
    return null;
  }
  
  public UnparseableExtraFieldData getUnparseableExtraFieldData()
  {
    return unparseableExtra;
  }
  
  public void setExtra(byte[] extra)
    throws RuntimeException
  {
    try
    {
      ZipExtraField[] local = ExtraFieldUtils.parse(extra, true, ExtraFieldUtils.UnparseableExtraField.READ);
      
      mergeExtraFields(local, true);
    }
    catch (ZipException e)
    {
      throw new RuntimeException("Error parsing extra fields for entry: " + getName() + " - " + e.getMessage(), e);
    }
  }
  
  protected void setExtra()
  {
    super.setExtra(ExtraFieldUtils.mergeLocalFileDataData(getExtraFields(true)));
  }
  
  public void setCentralDirectoryExtra(byte[] b)
  {
    try
    {
      ZipExtraField[] central = ExtraFieldUtils.parse(b, false, ExtraFieldUtils.UnparseableExtraField.READ);
      
      mergeExtraFields(central, false);
    }
    catch (ZipException e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  
  public byte[] getLocalFileDataExtra()
  {
    byte[] extra = getExtra();
    return extra != null ? extra : EMPTY;
  }
  
  public byte[] getCentralDirectoryExtra()
  {
    return ExtraFieldUtils.mergeCentralDirectoryData(getExtraFields(true));
  }
  
  @Deprecated
  public void setComprSize(long size)
  {
    setCompressedSize(size);
  }
  
  public String getName()
  {
    return name == null ? super.getName() : name;
  }
  
  public boolean isDirectory()
  {
    return getName().endsWith("/");
  }
  
  protected void setName(String name)
  {
    if ((name != null) && (getPlatform() == 0) && (!name.contains("/"))) {
      name = name.replace('\\', '/');
    }
    this.name = name;
  }
  
  public long getSize()
  {
    return size;
  }
  
  public void setSize(long size)
  {
    if (size < 0L) {
      throw new IllegalArgumentException("invalid entry size");
    }
    this.size = size;
  }
  
  protected void setName(String name, byte[] rawName)
  {
    setName(name);
    this.rawName = rawName;
  }
  
  public byte[] getRawName()
  {
    if (rawName != null)
    {
      byte[] b = new byte[rawName.length];
      System.arraycopy(rawName, 0, b, 0, rawName.length);
      return b;
    }
    return null;
  }
  
  public int hashCode()
  {
    return getName().hashCode();
  }
  
  public GeneralPurposeBit getGeneralPurposeBit()
  {
    return gpb;
  }
  
  public void setGeneralPurposeBit(GeneralPurposeBit b)
  {
    gpb = b;
  }
  
  private void mergeExtraFields(ZipExtraField[] f, boolean local)
    throws ZipException
  {
    if (extraFields == null)
    {
      setExtraFields(f);
    }
    else
    {
      for (ZipExtraField element : f)
      {
        ZipExtraField existing;
        ZipExtraField existing;
        if ((element instanceof UnparseableExtraFieldData)) {
          existing = unparseableExtra;
        } else {
          existing = getExtraField(element.getHeaderId());
        }
        if (existing == null)
        {
          addExtraField(element);
        }
        else if ((local) || (!(existing instanceof CentralDirectoryParsingZipExtraField)))
        {
          byte[] b = element.getLocalFileDataData();
          existing.parseFromLocalFileData(b, 0, b.length);
        }
        else
        {
          byte[] b = element.getCentralDirectoryData();
          ((CentralDirectoryParsingZipExtraField)existing)
            .parseFromCentralDirectoryData(b, 0, b.length);
        }
      }
      setExtra();
    }
  }
  
  public Date getLastModifiedDate()
  {
    return new Date(getTime());
  }
  
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    ZipEntry other = (ZipEntry)obj;
    String myName = getName();
    String otherName = other.getName();
    if (myName == null)
    {
      if (otherName != null) {
        return false;
      }
    }
    else if (!myName.equals(otherName)) {
      return false;
    }
    String myComment = getComment();
    String otherComment = other.getComment();
    if (myComment == null) {
      myComment = "";
    }
    if (otherComment == null) {
      otherComment = "";
    }
    return (getTime() == other.getTime()) && 
      (myComment.equals(otherComment)) && 
      (getInternalAttributes() == other.getInternalAttributes()) && 
      (getPlatform() == other.getPlatform()) && 
      (getExternalAttributes() == other.getExternalAttributes()) && 
      (getMethod() == other.getMethod()) && 
      (getSize() == other.getSize()) && 
      (getCrc() == other.getCrc()) && 
      (getCompressedSize() == other.getCompressedSize()) && 
      (Arrays.equals(getCentralDirectoryExtra(), other.getCentralDirectoryExtra())) && 
      (Arrays.equals(getLocalFileDataExtra(), other.getLocalFileDataExtra())) && 
      (gpb.equals(gpb));
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.ZipEntry
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */