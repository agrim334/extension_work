package org.apache.tools.ant.taskdefs.compilers;

@Deprecated
public class Javac12
  extends DefaultCompilerAdapter
{
  protected static final String CLASSIC_COMPILER_CLASSNAME = "sun.tools.javac.Main";
  
  /* Error */
  public boolean execute()
    throws org.apache.tools.ant.BuildException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 7	org/apache/tools/ant/taskdefs/compilers/Javac12:attributes	Lorg/apache/tools/ant/taskdefs/Javac;
    //   4: ldc 13
    //   6: iconst_3
    //   7: invokevirtual 17	org/apache/tools/ant/taskdefs/Javac:log	(Ljava/lang/String;I)V
    //   10: aload_0
    //   11: iconst_1
    //   12: invokevirtual 23	org/apache/tools/ant/taskdefs/compilers/Javac12:setupJavacCommand	(Z)Lorg/apache/tools/ant/types/Commandline;
    //   15: astore_1
    //   16: new 27	org/apache/tools/ant/taskdefs/LogOutputStream
    //   19: dup
    //   20: aload_0
    //   21: getfield 7	org/apache/tools/ant/taskdefs/compilers/Javac12:attributes	Lorg/apache/tools/ant/taskdefs/Javac;
    //   24: iconst_1
    //   25: invokespecial 29	org/apache/tools/ant/taskdefs/LogOutputStream:<init>	(Lorg/apache/tools/ant/Task;I)V
    //   28: astore_2
    //   29: ldc 32
    //   31: invokestatic 34	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   34: astore_3
    //   35: aload_3
    //   36: iconst_2
    //   37: anewarray 35	java/lang/Class
    //   40: dup
    //   41: iconst_0
    //   42: ldc 40
    //   44: aastore
    //   45: dup
    //   46: iconst_1
    //   47: ldc 42
    //   49: aastore
    //   50: invokevirtual 44	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   53: astore 4
    //   55: aload 4
    //   57: iconst_2
    //   58: anewarray 48	java/lang/Object
    //   61: dup
    //   62: iconst_0
    //   63: aload_2
    //   64: aastore
    //   65: dup
    //   66: iconst_1
    //   67: ldc 50
    //   69: aastore
    //   70: invokevirtual 52	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   73: astore 5
    //   75: aload_3
    //   76: ldc 58
    //   78: iconst_1
    //   79: anewarray 35	java/lang/Class
    //   82: dup
    //   83: iconst_0
    //   84: ldc 60
    //   86: aastore
    //   87: invokevirtual 62	java/lang/Class:getMethod	(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   90: astore 6
    //   92: aload 6
    //   94: aload 5
    //   96: iconst_1
    //   97: anewarray 48	java/lang/Object
    //   100: dup
    //   101: iconst_0
    //   102: aload_1
    //   103: invokevirtual 66	org/apache/tools/ant/types/Commandline:getArguments	()[Ljava/lang/String;
    //   106: aastore
    //   107: invokevirtual 72	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   110: checkcast 78	java/lang/Boolean
    //   113: invokevirtual 80	java/lang/Boolean:booleanValue	()Z
    //   116: istore 7
    //   118: aload_2
    //   119: invokevirtual 84	java/io/OutputStream:close	()V
    //   122: iload 7
    //   124: ireturn
    //   125: astore_3
    //   126: aload_2
    //   127: invokevirtual 84	java/io/OutputStream:close	()V
    //   130: goto +11 -> 141
    //   133: astore 4
    //   135: aload_3
    //   136: aload 4
    //   138: invokevirtual 89	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   141: aload_3
    //   142: athrow
    //   143: astore_2
    //   144: new 95	org/apache/tools/ant/BuildException
    //   147: dup
    //   148: new 97	java/lang/StringBuilder
    //   151: dup
    //   152: invokespecial 99	java/lang/StringBuilder:<init>	()V
    //   155: ldc 100
    //   157: invokevirtual 102	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   160: invokestatic 106	org/apache/tools/ant/util/JavaEnvUtils:getJavaHome	()Ljava/lang/String;
    //   163: invokevirtual 102	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   166: ldc 112
    //   168: invokevirtual 102	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   171: invokevirtual 114	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   174: aload_0
    //   175: getfield 117	org/apache/tools/ant/taskdefs/compilers/Javac12:location	Lorg/apache/tools/ant/Location;
    //   178: invokespecial 121	org/apache/tools/ant/BuildException:<init>	(Ljava/lang/String;Lorg/apache/tools/ant/Location;)V
    //   181: athrow
    //   182: astore_2
    //   183: aload_2
    //   184: instanceof 95
    //   187: ifeq +8 -> 195
    //   190: aload_2
    //   191: checkcast 95	org/apache/tools/ant/BuildException
    //   194: athrow
    //   195: new 95	org/apache/tools/ant/BuildException
    //   198: dup
    //   199: ldc 126
    //   201: aload_2
    //   202: aload_0
    //   203: getfield 117	org/apache/tools/ant/taskdefs/compilers/Javac12:location	Lorg/apache/tools/ant/Location;
    //   206: invokespecial 128	org/apache/tools/ant/BuildException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;Lorg/apache/tools/ant/Location;)V
    //   209: athrow
    // Line number table:
    //   Java source line #49	-> byte code offset #0
    //   Java source line #50	-> byte code offset #10
    //   Java source line #52	-> byte code offset #16
    //   Java source line #55	-> byte code offset #29
    //   Java source line #56	-> byte code offset #35
    //   Java source line #57	-> byte code offset #55
    //   Java source line #60	-> byte code offset #75
    //   Java source line #61	-> byte code offset #92
    //   Java source line #62	-> byte code offset #118
    //   Java source line #61	-> byte code offset #122
    //   Java source line #52	-> byte code offset #125
    //   Java source line #62	-> byte code offset #143
    //   Java source line #63	-> byte code offset #144
    //   Java source line #69	-> byte code offset #160
    //   Java source line #72	-> byte code offset #182
    //   Java source line #73	-> byte code offset #183
    //   Java source line #74	-> byte code offset #190
    //   Java source line #76	-> byte code offset #195
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	210	0	this	Javac12
    //   15	88	1	cmd	org.apache.tools.ant.types.Commandline
    //   28	99	2	logstr	java.io.OutputStream
    //   143	2	2	ex	ClassNotFoundException
    //   182	20	2	ex	Exception
    //   34	42	3	c	Class<?>
    //   125	17	3	localThrowable	Throwable
    //   53	3	4	cons	java.lang.reflect.Constructor<?>
    //   133	4	4	localThrowable1	Throwable
    //   73	22	5	compiler	Object
    //   90	3	6	compile	java.lang.reflect.Method
    //   116	7	7	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   29	118	125	java/lang/Throwable
    //   126	130	133	java/lang/Throwable
    //   16	122	143	java/lang/ClassNotFoundException
    //   125	143	143	java/lang/ClassNotFoundException
    //   16	122	182	java/lang/Exception
    //   125	143	182	java/lang/Exception
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.compilers.Javac12
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */