package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.OutputStream;

public class TeeOutputStream
  extends OutputStream
{
  private OutputStream left;
  private OutputStream right;
  
  public TeeOutputStream(OutputStream left, OutputStream right)
  {
    this.left = left;
    this.right = right;
  }
  
  /* Error */
  public void close()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 7	org/apache/tools/ant/util/TeeOutputStream:left	Ljava/io/OutputStream;
    //   4: invokevirtual 16	java/io/OutputStream:close	()V
    //   7: aload_0
    //   8: getfield 13	org/apache/tools/ant/util/TeeOutputStream:right	Ljava/io/OutputStream;
    //   11: invokevirtual 16	java/io/OutputStream:close	()V
    //   14: goto +13 -> 27
    //   17: astore_1
    //   18: aload_0
    //   19: getfield 13	org/apache/tools/ant/util/TeeOutputStream:right	Ljava/io/OutputStream;
    //   22: invokevirtual 16	java/io/OutputStream:close	()V
    //   25: aload_1
    //   26: athrow
    //   27: return
    // Line number table:
    //   Java source line #49	-> byte code offset #0
    //   Java source line #51	-> byte code offset #7
    //   Java source line #52	-> byte code offset #14
    //   Java source line #51	-> byte code offset #17
    //   Java source line #52	-> byte code offset #25
    //   Java source line #53	-> byte code offset #27
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	28	0	this	TeeOutputStream
    //   17	9	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	7	17	finally
  }
  
  public void flush()
    throws IOException
  {
    left.flush();
    right.flush();
  }
  
  public void write(byte[] b)
    throws IOException
  {
    left.write(b);
    right.write(b);
  }
  
  public void write(byte[] b, int off, int len)
    throws IOException
  {
    left.write(b, off, len);
    right.write(b, off, len);
  }
  
  public void write(int b)
    throws IOException
  {
    left.write(b);
    right.write(b);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.TeeOutputStream
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */