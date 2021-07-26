package org.apache.tools.tar;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.tools.zip.ZipEncoding;
import org.apache.tools.zip.ZipEncodingHelper;

public class TarOutputStream
  extends FilterOutputStream
{
  public static final int LONGFILE_ERROR = 0;
  public static final int LONGFILE_TRUNCATE = 1;
  public static final int LONGFILE_GNU = 2;
  public static final int LONGFILE_POSIX = 3;
  public static final int BIGNUMBER_ERROR = 0;
  public static final int BIGNUMBER_STAR = 1;
  public static final int BIGNUMBER_POSIX = 2;
  protected boolean debug;
  protected long currSize;
  protected String currName;
  protected long currBytes;
  protected byte[] oneBuf;
  protected byte[] recordBuf;
  protected int assemLen;
  protected byte[] assemBuf;
  protected TarBuffer buffer;
  protected int longFileMode = 0;
  private int bigNumberMode = 0;
  private boolean closed = false;
  private boolean haveUnclosedEntry = false;
  private boolean finished = false;
  private final ZipEncoding encoding;
  private boolean addPaxHeadersForNonAsciiNames = false;
  private static final ZipEncoding ASCII = ZipEncodingHelper.getZipEncoding("ASCII");
  
  public TarOutputStream(OutputStream os)
  {
    this(os, 10240, 512);
  }
  
  public TarOutputStream(OutputStream os, String encoding)
  {
    this(os, 10240, 512, encoding);
  }
  
  public TarOutputStream(OutputStream os, int blockSize)
  {
    this(os, blockSize, 512);
  }
  
  public TarOutputStream(OutputStream os, int blockSize, String encoding)
  {
    this(os, blockSize, 512, encoding);
  }
  
  public TarOutputStream(OutputStream os, int blockSize, int recordSize)
  {
    this(os, blockSize, recordSize, null);
  }
  
  public TarOutputStream(OutputStream os, int blockSize, int recordSize, String encoding)
  {
    super(os);
    this.encoding = ZipEncodingHelper.getZipEncoding(encoding);
    
    buffer = new TarBuffer(os, blockSize, recordSize);
    debug = false;
    assemLen = 0;
    assemBuf = new byte[recordSize];
    recordBuf = new byte[recordSize];
    oneBuf = new byte[1];
  }
  
  public void setLongFileMode(int longFileMode)
  {
    this.longFileMode = longFileMode;
  }
  
  public void setBigNumberMode(int bigNumberMode)
  {
    this.bigNumberMode = bigNumberMode;
  }
  
  public void setAddPaxHeadersForNonAsciiNames(boolean b)
  {
    addPaxHeadersForNonAsciiNames = b;
  }
  
  public void setDebug(boolean debugF)
  {
    debug = debugF;
  }
  
  public void setBufferDebug(boolean debug)
  {
    buffer.setDebug(debug);
  }
  
  public void finish()
    throws IOException
  {
    if (finished) {
      throw new IOException("This archive has already been finished");
    }
    if (haveUnclosedEntry) {
      throw new IOException("This archives contains unclosed entries.");
    }
    writeEOFRecord();
    writeEOFRecord();
    buffer.flushBlock();
    finished = true;
  }
  
  public void close()
    throws IOException
  {
    if (!finished) {
      finish();
    }
    if (!closed)
    {
      buffer.close();
      out.close();
      closed = true;
    }
  }
  
  public int getRecordSize()
  {
    return buffer.getRecordSize();
  }
  
  public void putNextEntry(TarEntry entry)
    throws IOException
  {
    if (finished) {
      throw new IOException("Stream has already been finished");
    }
    Map<String, String> paxHeaders = new HashMap();
    String entryName = entry.getName();
    boolean paxHeaderContainsPath = handleLongName(entry, entryName, paxHeaders, "path", (byte)76, "file name");
    
    String linkName = entry.getLinkName();
    
    boolean paxHeaderContainsLinkPath = (linkName != null) && (!linkName.isEmpty()) && (handleLongName(entry, linkName, paxHeaders, "linkpath", (byte)75, "link name"));
    if (bigNumberMode == 2) {
      addPaxHeadersForBigNumbers(paxHeaders, entry);
    } else if (bigNumberMode != 1) {
      failForBigNumbers(entry);
    }
    if ((addPaxHeadersForNonAsciiNames) && (!paxHeaderContainsPath) && 
      (!ASCII.canEncode(entryName))) {
      paxHeaders.put("path", entryName);
    }
    if ((addPaxHeadersForNonAsciiNames) && (!paxHeaderContainsLinkPath) && 
      ((entry.isLink()) || (entry.isSymbolicLink())) && 
      (!ASCII.canEncode(linkName))) {
      paxHeaders.put("linkpath", linkName);
    }
    if (paxHeaders.size() > 0) {
      writePaxHeaders(entry, entryName, paxHeaders);
    }
    entry.writeEntryHeader(recordBuf, encoding, bigNumberMode == 1);
    
    buffer.writeRecord(recordBuf);
    
    currBytes = 0L;
    if (entry.isDirectory()) {
      currSize = 0L;
    } else {
      currSize = entry.getSize();
    }
    currName = entryName;
    haveUnclosedEntry = true;
  }
  
  public void closeEntry()
    throws IOException
  {
    if (finished) {
      throw new IOException("Stream has already been finished");
    }
    if (!haveUnclosedEntry) {
      throw new IOException("No current entry to close");
    }
    if (assemLen > 0)
    {
      for (int i = assemLen; i < assemBuf.length; i++) {
        assemBuf[i] = 0;
      }
      buffer.writeRecord(assemBuf);
      
      currBytes += assemLen;
      assemLen = 0;
    }
    if (currBytes < currSize) {
      throw new IOException("entry '" + currName + "' closed at '" + currBytes + "' before the '" + currSize + "' bytes specified in the header were written");
    }
    haveUnclosedEntry = false;
  }
  
  public void write(int b)
    throws IOException
  {
    oneBuf[0] = ((byte)b);
    
    write(oneBuf, 0, 1);
  }
  
  public void write(byte[] wBuf)
    throws IOException
  {
    write(wBuf, 0, wBuf.length);
  }
  
  public void write(byte[] wBuf, int wOffset, int numToWrite)
    throws IOException
  {
    if (currBytes + numToWrite > currSize) {
      throw new IOException("request to write '" + numToWrite + "' bytes exceeds size in header of '" + currSize + "' bytes for entry '" + currName + "'");
    }
    if (assemLen > 0) {
      if (assemLen + numToWrite >= recordBuf.length)
      {
        int aLen = recordBuf.length - assemLen;
        
        System.arraycopy(assemBuf, 0, recordBuf, 0, assemLen);
        
        System.arraycopy(wBuf, wOffset, recordBuf, assemLen, aLen);
        
        buffer.writeRecord(recordBuf);
        
        currBytes += recordBuf.length;
        wOffset += aLen;
        numToWrite -= aLen;
        assemLen = 0;
      }
      else
      {
        System.arraycopy(wBuf, wOffset, assemBuf, assemLen, numToWrite);
        
        wOffset += numToWrite;
        assemLen += numToWrite;
        numToWrite = 0;
      }
    }
    while (numToWrite > 0)
    {
      if (numToWrite < recordBuf.length)
      {
        System.arraycopy(wBuf, wOffset, assemBuf, assemLen, numToWrite);
        
        assemLen += numToWrite;
        
        break;
      }
      buffer.writeRecord(wBuf, wOffset);
      
      int num = recordBuf.length;
      
      currBytes += num;
      numToWrite -= num;
      wOffset += num;
    }
  }
  
  void writePaxHeaders(TarEntry entry, String entryName, Map<String, String> headers)
    throws IOException
  {
    String name = "./PaxHeaders.X/" + stripTo7Bits(entryName);
    if (name.length() >= 100) {
      name = name.substring(0, 99);
    }
    while (name.endsWith("/")) {
      name = name.substring(0, name.length() - 1);
    }
    TarEntry pex = new TarEntry(name, (byte)120);
    
    transferModTime(entry, pex);
    
    StringWriter w = new StringWriter();
    for (Map.Entry<String, String> h : headers.entrySet())
    {
      String key = (String)h.getKey();
      String value = (String)h.getValue();
      int len = key.length() + value.length() + 3 + 2;
      
      String line = len + " " + key + "=" + value + "\n";
      int actualLength = line.getBytes(StandardCharsets.UTF_8).length;
      while (len != actualLength)
      {
        len = actualLength;
        line = len + " " + key + "=" + value + "\n";
        actualLength = line.getBytes(StandardCharsets.UTF_8).length;
      }
      w.write(line);
    }
    byte[] data = w.toString().getBytes(StandardCharsets.UTF_8);
    pex.setSize(data.length);
    putNextEntry(pex);
    write(data);
    closeEntry();
  }
  
  private String stripTo7Bits(String name)
  {
    StringBuilder result = new StringBuilder(name.length());
    for (char ch : name.toCharArray())
    {
      char stripped = (char)(ch & 0x7F);
      if (stripped != 0) {
        result.append(stripped);
      }
    }
    return result.toString();
  }
  
  private void writeEOFRecord()
    throws IOException
  {
    for (int i = 0; i < recordBuf.length; i++) {
      recordBuf[i] = 0;
    }
    buffer.writeRecord(recordBuf);
  }
  
  private void addPaxHeadersForBigNumbers(Map<String, String> paxHeaders, TarEntry entry)
  {
    addPaxHeaderForBigNumber(paxHeaders, "size", entry.getSize(), 8589934591L);
    
    addPaxHeaderForBigNumber(paxHeaders, "gid", entry.getLongGroupId(), 2097151L);
    
    addPaxHeaderForBigNumber(paxHeaders, "mtime", entry
      .getModTime().getTime() / 1000L, 8589934591L);
    
    addPaxHeaderForBigNumber(paxHeaders, "uid", entry.getLongUserId(), 2097151L);
    
    addPaxHeaderForBigNumber(paxHeaders, "SCHILY.devmajor", entry
      .getDevMajor(), 2097151L);
    addPaxHeaderForBigNumber(paxHeaders, "SCHILY.devminor", entry
      .getDevMinor(), 2097151L);
    
    failForBigNumber("mode", entry.getMode(), 2097151L);
  }
  
  private void addPaxHeaderForBigNumber(Map<String, String> paxHeaders, String header, long value, long maxValue)
  {
    if ((value < 0L) || (value > maxValue)) {
      paxHeaders.put(header, String.valueOf(value));
    }
  }
  
  private void failForBigNumbers(TarEntry entry)
  {
    failForBigNumber("entry size", entry.getSize(), 8589934591L);
    failForBigNumberWithPosixMessage("group id", entry.getLongGroupId(), 2097151L);
    failForBigNumber("last modification time", entry
      .getModTime().getTime() / 1000L, 8589934591L);
    
    failForBigNumber("user id", entry.getLongUserId(), 2097151L);
    failForBigNumber("mode", entry.getMode(), 2097151L);
    failForBigNumber("major device number", entry.getDevMajor(), 2097151L);
    
    failForBigNumber("minor device number", entry.getDevMinor(), 2097151L);
  }
  
  private void failForBigNumber(String field, long value, long maxValue)
  {
    failForBigNumber(field, value, maxValue, "");
  }
  
  private void failForBigNumberWithPosixMessage(String field, long value, long maxValue)
  {
    failForBigNumber(field, value, maxValue, " Use STAR or POSIX extensions to overcome this limit");
  }
  
  private void failForBigNumber(String field, long value, long maxValue, String additionalMsg)
  {
    if ((value < 0L) || (value > maxValue)) {
      throw new RuntimeException(field + " '" + value + "' is too big ( > " + maxValue + " )");
    }
  }
  
  private boolean handleLongName(TarEntry entry, String name, Map<String, String> paxHeaders, String paxHeaderName, byte linkType, String fieldName)
    throws IOException
  {
    ByteBuffer encodedName = encoding.encode(name);
    int len = encodedName.limit() - encodedName.position();
    if (len >= 100)
    {
      if (longFileMode == 3)
      {
        paxHeaders.put(paxHeaderName, name);
        return true;
      }
      if (longFileMode == 2)
      {
        TarEntry longLinkEntry = new TarEntry("././@LongLink", linkType);
        
        longLinkEntry.setSize(len + 1);
        transferModTime(entry, longLinkEntry);
        putNextEntry(longLinkEntry);
        write(encodedName.array(), encodedName.arrayOffset(), len);
        write(0);
        closeEntry();
      }
      else if (longFileMode != 1)
      {
        throw new RuntimeException(fieldName + " '" + name + "' is too long ( > " + 100 + " bytes)");
      }
    }
    return false;
  }
  
  private void transferModTime(TarEntry from, TarEntry to)
  {
    Date fromModTime = from.getModTime();
    long fromModTimeSeconds = fromModTime.getTime() / 1000L;
    if ((fromModTimeSeconds < 0L) || (fromModTimeSeconds > 8589934591L)) {
      fromModTime = new Date(0L);
    }
    to.setModTime(fromModTime);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.tar.TarOutputStream
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */