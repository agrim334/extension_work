package org.apache.tools.tar;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;
import org.apache.tools.zip.ZipEncoding;

public class TarEntry
  implements TarConstants
{
  private String name;
  private int mode;
  private long userId;
  private long groupId;
  private long size;
  private long modTime;
  private byte linkFlag;
  private String linkName;
  private String magic;
  private String version;
  private String userName;
  private String groupName;
  private int devMajor;
  private int devMinor;
  private boolean isExtended;
  private long realSize;
  private File file;
  public static final int MAX_NAMELEN = 31;
  public static final int DEFAULT_DIR_MODE = 16877;
  public static final int DEFAULT_FILE_MODE = 33188;
  public static final int MILLIS_PER_SECOND = 1000;
  
  private TarEntry()
  {
    magic = "ustar\000";
    version = "00";
    name = "";
    linkName = "";
    
    String user = System.getProperty("user.name", "");
    if (user.length() > 31) {
      user = user.substring(0, 31);
    }
    userId = 0L;
    groupId = 0L;
    userName = user;
    groupName = "";
    file = null;
  }
  
  public TarEntry(String name)
  {
    this(name, false);
  }
  
  public TarEntry(String name, boolean preserveLeadingSlashes)
  {
    this();
    
    name = normalizeFileName(name, preserveLeadingSlashes);
    boolean isDir = name.endsWith("/");
    
    devMajor = 0;
    devMinor = 0;
    this.name = name;
    mode = (isDir ? 16877 : 33188);
    linkFlag = (isDir ? 53 : 48);
    userId = 0L;
    groupId = 0L;
    size = 0L;
    modTime = (new Date().getTime() / 1000L);
    linkName = "";
    userName = "";
    groupName = "";
  }
  
  public TarEntry(String name, byte linkFlag)
  {
    this(name);
    this.linkFlag = linkFlag;
    if (linkFlag == 76)
    {
      magic = "ustar  ";
      version = " \000";
    }
  }
  
  public TarEntry(File file)
  {
    this(file, file.getPath());
  }
  
  public TarEntry(File file, String fileName)
  {
    this();
    
    String normalizedName = normalizeFileName(fileName, false);
    this.file = file;
    
    linkName = "";
    if (file.isDirectory())
    {
      mode = 16877;
      linkFlag = 53;
      
      int nameLength = normalizedName.length();
      if ((nameLength == 0) || (normalizedName.charAt(nameLength - 1) != '/')) {
        name = (normalizedName + "/");
      } else {
        name = normalizedName;
      }
      size = 0L;
    }
    else
    {
      mode = 33188;
      linkFlag = 48;
      size = file.length();
      name = normalizedName;
    }
    modTime = (file.lastModified() / 1000L);
    devMajor = 0;
    devMinor = 0;
  }
  
  public TarEntry(byte[] headerBuf)
  {
    this();
    parseTarHeader(headerBuf);
  }
  
  public TarEntry(byte[] headerBuf, ZipEncoding encoding)
    throws IOException
  {
    this();
    parseTarHeader(headerBuf, encoding);
  }
  
  public boolean equals(TarEntry it)
  {
    return (it != null) && (getName().equals(it.getName()));
  }
  
  public boolean equals(Object it)
  {
    return (it != null) && (getClass() == it.getClass()) && (equals((TarEntry)it));
  }
  
  public int hashCode()
  {
    return getName().hashCode();
  }
  
  public boolean isDescendent(TarEntry desc)
  {
    return desc.getName().startsWith(getName());
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String name)
  {
    this.name = normalizeFileName(name, false);
  }
  
  public void setMode(int mode)
  {
    this.mode = mode;
  }
  
  public String getLinkName()
  {
    return linkName;
  }
  
  public void setLinkName(String link)
  {
    linkName = link;
  }
  
  @Deprecated
  public int getUserId()
  {
    return (int)userId;
  }
  
  public void setUserId(int userId)
  {
    setUserId(userId);
  }
  
  public long getLongUserId()
  {
    return userId;
  }
  
  public void setUserId(long userId)
  {
    this.userId = userId;
  }
  
  @Deprecated
  public int getGroupId()
  {
    return (int)groupId;
  }
  
  public void setGroupId(int groupId)
  {
    setGroupId(groupId);
  }
  
  public long getLongGroupId()
  {
    return groupId;
  }
  
  public void setGroupId(long groupId)
  {
    this.groupId = groupId;
  }
  
  public String getUserName()
  {
    return userName;
  }
  
  public void setUserName(String userName)
  {
    this.userName = userName;
  }
  
  public String getGroupName()
  {
    return groupName;
  }
  
  public void setGroupName(String groupName)
  {
    this.groupName = groupName;
  }
  
  public void setIds(int userId, int groupId)
  {
    setUserId(userId);
    setGroupId(groupId);
  }
  
  public void setNames(String userName, String groupName)
  {
    setUserName(userName);
    setGroupName(groupName);
  }
  
  public void setModTime(long time)
  {
    modTime = (time / 1000L);
  }
  
  public void setModTime(Date time)
  {
    modTime = (time.getTime() / 1000L);
  }
  
  public Date getModTime()
  {
    return new Date(modTime * 1000L);
  }
  
  public File getFile()
  {
    return file;
  }
  
  public int getMode()
  {
    return mode;
  }
  
  public long getSize()
  {
    return size;
  }
  
  public void setSize(long size)
  {
    if (size < 0L) {
      throw new IllegalArgumentException("Size is out of range: " + size);
    }
    this.size = size;
  }
  
  public int getDevMajor()
  {
    return devMajor;
  }
  
  public void setDevMajor(int devNo)
  {
    if (devNo < 0) {
      throw new IllegalArgumentException("Major device number is out of range: " + devNo);
    }
    devMajor = devNo;
  }
  
  public int getDevMinor()
  {
    return devMinor;
  }
  
  public void setDevMinor(int devNo)
  {
    if (devNo < 0) {
      throw new IllegalArgumentException("Minor device number is out of range: " + devNo);
    }
    devMinor = devNo;
  }
  
  public boolean isExtended()
  {
    return isExtended;
  }
  
  public long getRealSize()
  {
    return realSize;
  }
  
  public boolean isGNUSparse()
  {
    return linkFlag == 83;
  }
  
  public boolean isGNULongLinkEntry()
  {
    return linkFlag == 75;
  }
  
  public boolean isGNULongNameEntry()
  {
    return linkFlag == 76;
  }
  
  public boolean isPaxHeader()
  {
    return (linkFlag == 120) || (linkFlag == 88);
  }
  
  public boolean isGlobalPaxHeader()
  {
    return linkFlag == 103;
  }
  
  public boolean isDirectory()
  {
    if (file != null) {
      return file.isDirectory();
    }
    return (linkFlag == 53) || (getName().endsWith("/"));
  }
  
  public boolean isFile()
  {
    return 
      (linkFlag == 0) || (linkFlag == 48) || (!getName().endsWith("/")) ? true : file != null ? file.isFile() : false;
  }
  
  public boolean isSymbolicLink()
  {
    return linkFlag == 50;
  }
  
  public boolean isLink()
  {
    return linkFlag == 49;
  }
  
  public boolean isCharacterDevice()
  {
    return linkFlag == 51;
  }
  
  public boolean isBlockDevice()
  {
    return linkFlag == 52;
  }
  
  public boolean isFIFO()
  {
    return linkFlag == 54;
  }
  
  public TarEntry[] getDirectoryEntries()
  {
    if ((file == null) || (!file.isDirectory())) {
      return new TarEntry[0];
    }
    String[] list = file.list();
    TarEntry[] result = new TarEntry[list.length];
    for (int i = 0; i < list.length; i++) {
      result[i] = new TarEntry(new File(file, list[i]));
    }
    return result;
  }
  
  public void writeEntryHeader(byte[] outbuf)
  {
    try
    {
      writeEntryHeader(outbuf, TarUtils.DEFAULT_ENCODING, false);
    }
    catch (IOException ex)
    {
      try
      {
        writeEntryHeader(outbuf, TarUtils.FALLBACK_ENCODING, false);
      }
      catch (IOException ex2)
      {
        throw new RuntimeException(ex2);
      }
    }
  }
  
  public void writeEntryHeader(byte[] outbuf, ZipEncoding encoding, boolean starMode)
    throws IOException
  {
    int offset = 0;
    
    offset = TarUtils.formatNameBytes(name, outbuf, offset, 100, encoding);
    
    offset = writeEntryHeaderField(mode, outbuf, offset, 8, starMode);
    offset = writeEntryHeaderField(userId, outbuf, offset, 8, starMode);
    
    offset = writeEntryHeaderField(groupId, outbuf, offset, 8, starMode);
    
    offset = writeEntryHeaderField(size, outbuf, offset, 12, starMode);
    offset = writeEntryHeaderField(modTime, outbuf, offset, 12, starMode);
    
    int csOffset = offset;
    for (int c = 0; c < 8; c++) {
      outbuf[(offset++)] = 32;
    }
    outbuf[(offset++)] = linkFlag;
    offset = TarUtils.formatNameBytes(linkName, outbuf, offset, 100, encoding);
    
    offset = TarUtils.formatNameBytes(magic, outbuf, offset, 6);
    offset = TarUtils.formatNameBytes(version, outbuf, offset, 2);
    offset = TarUtils.formatNameBytes(userName, outbuf, offset, 32, encoding);
    
    offset = TarUtils.formatNameBytes(groupName, outbuf, offset, 32, encoding);
    
    offset = writeEntryHeaderField(devMajor, outbuf, offset, 8, starMode);
    
    offset = writeEntryHeaderField(devMinor, outbuf, offset, 8, starMode);
    while (offset < outbuf.length) {
      outbuf[(offset++)] = 0;
    }
    long chk = TarUtils.computeCheckSum(outbuf);
    
    TarUtils.formatCheckSumOctalBytes(chk, outbuf, csOffset, 8);
  }
  
  private int writeEntryHeaderField(long value, byte[] outbuf, int offset, int length, boolean starMode)
  {
    if ((!starMode) && ((value < 0L) || (value >= 1L << 3 * (length - 1)))) {
      return TarUtils.formatLongOctalBytes(0L, outbuf, offset, length);
    }
    return TarUtils.formatLongOctalOrBinaryBytes(value, outbuf, offset, length);
  }
  
  public void parseTarHeader(byte[] header)
  {
    try
    {
      parseTarHeader(header, TarUtils.DEFAULT_ENCODING);
    }
    catch (IOException ex)
    {
      try
      {
        parseTarHeader(header, TarUtils.DEFAULT_ENCODING, true);
      }
      catch (IOException ex2)
      {
        throw new RuntimeException(ex2);
      }
    }
  }
  
  public void parseTarHeader(byte[] header, ZipEncoding encoding)
    throws IOException
  {
    parseTarHeader(header, encoding, false);
  }
  
  private void parseTarHeader(byte[] header, ZipEncoding encoding, boolean oldStyle)
    throws IOException
  {
    int offset = 0;
    
    name = (oldStyle ? TarUtils.parseName(header, offset, 100) : TarUtils.parseName(header, offset, 100, encoding));
    offset += 100;
    mode = ((int)TarUtils.parseOctalOrBinary(header, offset, 8));
    offset += 8;
    userId = ((int)TarUtils.parseOctalOrBinary(header, offset, 8));
    offset += 8;
    groupId = ((int)TarUtils.parseOctalOrBinary(header, offset, 8));
    offset += 8;
    size = TarUtils.parseOctalOrBinary(header, offset, 12);
    offset += 12;
    modTime = TarUtils.parseOctalOrBinary(header, offset, 12);
    offset += 12;
    offset += 8;
    linkFlag = header[(offset++)];
    
    linkName = (oldStyle ? TarUtils.parseName(header, offset, 100) : TarUtils.parseName(header, offset, 100, encoding));
    offset += 100;
    magic = TarUtils.parseName(header, offset, 6);
    offset += 6;
    version = TarUtils.parseName(header, offset, 2);
    offset += 2;
    
    userName = (oldStyle ? TarUtils.parseName(header, offset, 32) : TarUtils.parseName(header, offset, 32, encoding));
    offset += 32;
    
    groupName = (oldStyle ? TarUtils.parseName(header, offset, 32) : TarUtils.parseName(header, offset, 32, encoding));
    offset += 32;
    devMajor = ((int)TarUtils.parseOctalOrBinary(header, offset, 8));
    offset += 8;
    devMinor = ((int)TarUtils.parseOctalOrBinary(header, offset, 8));
    offset += 8;
    
    int type = evaluateType(header);
    switch (type)
    {
    case 2: 
      offset += 12;
      offset += 12;
      offset += 12;
      offset += 4;
      offset++;
      offset += 96;
      isExtended = TarUtils.parseBoolean(header, offset);
      offset++;
      realSize = TarUtils.parseOctal(header, offset, 12);
      offset += 12;
      break;
    case 3: 
    default: 
      String prefix = oldStyle ? TarUtils.parseName(header, offset, 155) : TarUtils.parseName(header, offset, 155, encoding);
      if ((isDirectory()) && (!name.endsWith("/"))) {
        name += "/";
      }
      if (!prefix.isEmpty()) {
        name = (prefix + "/" + name);
      }
      break;
    }
  }
  
  private static String normalizeFileName(String fileName, boolean preserveLeadingSlashes)
  {
    String osname = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
    if (osname != null) {
      if (osname.startsWith("windows"))
      {
        if (fileName.length() > 2)
        {
          char ch1 = fileName.charAt(0);
          char ch2 = fileName.charAt(1);
          if ((ch2 == ':') && (((ch1 >= 'a') && (ch1 <= 'z')) || ((ch1 >= 'A') && (ch1 <= 'Z')))) {
            fileName = fileName.substring(2);
          }
        }
      }
      else if (osname.contains("netware"))
      {
        int colon = fileName.indexOf(':');
        if (colon != -1) {
          fileName = fileName.substring(colon + 1);
        }
      }
    }
    fileName = fileName.replace(File.separatorChar, '/');
    while ((!preserveLeadingSlashes) && (fileName.startsWith("/"))) {
      fileName = fileName.substring(1);
    }
    return fileName;
  }
  
  private int evaluateType(byte[] header)
  {
    if (matchAsciiBuffer("ustar  ", header, 257, 6)) {
      return 2;
    }
    if (matchAsciiBuffer("ustar\000", header, 257, 6)) {
      return 3;
    }
    return 0;
  }
  
  private static boolean matchAsciiBuffer(String expected, byte[] buffer, int offset, int length)
  {
    byte[] buffer1 = expected.getBytes(StandardCharsets.US_ASCII);
    return isEqual(buffer1, 0, buffer1.length, buffer, offset, length, false);
  }
  
  private static boolean isEqual(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2, boolean ignoreTrailingNulls)
  {
    int minLen = length1 < length2 ? length1 : length2;
    for (int i = 0; i < minLen; i++) {
      if (buffer1[(offset1 + i)] != buffer2[(offset2 + i)]) {
        return false;
      }
    }
    if (length1 == length2) {
      return true;
    }
    if (ignoreTrailingNulls)
    {
      if (length1 > length2) {
        for (int i = length2; i < length1; i++) {
          if (buffer1[(offset1 + i)] != 0) {
            return false;
          }
        }
      } else {
        for (int i = length1; i < length2; i++) {
          if (buffer2[(offset2 + i)] != 0) {
            return false;
          }
        }
      }
      return true;
    }
    return false;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.tar.TarEntry
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */