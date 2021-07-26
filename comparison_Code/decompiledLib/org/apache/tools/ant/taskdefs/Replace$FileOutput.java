package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

class Replace$FileOutput
  implements AutoCloseable
{
  private StringBuffer inputBuffer;
  private final OutputStream os;
  private Writer writer;
  
  /* Error */
  Replace$FileOutput(Replace paramReplace, java.io.File out)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: putfield 1	org/apache/tools/ant/taskdefs/Replace$FileOutput:this$0	Lorg/apache/tools/ant/taskdefs/Replace;
    //   5: aload_0
    //   6: invokespecial 7	java/lang/Object:<init>	()V
    //   9: aload_0
    //   10: aload_2
    //   11: invokevirtual 13	java/io/File:toPath	()Ljava/nio/file/Path;
    //   14: iconst_0
    //   15: anewarray 19	java/nio/file/OpenOption
    //   18: invokestatic 21	java/nio/file/Files:newOutputStream	(Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Ljava/io/OutputStream;
    //   21: putfield 27	org/apache/tools/ant/taskdefs/Replace$FileOutput:os	Ljava/io/OutputStream;
    //   24: aload_0
    //   25: new 31	java/io/BufferedWriter
    //   28: dup
    //   29: aload_1
    //   30: invokestatic 33	org/apache/tools/ant/taskdefs/Replace:access$400	(Lorg/apache/tools/ant/taskdefs/Replace;)Ljava/lang/String;
    //   33: ifnull +21 -> 54
    //   36: new 39	java/io/OutputStreamWriter
    //   39: dup
    //   40: aload_0
    //   41: getfield 27	org/apache/tools/ant/taskdefs/Replace$FileOutput:os	Ljava/io/OutputStream;
    //   44: aload_1
    //   45: invokestatic 33	org/apache/tools/ant/taskdefs/Replace:access$400	(Lorg/apache/tools/ant/taskdefs/Replace;)Ljava/lang/String;
    //   48: invokespecial 41	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;Ljava/lang/String;)V
    //   51: goto +14 -> 65
    //   54: new 39	java/io/OutputStreamWriter
    //   57: dup
    //   58: aload_0
    //   59: getfield 27	org/apache/tools/ant/taskdefs/Replace$FileOutput:os	Ljava/io/OutputStream;
    //   62: invokespecial 44	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;)V
    //   65: invokespecial 47	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   68: putfield 50	org/apache/tools/ant/taskdefs/Replace$FileOutput:writer	Ljava/io/Writer;
    //   71: aload_0
    //   72: getfield 50	org/apache/tools/ant/taskdefs/Replace$FileOutput:writer	Ljava/io/Writer;
    //   75: ifnonnull +30 -> 105
    //   78: aload_0
    //   79: getfield 27	org/apache/tools/ant/taskdefs/Replace$FileOutput:os	Ljava/io/OutputStream;
    //   82: invokevirtual 54	java/io/OutputStream:close	()V
    //   85: goto +20 -> 105
    //   88: astore_3
    //   89: aload_0
    //   90: getfield 50	org/apache/tools/ant/taskdefs/Replace$FileOutput:writer	Ljava/io/Writer;
    //   93: ifnonnull +10 -> 103
    //   96: aload_0
    //   97: getfield 27	org/apache/tools/ant/taskdefs/Replace$FileOutput:os	Ljava/io/OutputStream;
    //   100: invokevirtual 54	java/io/OutputStream:close	()V
    //   103: aload_3
    //   104: athrow
    //   105: return
    // Line number table:
    //   Java source line #429	-> byte code offset #0
    //   Java source line #430	-> byte code offset #9
    //   Java source line #432	-> byte code offset #24
    //   Java source line #433	-> byte code offset #29
    //   Java source line #434	-> byte code offset #54
    //   Java source line #436	-> byte code offset #71
    //   Java source line #437	-> byte code offset #78
    //   Java source line #436	-> byte code offset #88
    //   Java source line #437	-> byte code offset #96
    //   Java source line #439	-> byte code offset #103
    //   Java source line #440	-> byte code offset #105
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	106	0	this	FileOutput
    //   0	106	1	paramReplace	Replace
    //   0	106	2	out	java.io.File
    //   88	16	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   24	71	88	finally
  }
  
  void setInputBuffer(StringBuffer input)
  {
    inputBuffer = input;
  }
  
  boolean process()
    throws IOException
  {
    writer.write(inputBuffer.toString());
    inputBuffer.delete(0, inputBuffer.length());
    return false;
  }
  
  void flush()
    throws IOException
  {
    process();
    writer.flush();
  }
  
  public void close()
    throws IOException
  {
    os.close();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Replace.FileOutput
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */