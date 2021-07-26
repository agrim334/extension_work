package org.apache.tools.zip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.ZipException;

public class ZipOutputStream
  extends FilterOutputStream
{
  private static final int BUFFER_SIZE = 512;
  private static final int LFH_SIG_OFFSET = 0;
  private static final int LFH_VERSION_NEEDED_OFFSET = 4;
  private static final int LFH_GPB_OFFSET = 6;
  private static final int LFH_METHOD_OFFSET = 8;
  private static final int LFH_TIME_OFFSET = 10;
  private static final int LFH_CRC_OFFSET = 14;
  private static final int LFH_COMPRESSED_SIZE_OFFSET = 18;
  private static final int LFH_ORIGINAL_SIZE_OFFSET = 22;
  private static final int LFH_FILENAME_LENGTH_OFFSET = 26;
  private static final int LFH_EXTRA_LENGTH_OFFSET = 28;
  private static final int LFH_FILENAME_OFFSET = 30;
  private static final int CFH_SIG_OFFSET = 0;
  private static final int CFH_VERSION_MADE_BY_OFFSET = 4;
  private static final int CFH_VERSION_NEEDED_OFFSET = 6;
  private static final int CFH_GPB_OFFSET = 8;
  private static final int CFH_METHOD_OFFSET = 10;
  private static final int CFH_TIME_OFFSET = 12;
  private static final int CFH_CRC_OFFSET = 16;
  private static final int CFH_COMPRESSED_SIZE_OFFSET = 20;
  private static final int CFH_ORIGINAL_SIZE_OFFSET = 24;
  private static final int CFH_FILENAME_LENGTH_OFFSET = 28;
  private static final int CFH_EXTRA_LENGTH_OFFSET = 30;
  private static final int CFH_COMMENT_LENGTH_OFFSET = 32;
  private static final int CFH_DISK_NUMBER_OFFSET = 34;
  private static final int CFH_INTERNAL_ATTRIBUTES_OFFSET = 36;
  private static final int CFH_EXTERNAL_ATTRIBUTES_OFFSET = 38;
  private static final int CFH_LFH_OFFSET = 42;
  private static final int CFH_FILENAME_OFFSET = 46;
  private boolean finished = false;
  private static final int DEFLATER_BLOCK_SIZE = 8192;
  public static final int DEFLATED = 8;
  public static final int DEFAULT_COMPRESSION = -1;
  public static final int STORED = 0;
  static final String DEFAULT_ENCODING = null;
  @Deprecated
  public static final int EFS_FLAG = 2048;
  private static final byte[] EMPTY = new byte[0];
  private CurrentEntry entry;
  private String comment = "";
  private int level = -1;
  private boolean hasCompressionLevelChanged = false;
  private int method = 8;
  private final List<ZipEntry> entries = new LinkedList();
  private final CRC32 crc = new CRC32();
  private long written = 0L;
  private long cdOffset = 0L;
  private long cdLength = 0L;
  private static final byte[] ZERO = { 0, 0 };
  private static final byte[] LZERO = { 0, 0, 0, 0 };
  private static final byte[] ONE = ZipLong.getBytes(1L);
  private final Map<ZipEntry, Long> offsets = new HashMap();
  private String encoding = null;
  private ZipEncoding zipEncoding = ZipEncodingHelper.getZipEncoding(DEFAULT_ENCODING);
  protected final Deflater def = new Deflater(level, true);
  protected byte[] buf = new byte['Ȁ'];
  private final RandomAccessFile raf;
  private boolean useUTF8Flag = true;
  private boolean fallbackToUTF8 = false;
  private UnicodeExtraFieldPolicy createUnicodeExtraFields = UnicodeExtraFieldPolicy.NEVER;
  private boolean hasUsedZip64 = false;
  private Zip64Mode zip64Mode = Zip64Mode.AsNeeded;
  private final Calendar calendarInstance = Calendar.getInstance();
  
  public ZipOutputStream(OutputStream out)
  {
    super(out);
    raf = null;
  }
  
  public ZipOutputStream(File file)
    throws IOException
  {
    super(null);
    RandomAccessFile ranf = null;
    try
    {
      ranf = new RandomAccessFile(file, "rw");
      ranf.setLength(0L);
    }
    catch (IOException e)
    {
      if (ranf != null)
      {
        try
        {
          ranf.close();
        }
        catch (IOException localIOException1) {}
        ranf = null;
      }
      out = Files.newOutputStream(file.toPath(), new OpenOption[0]);
    }
    raf = ranf;
  }
  
  public boolean isSeekable()
  {
    return raf != null;
  }
  
  public void setEncoding(String encoding)
  {
    this.encoding = encoding;
    zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
    if ((useUTF8Flag) && (!ZipEncodingHelper.isUTF8(encoding))) {
      useUTF8Flag = false;
    }
  }
  
  public String getEncoding()
  {
    return encoding;
  }
  
  public void setUseLanguageEncodingFlag(boolean b)
  {
    useUTF8Flag = ((b) && (ZipEncodingHelper.isUTF8(encoding)));
  }
  
  public void setCreateUnicodeExtraFields(UnicodeExtraFieldPolicy b)
  {
    createUnicodeExtraFields = b;
  }
  
  public void setFallbackToUTF8(boolean b)
  {
    fallbackToUTF8 = b;
  }
  
  public void setUseZip64(Zip64Mode mode)
  {
    zip64Mode = mode;
  }
  
  public void finish()
    throws IOException
  {
    if (finished) {
      throw new IOException("This archive has already been finished");
    }
    if (entry != null) {
      closeEntry();
    }
    cdOffset = written;
    writeCentralDirectoryInChunks();
    cdLength = (written - cdOffset);
    writeZip64CentralDirectory();
    writeCentralDirectoryEnd();
    offsets.clear();
    entries.clear();
    def.end();
    finished = true;
  }
  
  private void writeCentralDirectoryInChunks()
    throws IOException
  {
    int NUM_PER_WRITE = 1000;
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(70000);
    int count = 0;
    for (ZipEntry ze : entries)
    {
      byteArrayOutputStream.write(createCentralFileHeader(ze));
      count++;
      if (count > 1000)
      {
        writeCounted(byteArrayOutputStream.toByteArray());
        byteArrayOutputStream.reset();
        count = 0;
      }
    }
    writeCounted(byteArrayOutputStream.toByteArray());
  }
  
  public void closeEntry()
    throws IOException
  {
    preClose();
    
    flushDeflater();
    
    Zip64Mode effectiveMode = getEffectiveZip64Mode(entry.entry);
    long bytesWritten = written - entry.dataStart;
    long realCrc = crc.getValue();
    crc.reset();
    
    boolean actuallyNeedsZip64 = handleSizesAndCrc(bytesWritten, realCrc, effectiveMode);
    
    closeEntry(actuallyNeedsZip64);
  }
  
  private void closeEntry(boolean actuallyNeedsZip64)
    throws IOException
  {
    if (raf != null) {
      rewriteSizesAndCrc(actuallyNeedsZip64);
    }
    writeDataDescriptor(entry.entry);
    entry = null;
  }
  
  private void preClose()
    throws IOException
  {
    if (finished) {
      throw new IOException("Stream has already been finished");
    }
    if (entry == null) {
      throw new IOException("No current entry to close");
    }
    if (!entry.hasWritten) {
      write(EMPTY, 0, 0);
    }
  }
  
  private void flushDeflater()
    throws IOException
  {
    if (entry.entry.getMethod() == 8)
    {
      def.finish();
      while (!def.finished()) {
        deflate();
      }
    }
  }
  
  private boolean handleSizesAndCrc(long bytesWritten, long crc, Zip64Mode effectiveMode)
    throws ZipException
  {
    if (entry.entry.getMethod() == 8)
    {
      entry.entry.setSize(entry.bytesRead);
      entry.entry.setCompressedSize(bytesWritten);
      entry.entry.setCrc(crc);
      
      def.reset();
    }
    else if (raf == null)
    {
      if (entry.entry.getCrc() != crc) {
        throw new ZipException("bad CRC checksum for entry " + entry.entry.getName() + ": " + Long.toHexString(entry.entry.getCrc()) + " instead of " + Long.toHexString(crc));
      }
      if (entry.entry.getSize() != bytesWritten) {
        throw new ZipException("bad size for entry " + entry.entry.getName() + ": " + entry.entry.getSize() + " instead of " + bytesWritten);
      }
    }
    else
    {
      entry.entry.setSize(bytesWritten);
      entry.entry.setCompressedSize(bytesWritten);
      entry.entry.setCrc(crc);
    }
    return checkIfNeedsZip64(effectiveMode);
  }
  
  private boolean checkIfNeedsZip64(Zip64Mode effectiveMode)
    throws ZipException
  {
    boolean actuallyNeedsZip64 = isZip64Required(entry.entry, effectiveMode);
    if ((actuallyNeedsZip64) && (effectiveMode == Zip64Mode.Never)) {
      throw new Zip64RequiredException(Zip64RequiredException.getEntryTooBigMessage(entry.entry));
    }
    return actuallyNeedsZip64;
  }
  
  private boolean isZip64Required(ZipEntry entry1, Zip64Mode requestedMode)
  {
    return (requestedMode == Zip64Mode.Always) || (isTooLageForZip32(entry1));
  }
  
  private boolean isTooLageForZip32(ZipEntry zipArchiveEntry)
  {
    return (zipArchiveEntry.getSize() >= 4294967295L) || 
      (zipArchiveEntry.getCompressedSize() >= 4294967295L);
  }
  
  private void rewriteSizesAndCrc(boolean actuallyNeedsZip64)
    throws IOException
  {
    long save = raf.getFilePointer();
    
    raf.seek(entry.localDataStart);
    writeOut(ZipLong.getBytes(entry.entry.getCrc()));
    if ((!hasZip64Extra(entry.entry)) || (!actuallyNeedsZip64))
    {
      writeOut(ZipLong.getBytes(entry.entry.getCompressedSize()));
      writeOut(ZipLong.getBytes(entry.entry.getSize()));
    }
    else
    {
      writeOut(ZipLong.ZIP64_MAGIC.getBytes());
      writeOut(ZipLong.ZIP64_MAGIC.getBytes());
    }
    if (hasZip64Extra(entry.entry))
    {
      raf.seek(entry.localDataStart + 12L + 4L + 
        getName(entry.entry).limit() + 4L);
      
      writeOut(ZipEightByteInteger.getBytes(entry.entry.getSize()));
      writeOut(ZipEightByteInteger.getBytes(entry.entry.getCompressedSize()));
      if (!actuallyNeedsZip64)
      {
        raf.seek(entry.localDataStart - 10L);
        writeOut(ZipShort.getBytes(10));
        
        entry.entry.removeExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
        
        entry.entry.setExtra();
        if (entry.causedUseOfZip64) {
          hasUsedZip64 = false;
        }
      }
    }
    raf.seek(save);
  }
  
  public void putNextEntry(ZipEntry archiveEntry)
    throws IOException
  {
    if (finished) {
      throw new IOException("Stream has already been finished");
    }
    if (entry != null) {
      closeEntry();
    }
    entry = new CurrentEntry(archiveEntry, null);
    entries.add(entry.entry);
    
    setDefaults(entry.entry);
    
    Zip64Mode effectiveMode = getEffectiveZip64Mode(entry.entry);
    validateSizeInformation(effectiveMode);
    if (shouldAddZip64Extra(entry.entry, effectiveMode))
    {
      Zip64ExtendedInformationExtraField z64 = getZip64Extra(entry.entry);
      
      ZipEightByteInteger size = ZipEightByteInteger.ZERO;
      ZipEightByteInteger compressedSize = ZipEightByteInteger.ZERO;
      if ((entry.entry.getMethod() == 0) && 
        (entry.entry.getSize() != -1L))
      {
        size = new ZipEightByteInteger(entry.entry.getSize());
        compressedSize = size;
      }
      z64.setSize(size);
      z64.setCompressedSize(compressedSize);
      entry.entry.setExtra();
    }
    if ((entry.entry.getMethod() == 8) && (hasCompressionLevelChanged))
    {
      def.setLevel(level);
      hasCompressionLevelChanged = false;
    }
    writeLocalFileHeader(entry.entry);
  }
  
  private void setDefaults(ZipEntry entry)
  {
    if (entry.getMethod() == -1) {
      entry.setMethod(method);
    }
    if (entry.getTime() == -1L) {
      entry.setTime(System.currentTimeMillis());
    }
  }
  
  private void validateSizeInformation(Zip64Mode effectiveMode)
    throws ZipException
  {
    if ((entry.entry.getMethod() == 0) && (raf == null))
    {
      if (entry.entry.getSize() == -1L) {
        throw new ZipException("uncompressed size is required for STORED method when not writing to a file");
      }
      if (entry.entry.getCrc() == -1L) {
        throw new ZipException("crc checksum is required for STORED method when not writing to a file");
      }
      entry.entry.setCompressedSize(entry.entry.getSize());
    }
    if (((entry.entry.getSize() >= 4294967295L) || 
      (entry.entry.getCompressedSize() >= 4294967295L)) && (effectiveMode == Zip64Mode.Never)) {
      throw new Zip64RequiredException(Zip64RequiredException.getEntryTooBigMessage(entry.entry));
    }
  }
  
  private boolean shouldAddZip64Extra(ZipEntry entry, Zip64Mode mode)
  {
    return (mode == Zip64Mode.Always) || 
      (entry.getSize() >= 4294967295L) || 
      (entry.getCompressedSize() >= 4294967295L) || (
      (entry.getSize() == -1L) && (raf != null) && (mode != Zip64Mode.Never));
  }
  
  public void setComment(String comment)
  {
    this.comment = comment;
  }
  
  public void setLevel(int level)
  {
    if ((level < -1) || (level > 9)) {
      throw new IllegalArgumentException("Invalid compression level: " + level);
    }
    if (this.level == level) {
      return;
    }
    hasCompressionLevelChanged = true;
    this.level = level;
  }
  
  public void setMethod(int method)
  {
    this.method = method;
  }
  
  public boolean canWriteEntryData(ZipEntry ae)
  {
    return ZipUtil.canHandleEntryData(ae);
  }
  
  public void write(byte[] b, int offset, int length)
    throws IOException
  {
    if (entry == null) {
      throw new IllegalStateException("No current entry");
    }
    ZipUtil.checkRequestedFeatures(entry.entry);
    entry.hasWritten = true;
    if (entry.entry.getMethod() == 8) {
      writeDeflated(b, offset, length);
    } else {
      writeCounted(b, offset, length);
    }
    crc.update(b, offset, length);
  }
  
  private void writeCounted(byte[] data)
    throws IOException
  {
    writeCounted(data, 0, data.length);
  }
  
  private void writeCounted(byte[] data, int offset, int length)
    throws IOException
  {
    writeOut(data, offset, length);
    written += length;
  }
  
  private void writeDeflated(byte[] b, int offset, int length)
    throws IOException
  {
    if ((length > 0) && (!def.finished()))
    {
      CurrentEntry.access$314(entry, length);
      if (length <= 8192)
      {
        def.setInput(b, offset, length);
        deflateUntilInputIsNeeded();
      }
      else
      {
        int fullblocks = length / 8192;
        for (int i = 0; i < fullblocks; i++)
        {
          def.setInput(b, offset + i * 8192, 8192);
          
          deflateUntilInputIsNeeded();
        }
        int done = fullblocks * 8192;
        if (done < length)
        {
          def.setInput(b, offset + done, length - done);
          deflateUntilInputIsNeeded();
        }
      }
    }
  }
  
  public void close()
    throws IOException
  {
    if (!finished) {
      finish();
    }
    destroy();
  }
  
  public void flush()
    throws IOException
  {
    if (out != null) {
      out.flush();
    }
  }
  
  protected static final byte[] LFH_SIG = ZipLong.LFH_SIG.getBytes();
  protected static final byte[] DD_SIG = ZipLong.DD_SIG.getBytes();
  protected static final byte[] CFH_SIG = ZipLong.CFH_SIG.getBytes();
  protected static final byte[] EOCD_SIG = ZipLong.getBytes(101010256L);
  static final byte[] ZIP64_EOCD_SIG = ZipLong.getBytes(101075792L);
  static final byte[] ZIP64_EOCD_LOC_SIG = ZipLong.getBytes(117853008L);
  
  protected final void deflate()
    throws IOException
  {
    int len = def.deflate(buf, 0, buf.length);
    if (len > 0) {
      writeCounted(buf, 0, len);
    }
  }
  
  protected void writeLocalFileHeader(ZipEntry ze)
    throws IOException
  {
    boolean encodable = zipEncoding.canEncode(ze.getName());
    ByteBuffer name = getName(ze);
    if (createUnicodeExtraFields != UnicodeExtraFieldPolicy.NEVER) {
      addUnicodeExtraFields(ze, encodable, name);
    }
    byte[] localHeader = createLocalFileHeader(ze, name, encodable);
    long localHeaderStart = written;
    offsets.put(ze, Long.valueOf(localHeaderStart));
    entry.localDataStart = (localHeaderStart + 14L);
    writeCounted(localHeader);
    entry.dataStart = written;
  }
  
  private byte[] createLocalFileHeader(ZipEntry ze, ByteBuffer name, boolean encodable)
  {
    byte[] extra = ze.getLocalFileDataExtra();
    int nameLen = name.limit() - name.position();
    int len = 30 + nameLen + extra.length;
    byte[] buf = new byte[len];
    
    System.arraycopy(LFH_SIG, 0, buf, 0, 4);
    
    int zipMethod = ze.getMethod();
    
    ZipShort.putShort(versionNeededToExtract(zipMethod, hasZip64Extra(ze)), buf, 4);
    
    GeneralPurposeBit generalPurposeBit = getGeneralPurposeBits(zipMethod, (!encodable) && (fallbackToUTF8));
    generalPurposeBit.encode(buf, 6);
    
    ZipShort.putShort(zipMethod, buf, 8);
    
    ZipUtil.toDosTime(calendarInstance, ze.getTime(), buf, 10);
    if ((zipMethod == 8) || (raf != null)) {
      System.arraycopy(LZERO, 0, buf, 14, 4);
    } else {
      ZipLong.putLong(ze.getCrc(), buf, 14);
    }
    if (hasZip64Extra(entry.entry))
    {
      ZipLong.ZIP64_MAGIC.putLong(buf, 18);
      ZipLong.ZIP64_MAGIC.putLong(buf, 22);
    }
    else if ((zipMethod == 8) || (raf != null))
    {
      System.arraycopy(LZERO, 0, buf, 18, 4);
      System.arraycopy(LZERO, 0, buf, 22, 4);
    }
    else
    {
      ZipLong.putLong(ze.getSize(), buf, 18);
      ZipLong.putLong(ze.getSize(), buf, 22);
    }
    ZipShort.putShort(nameLen, buf, 26);
    
    ZipShort.putShort(extra.length, buf, 28);
    
    System.arraycopy(name.array(), name.arrayOffset(), buf, 30, nameLen);
    
    System.arraycopy(extra, 0, buf, 30 + nameLen, extra.length);
    return buf;
  }
  
  private void addUnicodeExtraFields(ZipEntry ze, boolean encodable, ByteBuffer name)
    throws IOException
  {
    if ((createUnicodeExtraFields == UnicodeExtraFieldPolicy.ALWAYS) || (!encodable)) {
      ze.addExtraField(new UnicodePathExtraField(ze.getName(), name
        .array(), name
        .arrayOffset(), name
        .limit() - name
        .position()));
    }
    String comm = ze.getComment();
    if ((comm == null) || (comm.isEmpty())) {
      return;
    }
    if ((createUnicodeExtraFields == UnicodeExtraFieldPolicy.ALWAYS) || 
      (!zipEncoding.canEncode(comm)))
    {
      ByteBuffer commentB = getEntryEncoding(ze).encode(comm);
      ze.addExtraField(new UnicodeCommentExtraField(comm, commentB
        .array(), commentB.arrayOffset(), commentB
        .limit() - commentB.position()));
    }
  }
  
  protected void writeDataDescriptor(ZipEntry ze)
    throws IOException
  {
    if ((ze.getMethod() != 8) || (raf != null)) {
      return;
    }
    writeCounted(DD_SIG);
    writeCounted(ZipLong.getBytes(ze.getCrc()));
    if (!hasZip64Extra(ze))
    {
      writeCounted(ZipLong.getBytes(ze.getCompressedSize()));
      writeCounted(ZipLong.getBytes(ze.getSize()));
    }
    else
    {
      writeCounted(ZipEightByteInteger.getBytes(ze.getCompressedSize()));
      writeCounted(ZipEightByteInteger.getBytes(ze.getSize()));
    }
  }
  
  protected void writeCentralFileHeader(ZipEntry ze)
    throws IOException
  {
    byte[] centralFileHeader = createCentralFileHeader(ze);
    writeCounted(centralFileHeader);
  }
  
  private byte[] createCentralFileHeader(ZipEntry ze)
    throws IOException
  {
    long lfhOffset = ((Long)offsets.get(ze)).longValue();
    
    boolean needsZip64Extra = (hasZip64Extra(ze)) || (ze.getCompressedSize() >= 4294967295L) || (ze.getSize() >= 4294967295L) || (lfhOffset >= 4294967295L);
    if ((needsZip64Extra) && (zip64Mode == Zip64Mode.Never)) {
      throw new Zip64RequiredException("archive's size exceeds the limit of 4GByte.");
    }
    handleZip64Extra(ze, lfhOffset, needsZip64Extra);
    
    return createCentralFileHeader(ze, getName(ze), lfhOffset, needsZip64Extra);
  }
  
  private byte[] createCentralFileHeader(ZipEntry ze, ByteBuffer name, long lfhOffset, boolean needsZip64Extra)
    throws IOException
  {
    byte[] extra = ze.getCentralDirectoryExtra();
    
    String comm = ze.getComment();
    if (comm == null) {
      comm = "";
    }
    ByteBuffer commentB = getEntryEncoding(ze).encode(comm);
    int nameLen = name.limit() - name.position();
    int commentLen = commentB.limit() - commentB.position();
    int len = 46 + nameLen + extra.length + commentLen;
    byte[] buf = new byte[len];
    
    System.arraycopy(CFH_SIG, 0, buf, 0, 4);
    
    ZipShort.putShort(ze.getPlatform() << 8 | (!hasUsedZip64 ? 20 : 45), buf, 4);
    
    int zipMethod = ze.getMethod();
    boolean encodable = zipEncoding.canEncode(ze.getName());
    ZipShort.putShort(versionNeededToExtract(zipMethod, needsZip64Extra), buf, 6);
    getGeneralPurposeBits(zipMethod, (!encodable) && (fallbackToUTF8)).encode(buf, 8);
    
    ZipShort.putShort(zipMethod, buf, 10);
    
    ZipUtil.toDosTime(calendarInstance, ze.getTime(), buf, 12);
    
    ZipLong.putLong(ze.getCrc(), buf, 16);
    if ((ze.getCompressedSize() >= 4294967295L) || 
      (ze.getSize() >= 4294967295L))
    {
      ZipLong.ZIP64_MAGIC.putLong(buf, 20);
      ZipLong.ZIP64_MAGIC.putLong(buf, 24);
    }
    else
    {
      ZipLong.putLong(ze.getCompressedSize(), buf, 20);
      ZipLong.putLong(ze.getSize(), buf, 24);
    }
    ZipShort.putShort(nameLen, buf, 28);
    
    ZipShort.putShort(extra.length, buf, 30);
    
    ZipShort.putShort(commentLen, buf, 32);
    
    System.arraycopy(ZERO, 0, buf, 34, 2);
    
    ZipShort.putShort(ze.getInternalAttributes(), buf, 36);
    
    ZipLong.putLong(ze.getExternalAttributes(), buf, 38);
    
    ZipLong.putLong(Math.min(lfhOffset, 4294967295L), buf, 42);
    
    System.arraycopy(name.array(), name.arrayOffset(), buf, 46, nameLen);
    
    int extraStart = 46 + nameLen;
    System.arraycopy(extra, 0, buf, extraStart, extra.length);
    
    int commentStart = extraStart + extra.length;
    
    System.arraycopy(commentB.array(), commentB.arrayOffset(), buf, commentStart, commentLen);
    return buf;
  }
  
  private void handleZip64Extra(ZipEntry ze, long lfhOffset, boolean needsZip64Extra)
  {
    if (needsZip64Extra)
    {
      Zip64ExtendedInformationExtraField z64 = getZip64Extra(ze);
      if ((ze.getCompressedSize() >= 4294967295L) || 
        (ze.getSize() >= 4294967295L))
      {
        z64.setCompressedSize(new ZipEightByteInteger(ze.getCompressedSize()));
        z64.setSize(new ZipEightByteInteger(ze.getSize()));
      }
      else
      {
        z64.setCompressedSize(null);
        z64.setSize(null);
      }
      if (lfhOffset >= 4294967295L) {
        z64.setRelativeHeaderOffset(new ZipEightByteInteger(lfhOffset));
      }
      ze.setExtra();
    }
  }
  
  protected void writeCentralDirectoryEnd()
    throws IOException
  {
    writeCounted(EOCD_SIG);
    
    writeCounted(ZERO);
    writeCounted(ZERO);
    
    int numberOfEntries = entries.size();
    if ((numberOfEntries > 65535) && (zip64Mode == Zip64Mode.Never)) {
      throw new Zip64RequiredException("archive contains more than 65535 entries.");
    }
    if ((cdOffset > 4294967295L) && (zip64Mode == Zip64Mode.Never)) {
      throw new Zip64RequiredException("archive's size exceeds the limit of 4GByte.");
    }
    byte[] num = ZipShort.getBytes(Math.min(numberOfEntries, 65535));
    
    writeCounted(num);
    writeCounted(num);
    
    writeCounted(ZipLong.getBytes(Math.min(cdLength, 4294967295L)));
    writeCounted(ZipLong.getBytes(Math.min(cdOffset, 4294967295L)));
    
    ByteBuffer data = zipEncoding.encode(comment);
    int dataLen = data.limit() - data.position();
    writeCounted(ZipShort.getBytes(dataLen));
    writeCounted(data.array(), data.arrayOffset(), dataLen);
  }
  
  @Deprecated
  protected static ZipLong toDosTime(Date time)
  {
    return ZipUtil.toDosTime(time);
  }
  
  @Deprecated
  protected static byte[] toDosTime(long t)
  {
    return ZipUtil.toDosTime(t);
  }
  
  protected byte[] getBytes(String name)
    throws ZipException
  {
    try
    {
      ByteBuffer b = ZipEncodingHelper.getZipEncoding(encoding).encode(name);
      byte[] result = new byte[b.limit()];
      System.arraycopy(b.array(), b.arrayOffset(), result, 0, result.length);
      
      return result;
    }
    catch (IOException ex)
    {
      throw new ZipException("Failed to encode name: " + ex.getMessage());
    }
  }
  
  protected void writeZip64CentralDirectory()
    throws IOException
  {
    if (zip64Mode == Zip64Mode.Never) {
      return;
    }
    if ((!hasUsedZip64) && ((cdOffset >= 4294967295L) || (cdLength >= 4294967295L) || 
    
      (entries.size() >= 65535))) {
      hasUsedZip64 = true;
    }
    if (!hasUsedZip64) {
      return;
    }
    long offset = written;
    
    writeOut(ZIP64_EOCD_SIG);
    
    writeOut(
      ZipEightByteInteger.getBytes(44L));
    
    writeOut(ZipShort.getBytes(45));
    writeOut(ZipShort.getBytes(45));
    
    writeOut(LZERO);
    writeOut(LZERO);
    
    byte[] num = ZipEightByteInteger.getBytes(entries.size());
    writeOut(num);
    writeOut(num);
    
    writeOut(ZipEightByteInteger.getBytes(cdLength));
    writeOut(ZipEightByteInteger.getBytes(cdOffset));
    
    writeOut(ZIP64_EOCD_LOC_SIG);
    
    writeOut(LZERO);
    
    writeOut(ZipEightByteInteger.getBytes(offset));
    
    writeOut(ONE);
  }
  
  protected final void writeOut(byte[] data)
    throws IOException
  {
    writeOut(data, 0, data.length);
  }
  
  protected final void writeOut(byte[] data, int offset, int length)
    throws IOException
  {
    if (raf != null) {
      raf.write(data, offset, length);
    } else {
      out.write(data, offset, length);
    }
  }
  
  @Deprecated
  protected static long adjustToLong(int i)
  {
    return ZipUtil.adjustToLong(i);
  }
  
  private void deflateUntilInputIsNeeded()
    throws IOException
  {
    while (!def.needsInput()) {
      deflate();
    }
  }
  
  private GeneralPurposeBit getGeneralPurposeBits(int zipMethod, boolean utfFallback)
  {
    GeneralPurposeBit b = new GeneralPurposeBit();
    b.useUTF8ForNames((useUTF8Flag) || (utfFallback));
    if (isDeflatedToOutputStream(zipMethod)) {
      b.useDataDescriptor(true);
    }
    return b;
  }
  
  private int versionNeededToExtract(int zipMethod, boolean zip64)
  {
    if (zip64) {
      return 45;
    }
    return isDeflatedToOutputStream(zipMethod) ? 
      20 : 10;
  }
  
  private boolean isDeflatedToOutputStream(int zipMethod)
  {
    return (zipMethod == 8) && (raf == null);
  }
  
  private Zip64ExtendedInformationExtraField getZip64Extra(ZipEntry ze)
  {
    if (entry != null) {
      entry.causedUseOfZip64 = (!hasUsedZip64);
    }
    hasUsedZip64 = true;
    
    Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField)ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
    if (z64 == null) {
      z64 = new Zip64ExtendedInformationExtraField();
    }
    ze.addAsFirstExtraField(z64);
    
    return z64;
  }
  
  private boolean hasZip64Extra(ZipEntry ze)
  {
    return ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID) != null;
  }
  
  private Zip64Mode getEffectiveZip64Mode(ZipEntry ze)
  {
    if ((zip64Mode != Zip64Mode.AsNeeded) || (raf != null) || 
    
      (ze.getMethod() != 8) || 
      (ze.getSize() != -1L)) {
      return zip64Mode;
    }
    return Zip64Mode.Never;
  }
  
  private ZipEncoding getEntryEncoding(ZipEntry ze)
  {
    boolean encodable = zipEncoding.canEncode(ze.getName());
    return (!encodable) && (fallbackToUTF8) ? 
      ZipEncodingHelper.UTF8_ZIP_ENCODING : zipEncoding;
  }
  
  private ByteBuffer getName(ZipEntry ze)
    throws IOException
  {
    return getEntryEncoding(ze).encode(ze.getName());
  }
  
  void destroy()
    throws IOException
  {
    if (raf != null) {
      raf.close();
    }
    if (out != null) {
      out.close();
    }
  }
  
  public static final class UnicodeExtraFieldPolicy
  {
    public static final UnicodeExtraFieldPolicy ALWAYS = new UnicodeExtraFieldPolicy("always");
    public static final UnicodeExtraFieldPolicy NEVER = new UnicodeExtraFieldPolicy("never");
    public static final UnicodeExtraFieldPolicy NOT_ENCODEABLE = new UnicodeExtraFieldPolicy("not encodeable");
    private final String name;
    
    private UnicodeExtraFieldPolicy(String n)
    {
      name = n;
    }
    
    public String toString()
    {
      return name;
    }
  }
  
  private static final class CurrentEntry
  {
    private final ZipEntry entry;
    
    private CurrentEntry(ZipEntry entry)
    {
      this.entry = entry;
    }
    
    private long localDataStart = 0L;
    private long dataStart = 0L;
    private long bytesRead = 0L;
    private boolean causedUseOfZip64 = false;
    private boolean hasWritten;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.ZipOutputStream
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */