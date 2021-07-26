package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

class Replace$FileInput
  implements AutoCloseable
{
  private static final int BUFF_SIZE = 4096;
  private StringBuffer outputBuffer;
  private final InputStream is;
  private Reader reader;
  private char[] buffer;
  
  /* Error */
  Replace$FileInput(Replace paramReplace, java.io.File source)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: putfield 1	org/apache/tools/ant/taskdefs/Replace$FileInput:this$0	Lorg/apache/tools/ant/taskdefs/Replace;
    //   5: aload_0
    //   6: invokespecial 7	java/lang/Object:<init>	()V
    //   9: aload_0
    //   10: new 13	java/lang/StringBuffer
    //   13: dup
    //   14: invokespecial 15	java/lang/StringBuffer:<init>	()V
    //   17: putfield 16	org/apache/tools/ant/taskdefs/Replace$FileInput:outputBuffer	Ljava/lang/StringBuffer;
    //   20: aload_0
    //   21: sipush 4096
    //   24: newarray <illegal type>
    //   26: putfield 20	org/apache/tools/ant/taskdefs/Replace$FileInput:buffer	[C
    //   29: aload_0
    //   30: aload_2
    //   31: invokevirtual 24	java/io/File:toPath	()Ljava/nio/file/Path;
    //   34: iconst_0
    //   35: anewarray 30	java/nio/file/OpenOption
    //   38: invokestatic 32	java/nio/file/Files:newInputStream	(Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Ljava/io/InputStream;
    //   41: putfield 38	org/apache/tools/ant/taskdefs/Replace$FileInput:is	Ljava/io/InputStream;
    //   44: aload_0
    //   45: new 42	java/io/BufferedReader
    //   48: dup
    //   49: aload_1
    //   50: invokestatic 44	org/apache/tools/ant/taskdefs/Replace:access$400	(Lorg/apache/tools/ant/taskdefs/Replace;)Ljava/lang/String;
    //   53: ifnull +21 -> 74
    //   56: new 50	java/io/InputStreamReader
    //   59: dup
    //   60: aload_0
    //   61: getfield 38	org/apache/tools/ant/taskdefs/Replace$FileInput:is	Ljava/io/InputStream;
    //   64: aload_1
    //   65: invokestatic 44	org/apache/tools/ant/taskdefs/Replace:access$400	(Lorg/apache/tools/ant/taskdefs/Replace;)Ljava/lang/String;
    //   68: invokespecial 52	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/lang/String;)V
    //   71: goto +14 -> 85
    //   74: new 50	java/io/InputStreamReader
    //   77: dup
    //   78: aload_0
    //   79: getfield 38	org/apache/tools/ant/taskdefs/Replace$FileInput:is	Ljava/io/InputStream;
    //   82: invokespecial 55	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   85: invokespecial 58	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   88: putfield 61	org/apache/tools/ant/taskdefs/Replace$FileInput:reader	Ljava/io/Reader;
    //   91: aload_0
    //   92: getfield 61	org/apache/tools/ant/taskdefs/Replace$FileInput:reader	Ljava/io/Reader;
    //   95: ifnonnull +30 -> 125
    //   98: aload_0
    //   99: getfield 38	org/apache/tools/ant/taskdefs/Replace$FileInput:is	Ljava/io/InputStream;
    //   102: invokevirtual 65	java/io/InputStream:close	()V
    //   105: goto +20 -> 125
    //   108: astore_3
    //   109: aload_0
    //   110: getfield 61	org/apache/tools/ant/taskdefs/Replace$FileInput:reader	Ljava/io/Reader;
    //   113: ifnonnull +10 -> 123
    //   116: aload_0
    //   117: getfield 38	org/apache/tools/ant/taskdefs/Replace$FileInput:is	Ljava/io/InputStream;
    //   120: invokevirtual 65	java/io/InputStream:close	()V
    //   123: aload_3
    //   124: athrow
    //   125: return
    // Line number table:
    //   Java source line #365	-> byte code offset #0
    //   Java source line #366	-> byte code offset #9
    //   Java source line #367	-> byte code offset #20
    //   Java source line #368	-> byte code offset #29
    //   Java source line #370	-> byte code offset #44
    //   Java source line #371	-> byte code offset #49
    //   Java source line #372	-> byte code offset #74
    //   Java source line #374	-> byte code offset #91
    //   Java source line #375	-> byte code offset #98
    //   Java source line #374	-> byte code offset #108
    //   Java source line #375	-> byte code offset #116
    //   Java source line #377	-> byte code offset #123
    //   Java source line #378	-> byte code offset #125
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	126	0	this	FileInput
    //   0	126	1	paramReplace	Replace
    //   0	126	2	source	java.io.File
    //   108	16	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   44	91	108	finally
  }
  
  StringBuffer getOutputBuffer()
  {
    return outputBuffer;
  }
  
  boolean readChunk()
    throws IOException
  {
    int bufferLength = reader.read(buffer);
    if (bufferLength < 0) {
      return false;
    }
    outputBuffer.append(new String(buffer, 0, bufferLength));
    return true;
  }
  
  public void close()
    throws IOException
  {
    is.close();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Replace.FileInput
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */