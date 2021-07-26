package org.apache.tools.tar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class TarBuffer
{
  public static final int DEFAULT_RCDSIZE = 512;
  public static final int DEFAULT_BLKSIZE = 10240;
  private InputStream inStream;
  private OutputStream outStream;
  private final int blockSize;
  private final int recordSize;
  private final int recsPerBlock;
  private final byte[] blockBuffer;
  private int currBlkIdx;
  private int currRecIdx;
  private boolean debug;
  
  public TarBuffer(InputStream inStream)
  {
    this(inStream, 10240);
  }
  
  public TarBuffer(InputStream inStream, int blockSize)
  {
    this(inStream, blockSize, 512);
  }
  
  public TarBuffer(InputStream inStream, int blockSize, int recordSize)
  {
    this(inStream, null, blockSize, recordSize);
  }
  
  public TarBuffer(OutputStream outStream)
  {
    this(outStream, 10240);
  }
  
  public TarBuffer(OutputStream outStream, int blockSize)
  {
    this(outStream, blockSize, 512);
  }
  
  public TarBuffer(OutputStream outStream, int blockSize, int recordSize)
  {
    this(null, outStream, blockSize, recordSize);
  }
  
  private TarBuffer(InputStream inStream, OutputStream outStream, int blockSize, int recordSize)
  {
    this.inStream = inStream;
    this.outStream = outStream;
    debug = false;
    this.blockSize = blockSize;
    this.recordSize = recordSize;
    recsPerBlock = (this.blockSize / this.recordSize);
    blockBuffer = new byte[this.blockSize];
    if (this.inStream != null)
    {
      currBlkIdx = -1;
      currRecIdx = recsPerBlock;
    }
    else
    {
      currBlkIdx = 0;
      currRecIdx = 0;
    }
  }
  
  public int getBlockSize()
  {
    return blockSize;
  }
  
  public int getRecordSize()
  {
    return recordSize;
  }
  
  public void setDebug(boolean debug)
  {
    this.debug = debug;
  }
  
  public boolean isEOFRecord(byte[] record)
  {
    int i = 0;
    for (int sz = getRecordSize(); i < sz; i++) {
      if (record[i] != 0) {
        return false;
      }
    }
    return true;
  }
  
  public void skipRecord()
    throws IOException
  {
    if (debug) {
      System.err.println("SkipRecord: recIdx = " + currRecIdx + " blkIdx = " + currBlkIdx);
    }
    if (inStream == null) {
      throw new IOException("reading (via skip) from an output buffer");
    }
    if ((currRecIdx >= recsPerBlock) && (!readBlock())) {
      return;
    }
    currRecIdx += 1;
  }
  
  public byte[] readRecord()
    throws IOException
  {
    if (debug) {
      System.err.println("ReadRecord: recIdx = " + currRecIdx + " blkIdx = " + currBlkIdx);
    }
    if (inStream == null)
    {
      if (outStream == null) {
        throw new IOException("input buffer is closed");
      }
      throw new IOException("reading from an output buffer");
    }
    if ((currRecIdx >= recsPerBlock) && (!readBlock())) {
      return null;
    }
    byte[] result = new byte[recordSize];
    
    System.arraycopy(blockBuffer, currRecIdx * recordSize, result, 0, recordSize);
    
    currRecIdx += 1;
    
    return result;
  }
  
  private boolean readBlock()
    throws IOException
  {
    if (debug) {
      System.err.println("ReadBlock: blkIdx = " + currBlkIdx);
    }
    if (inStream == null) {
      throw new IOException("reading from an output buffer");
    }
    currRecIdx = 0;
    
    int offset = 0;
    int bytesNeeded = blockSize;
    while (bytesNeeded > 0)
    {
      long numBytes = inStream.read(blockBuffer, offset, bytesNeeded);
      if (numBytes == -1L)
      {
        if (offset == 0) {
          return false;
        }
        Arrays.fill(blockBuffer, offset, offset + bytesNeeded, (byte)0);
        
        break;
      }
      offset = (int)(offset + numBytes);
      bytesNeeded = (int)(bytesNeeded - numBytes);
      if ((numBytes != blockSize) && 
        (debug)) {
        System.err.println("ReadBlock: INCOMPLETE READ " + numBytes + " of " + blockSize + " bytes read.");
      }
    }
    currBlkIdx += 1;
    
    return true;
  }
  
  public int getCurrentBlockNum()
  {
    return currBlkIdx;
  }
  
  public int getCurrentRecordNum()
  {
    return currRecIdx - 1;
  }
  
  public void writeRecord(byte[] record)
    throws IOException
  {
    if (debug) {
      System.err.println("WriteRecord: recIdx = " + currRecIdx + " blkIdx = " + currBlkIdx);
    }
    if (outStream == null)
    {
      if (inStream == null) {
        throw new IOException("Output buffer is closed");
      }
      throw new IOException("writing to an input buffer");
    }
    if (record.length != recordSize) {
      throw new IOException("record to write has length '" + record.length + "' which is not the record size of '" + recordSize + "'");
    }
    if (currRecIdx >= recsPerBlock) {
      writeBlock();
    }
    System.arraycopy(record, 0, blockBuffer, currRecIdx * recordSize, recordSize);
    
    currRecIdx += 1;
  }
  
  public void writeRecord(byte[] buf, int offset)
    throws IOException
  {
    if (debug) {
      System.err.println("WriteRecord: recIdx = " + currRecIdx + " blkIdx = " + currBlkIdx);
    }
    if (outStream == null)
    {
      if (inStream == null) {
        throw new IOException("Output buffer is closed");
      }
      throw new IOException("writing to an input buffer");
    }
    if (offset + recordSize > buf.length) {
      throw new IOException("record has length '" + buf.length + "' with offset '" + offset + "' which is less than the record size of '" + recordSize + "'");
    }
    if (currRecIdx >= recsPerBlock) {
      writeBlock();
    }
    System.arraycopy(buf, offset, blockBuffer, currRecIdx * recordSize, recordSize);
    
    currRecIdx += 1;
  }
  
  private void writeBlock()
    throws IOException
  {
    if (debug) {
      System.err.println("WriteBlock: blkIdx = " + currBlkIdx);
    }
    if (outStream == null) {
      throw new IOException("writing to an input buffer");
    }
    outStream.write(blockBuffer, 0, blockSize);
    outStream.flush();
    
    currRecIdx = 0;
    currBlkIdx += 1;
    Arrays.fill(blockBuffer, (byte)0);
  }
  
  void flushBlock()
    throws IOException
  {
    if (debug) {
      System.err.println("TarBuffer.flushBlock() called.");
    }
    if (outStream == null) {
      throw new IOException("writing to an input buffer");
    }
    if (currRecIdx > 0) {
      writeBlock();
    }
  }
  
  public void close()
    throws IOException
  {
    if (debug) {
      System.err.println("TarBuffer.closeBuffer().");
    }
    if (outStream != null)
    {
      flushBlock();
      if ((outStream != System.out) && (outStream != System.err))
      {
        outStream.close();
        
        outStream = null;
      }
    }
    else if (inStream != null)
    {
      if (inStream != System.in) {
        inStream.close();
      }
      inStream = null;
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.tar.TarBuffer
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */