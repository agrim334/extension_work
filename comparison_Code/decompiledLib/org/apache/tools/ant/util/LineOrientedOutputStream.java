package org.apache.tools.ant.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class LineOrientedOutputStream
  extends OutputStream
{
  private static final int INITIAL_SIZE = 132;
  private static final int CR = 13;
  private static final int LF = 10;
  private ByteArrayOutputStream buffer = new ByteArrayOutputStream(132);
  private boolean skip = false;
  
  public final void write(int cc)
    throws IOException
  {
    byte c = (byte)cc;
    if ((c == 10) || (c == 13))
    {
      if (!skip) {
        processBuffer();
      }
    }
    else {
      buffer.write(cc);
    }
    skip = (c == 13);
  }
  
  public void flush()
    throws IOException
  {}
  
  /* Error */
  protected void processBuffer()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_0
    //   2: getfield 14	org/apache/tools/ant/util/LineOrientedOutputStream:buffer	Ljava/io/ByteArrayOutputStream;
    //   5: invokevirtual 28	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   8: invokevirtual 32	org/apache/tools/ant/util/LineOrientedOutputStream:processLine	([B)V
    //   11: aload_0
    //   12: getfield 14	org/apache/tools/ant/util/LineOrientedOutputStream:buffer	Ljava/io/ByteArrayOutputStream;
    //   15: invokevirtual 36	java/io/ByteArrayOutputStream:reset	()V
    //   18: goto +13 -> 31
    //   21: astore_1
    //   22: aload_0
    //   23: getfield 14	org/apache/tools/ant/util/LineOrientedOutputStream:buffer	Ljava/io/ByteArrayOutputStream;
    //   26: invokevirtual 36	java/io/ByteArrayOutputStream:reset	()V
    //   29: aload_1
    //   30: athrow
    //   31: return
    // Line number table:
    //   Java source line #81	-> byte code offset #0
    //   Java source line #83	-> byte code offset #11
    //   Java source line #84	-> byte code offset #18
    //   Java source line #83	-> byte code offset #21
    //   Java source line #84	-> byte code offset #29
    //   Java source line #85	-> byte code offset #31
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	32	0	this	LineOrientedOutputStream
    //   21	9	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	11	21	finally
  }
  
  protected abstract void processLine(String paramString)
    throws IOException;
  
  protected void processLine(byte[] line)
    throws IOException
  {
    processLine(new String(line));
  }
  
  public void close()
    throws IOException
  {
    if (buffer.size() > 0) {
      processBuffer();
    }
    super.close();
  }
  
  public final void write(byte[] b, int off, int len)
    throws IOException
  {
    int offset = off;
    int blockStartOffset = offset;
    int remaining = len;
    while (remaining > 0)
    {
      while ((remaining > 0) && (b[offset] != 10) && (b[offset] != 13))
      {
        offset++;
        remaining--;
      }
      int blockLength = offset - blockStartOffset;
      if (blockLength > 0) {
        buffer.write(b, blockStartOffset, blockLength);
      }
      while ((remaining > 0) && ((b[offset] == 10) || (b[offset] == 13)))
      {
        write(b[offset]);
        offset++;
        remaining--;
      }
      blockStartOffset = offset;
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.LineOrientedOutputStream
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */