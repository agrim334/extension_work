package org.apache.tools.ant.types.resources;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tools.ant.util.FileUtils;

class AbstractClasspathResource$1
  extends FilterInputStream
{
  AbstractClasspathResource$1(AbstractClasspathResource this$0, InputStream arg0, AbstractClasspathResource.ClassLoaderWithFlag paramClassLoaderWithFlag)
  {
    super(arg0);
  }
  
  public void close()
    throws IOException
  {
    FileUtils.close(in);
    val$classLoader.cleanup();
  }
  
  /* Error */
  protected void finalize()
    throws java.lang.Throwable
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 32	org/apache/tools/ant/types/resources/AbstractClasspathResource$1:close	()V
    //   4: aload_0
    //   5: invokespecial 34	java/lang/Object:finalize	()V
    //   8: goto +10 -> 18
    //   11: astore_1
    //   12: aload_0
    //   13: invokespecial 34	java/lang/Object:finalize	()V
    //   16: aload_1
    //   17: athrow
    //   18: return
    // Line number table:
    //   Java source line #189	-> byte code offset #0
    //   Java source line #191	-> byte code offset #4
    //   Java source line #192	-> byte code offset #8
    //   Java source line #191	-> byte code offset #11
    //   Java source line #192	-> byte code offset #16
    //   Java source line #193	-> byte code offset #18
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	1
    //   11	6	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	4	11	finally
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.AbstractClasspathResource.1
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */