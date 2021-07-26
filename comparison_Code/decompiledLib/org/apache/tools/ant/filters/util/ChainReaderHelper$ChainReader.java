package org.apache.tools.ant.filters.util;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import org.apache.tools.ant.AntClassLoader;

public class ChainReaderHelper$ChainReader
  extends FilterReader
{
  private List<AntClassLoader> cleanupLoaders;
  
  private ChainReaderHelper$ChainReader(Reader this$0, List<AntClassLoader> in)
  {
    super(in);
    this.cleanupLoaders = cleanupLoaders;
  }
  
  public String readFully()
    throws IOException
  {
    return this$0.readFully(this);
  }
  
  public void close()
    throws IOException
  {
    ChainReaderHelper.access$000(cleanupLoaders);
    super.close();
  }
  
  /* Error */
  protected void finalize()
    throws java.lang.Throwable
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 34	org/apache/tools/ant/filters/util/ChainReaderHelper$ChainReader:close	()V
    //   4: aload_0
    //   5: invokespecial 35	java/lang/Object:finalize	()V
    //   8: goto +10 -> 18
    //   11: astore_1
    //   12: aload_0
    //   13: invokespecial 35	java/lang/Object:finalize	()V
    //   16: aload_1
    //   17: athrow
    //   18: return
    // Line number table:
    //   Java source line #75	-> byte code offset #0
    //   Java source line #77	-> byte code offset #4
    //   Java source line #78	-> byte code offset #8
    //   Java source line #77	-> byte code offset #11
    //   Java source line #78	-> byte code offset #16
    //   Java source line #79	-> byte code offset #18
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	ChainReader
    //   11	6	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	4	11	finally
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.util.ChainReaderHelper.ChainReader
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */