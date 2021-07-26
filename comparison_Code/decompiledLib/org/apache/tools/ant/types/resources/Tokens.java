package org.apache.tools.ant.types.resources;

import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.util.Tokenizer;

public class Tokens
  extends BaseResourceCollectionWrapper
{
  private Tokenizer tokenizer;
  private String encoding;
  
  /* Error */
  protected synchronized java.util.Collection<org.apache.tools.ant.types.Resource> getCollection()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 7	org/apache/tools/ant/types/resources/Tokens:getResourceCollection	()Lorg/apache/tools/ant/types/ResourceCollection;
    //   4: astore_1
    //   5: aload_1
    //   6: invokeinterface 13 1 0
    //   11: ifeq +7 -> 18
    //   14: invokestatic 19	java/util/Collections:emptySet	()Ljava/util/Set;
    //   17: areturn
    //   18: aload_0
    //   19: getfield 25	org/apache/tools/ant/types/resources/Tokens:tokenizer	Lorg/apache/tools/ant/util/Tokenizer;
    //   22: ifnonnull +14 -> 36
    //   25: aload_0
    //   26: new 29	org/apache/tools/ant/util/LineTokenizer
    //   29: dup
    //   30: invokespecial 31	org/apache/tools/ant/util/LineTokenizer:<init>	()V
    //   33: putfield 25	org/apache/tools/ant/types/resources/Tokens:tokenizer	Lorg/apache/tools/ant/util/Tokenizer;
    //   36: new 32	org/apache/tools/ant/util/ConcatResourceInputStream
    //   39: dup
    //   40: aload_1
    //   41: invokespecial 34	org/apache/tools/ant/util/ConcatResourceInputStream:<init>	(Lorg/apache/tools/ant/types/ResourceCollection;)V
    //   44: astore_2
    //   45: new 37	java/io/InputStreamReader
    //   48: dup
    //   49: aload_2
    //   50: aload_0
    //   51: getfield 39	org/apache/tools/ant/types/resources/Tokens:encoding	Ljava/lang/String;
    //   54: ifnonnull +9 -> 63
    //   57: invokestatic 43	java/nio/charset/Charset:defaultCharset	()Ljava/nio/charset/Charset;
    //   60: goto +10 -> 70
    //   63: aload_0
    //   64: getfield 39	org/apache/tools/ant/types/resources/Tokens:encoding	Ljava/lang/String;
    //   67: invokestatic 49	java/nio/charset/Charset:forName	(Ljava/lang/String;)Ljava/nio/charset/Charset;
    //   70: invokespecial 53	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/nio/charset/Charset;)V
    //   73: astore_3
    //   74: aload_2
    //   75: aload_0
    //   76: invokevirtual 56	org/apache/tools/ant/util/ConcatResourceInputStream:setManagingComponent	(Lorg/apache/tools/ant/ProjectComponent;)V
    //   79: new 60	java/util/ArrayList
    //   82: dup
    //   83: invokespecial 62	java/util/ArrayList:<init>	()V
    //   86: astore 4
    //   88: aload_0
    //   89: getfield 25	org/apache/tools/ant/types/resources/Tokens:tokenizer	Lorg/apache/tools/ant/util/Tokenizer;
    //   92: aload_3
    //   93: invokeinterface 63 2 0
    //   98: astore 5
    //   100: aload 5
    //   102: ifnull +48 -> 150
    //   105: new 69	org/apache/tools/ant/types/resources/StringResource
    //   108: dup
    //   109: aload 5
    //   111: invokespecial 71	org/apache/tools/ant/types/resources/StringResource:<init>	(Ljava/lang/String;)V
    //   114: astore 6
    //   116: aload 6
    //   118: aload_0
    //   119: invokevirtual 74	org/apache/tools/ant/types/resources/Tokens:getProject	()Lorg/apache/tools/ant/Project;
    //   122: invokevirtual 78	org/apache/tools/ant/types/resources/StringResource:setProject	(Lorg/apache/tools/ant/Project;)V
    //   125: aload 4
    //   127: aload 6
    //   129: invokeinterface 82 2 0
    //   134: pop
    //   135: aload_0
    //   136: getfield 25	org/apache/tools/ant/types/resources/Tokens:tokenizer	Lorg/apache/tools/ant/util/Tokenizer;
    //   139: aload_3
    //   140: invokeinterface 63 2 0
    //   145: astore 5
    //   147: goto -47 -> 100
    //   150: aload 4
    //   152: astore 5
    //   154: aload_3
    //   155: invokevirtual 88	java/io/InputStreamReader:close	()V
    //   158: aload_2
    //   159: invokevirtual 91	org/apache/tools/ant/util/ConcatResourceInputStream:close	()V
    //   162: aload 5
    //   164: areturn
    //   165: astore 4
    //   167: aload_3
    //   168: invokevirtual 88	java/io/InputStreamReader:close	()V
    //   171: goto +12 -> 183
    //   174: astore 5
    //   176: aload 4
    //   178: aload 5
    //   180: invokevirtual 94	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   183: aload 4
    //   185: athrow
    //   186: astore_3
    //   187: aload_2
    //   188: invokevirtual 91	org/apache/tools/ant/util/ConcatResourceInputStream:close	()V
    //   191: goto +11 -> 202
    //   194: astore 4
    //   196: aload_3
    //   197: aload 4
    //   199: invokevirtual 94	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   202: aload_3
    //   203: athrow
    //   204: astore_2
    //   205: new 100	org/apache/tools/ant/BuildException
    //   208: dup
    //   209: ldc 102
    //   211: aload_2
    //   212: invokespecial 104	org/apache/tools/ant/BuildException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   215: athrow
    // Line number table:
    //   Java source line #53	-> byte code offset #0
    //   Java source line #54	-> byte code offset #5
    //   Java source line #55	-> byte code offset #14
    //   Java source line #57	-> byte code offset #18
    //   Java source line #58	-> byte code offset #25
    //   Java source line #60	-> byte code offset #36
    //   Java source line #61	-> byte code offset #45
    //   Java source line #62	-> byte code offset #50
    //   Java source line #63	-> byte code offset #63
    //   Java source line #64	-> byte code offset #74
    //   Java source line #65	-> byte code offset #79
    //   Java source line #66	-> byte code offset #88
    //   Java source line #72	-> byte code offset #105
    //   Java source line #73	-> byte code offset #116
    //   Java source line #74	-> byte code offset #125
    //   Java source line #66	-> byte code offset #135
    //   Java source line #67	-> byte code offset #140
    //   Java source line #76	-> byte code offset #150
    //   Java source line #77	-> byte code offset #154
    //   Java source line #76	-> byte code offset #162
    //   Java source line #60	-> byte code offset #165
    //   Java source line #77	-> byte code offset #204
    //   Java source line #78	-> byte code offset #205
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	216	0	this	Tokens
    //   4	37	1	rc	org.apache.tools.ant.types.ResourceCollection
    //   44	144	2	cat	org.apache.tools.ant.util.ConcatResourceInputStream
    //   204	8	2	e	java.io.IOException
    //   73	95	3	rdr	java.io.InputStreamReader
    //   186	17	3	localThrowable2	Throwable
    //   86	65	4	result	java.util.List<org.apache.tools.ant.types.Resource>
    //   165	19	4	localThrowable	Throwable
    //   194	4	4	localThrowable3	Throwable
    //   98	65	5	s	String
    //   174	5	5	localThrowable1	Throwable
    //   114	14	6	resource	StringResource
    // Exception table:
    //   from	to	target	type
    //   74	154	165	java/lang/Throwable
    //   167	171	174	java/lang/Throwable
    //   45	158	186	java/lang/Throwable
    //   165	186	186	java/lang/Throwable
    //   187	191	194	java/lang/Throwable
    //   36	162	204	java/io/IOException
    //   165	204	204	java/io/IOException
  }
  
  public synchronized void setEncoding(String encoding)
  {
    this.encoding = encoding;
  }
  
  public synchronized void add(Tokenizer tokenizer)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    if (this.tokenizer != null) {
      throw new BuildException("Only one nested tokenizer allowed.");
    }
    this.tokenizer = tokenizer;
    setChecked(false);
  }
  
  protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p)
    throws BuildException
  {
    if (isChecked()) {
      return;
    }
    super.dieOnCircularReference(stk, p);
    if (!isReference())
    {
      if ((tokenizer instanceof DataType)) {
        pushAndInvokeCircularReferenceCheck((DataType)tokenizer, stk, p);
      }
      setChecked(true);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.Tokens
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */