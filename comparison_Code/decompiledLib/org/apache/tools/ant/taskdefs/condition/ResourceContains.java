package org.apache.tools.ant.taskdefs.condition;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;

public class ResourceContains
  implements Condition
{
  private Project project;
  private String substring;
  private Resource resource;
  private String refid;
  private boolean casesensitive = true;
  
  public void setProject(Project project)
  {
    this.project = project;
  }
  
  public Project getProject()
  {
    return project;
  }
  
  public void setResource(String r)
  {
    resource = new FileResource(new File(r));
  }
  
  public void setRefid(String refid)
  {
    this.refid = refid;
  }
  
  /* Error */
  private void resolveRefid()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 35	org/apache/tools/ant/taskdefs/condition/ResourceContains:getProject	()Lorg/apache/tools/ant/Project;
    //   4: ifnonnull +13 -> 17
    //   7: new 39	org/apache/tools/ant/BuildException
    //   10: dup
    //   11: ldc 41
    //   13: invokespecial 43	org/apache/tools/ant/BuildException:<init>	(Ljava/lang/String;)V
    //   16: athrow
    //   17: aload_0
    //   18: invokevirtual 35	org/apache/tools/ant/taskdefs/condition/ResourceContains:getProject	()Lorg/apache/tools/ant/Project;
    //   21: aload_0
    //   22: getfield 31	org/apache/tools/ant/taskdefs/condition/ResourceContains:refid	Ljava/lang/String;
    //   25: invokevirtual 44	org/apache/tools/ant/Project:getReference	(Ljava/lang/String;)Ljava/lang/Object;
    //   28: astore_1
    //   29: aload_1
    //   30: instanceof 50
    //   33: ifne +65 -> 98
    //   36: aload_1
    //   37: instanceof 52
    //   40: ifeq +33 -> 73
    //   43: aload_1
    //   44: checkcast 52	org/apache/tools/ant/types/ResourceCollection
    //   47: astore_2
    //   48: aload_2
    //   49: invokeinterface 54 1 0
    //   54: iconst_1
    //   55: if_icmpne +15 -> 70
    //   58: aload_2
    //   59: invokeinterface 58 1 0
    //   64: invokeinterface 62 1 0
    //   69: astore_1
    //   70: goto +28 -> 98
    //   73: new 39	org/apache/tools/ant/BuildException
    //   76: dup
    //   77: ldc 68
    //   79: iconst_2
    //   80: anewarray 2	java/lang/Object
    //   83: dup
    //   84: iconst_0
    //   85: aload_0
    //   86: getfield 31	org/apache/tools/ant/taskdefs/condition/ResourceContains:refid	Ljava/lang/String;
    //   89: aastore
    //   90: dup
    //   91: iconst_1
    //   92: aload_1
    //   93: aastore
    //   94: invokespecial 70	org/apache/tools/ant/BuildException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
    //   97: athrow
    //   98: aload_0
    //   99: aload_1
    //   100: checkcast 50	org/apache/tools/ant/types/Resource
    //   103: putfield 27	org/apache/tools/ant/taskdefs/condition/ResourceContains:resource	Lorg/apache/tools/ant/types/Resource;
    //   106: aload_0
    //   107: aconst_null
    //   108: putfield 31	org/apache/tools/ant/taskdefs/condition/ResourceContains:refid	Ljava/lang/String;
    //   111: goto +11 -> 122
    //   114: astore_3
    //   115: aload_0
    //   116: aconst_null
    //   117: putfield 31	org/apache/tools/ant/taskdefs/condition/ResourceContains:refid	Ljava/lang/String;
    //   120: aload_3
    //   121: athrow
    //   122: return
    // Line number table:
    //   Java source line #80	-> byte code offset #0
    //   Java source line #81	-> byte code offset #7
    //   Java source line #83	-> byte code offset #17
    //   Java source line #84	-> byte code offset #29
    //   Java source line #85	-> byte code offset #36
    //   Java source line #86	-> byte code offset #43
    //   Java source line #87	-> byte code offset #48
    //   Java source line #88	-> byte code offset #58
    //   Java source line #90	-> byte code offset #70
    //   Java source line #91	-> byte code offset #73
    //   Java source line #94	-> byte code offset #98
    //   Java source line #96	-> byte code offset #106
    //   Java source line #97	-> byte code offset #111
    //   Java source line #96	-> byte code offset #114
    //   Java source line #97	-> byte code offset #120
    //   Java source line #98	-> byte code offset #122
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	123	0	this	ResourceContains
    //   28	72	1	o	Object
    //   47	12	2	rc	org.apache.tools.ant.types.ResourceCollection
    //   114	7	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   0	106	114	finally
  }
  
  public void setSubstring(String substring)
  {
    this.substring = substring;
  }
  
  public void setCasesensitive(boolean casesensitive)
  {
    this.casesensitive = casesensitive;
  }
  
  private void validate()
  {
    if ((resource != null) && (refid != null)) {
      throw new BuildException("Cannot set both resource and refid");
    }
    if ((resource == null) && (refid != null)) {
      resolveRefid();
    }
    if ((resource == null) || (substring == null)) {
      throw new BuildException("both resource and substring are required in <resourcecontains>");
    }
  }
  
  /* Error */
  public synchronized boolean eval()
    throws BuildException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 83	org/apache/tools/ant/taskdefs/condition/ResourceContains:validate	()V
    //   4: aload_0
    //   5: getfield 73	org/apache/tools/ant/taskdefs/condition/ResourceContains:substring	Ljava/lang/String;
    //   8: invokevirtual 86	java/lang/String:isEmpty	()Z
    //   11: ifeq +22 -> 33
    //   14: aload_0
    //   15: invokevirtual 35	org/apache/tools/ant/taskdefs/condition/ResourceContains:getProject	()Lorg/apache/tools/ant/Project;
    //   18: ifnull +13 -> 31
    //   21: aload_0
    //   22: invokevirtual 35	org/apache/tools/ant/taskdefs/condition/ResourceContains:getProject	()Lorg/apache/tools/ant/Project;
    //   25: ldc 92
    //   27: iconst_3
    //   28: invokevirtual 94	org/apache/tools/ant/Project:log	(Ljava/lang/String;I)V
    //   31: iconst_1
    //   32: ireturn
    //   33: aload_0
    //   34: getfield 27	org/apache/tools/ant/taskdefs/condition/ResourceContains:resource	Lorg/apache/tools/ant/types/Resource;
    //   37: invokevirtual 98	org/apache/tools/ant/types/Resource:getSize	()J
    //   40: lconst_0
    //   41: lcmp
    //   42: ifne +5 -> 47
    //   45: iconst_0
    //   46: ireturn
    //   47: new 102	java/io/BufferedReader
    //   50: dup
    //   51: new 104	java/io/InputStreamReader
    //   54: dup
    //   55: aload_0
    //   56: getfield 27	org/apache/tools/ant/taskdefs/condition/ResourceContains:resource	Lorg/apache/tools/ant/types/Resource;
    //   59: invokevirtual 106	org/apache/tools/ant/types/Resource:getInputStream	()Ljava/io/InputStream;
    //   62: invokespecial 110	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   65: invokespecial 113	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   68: astore_1
    //   69: aload_1
    //   70: invokestatic 116	org/apache/tools/ant/util/FileUtils:safeReadFully	(Ljava/io/Reader;)Ljava/lang/String;
    //   73: astore_2
    //   74: aload_0
    //   75: getfield 73	org/apache/tools/ant/taskdefs/condition/ResourceContains:substring	Ljava/lang/String;
    //   78: astore_3
    //   79: aload_0
    //   80: getfield 7	org/apache/tools/ant/taskdefs/condition/ResourceContains:casesensitive	Z
    //   83: ifne +13 -> 96
    //   86: aload_2
    //   87: invokevirtual 122	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   90: astore_2
    //   91: aload_3
    //   92: invokevirtual 122	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   95: astore_3
    //   96: aload_2
    //   97: aload_3
    //   98: invokevirtual 126	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   101: istore 4
    //   103: aload_1
    //   104: invokevirtual 130	java/io/BufferedReader:close	()V
    //   107: iload 4
    //   109: ireturn
    //   110: astore_2
    //   111: aload_1
    //   112: invokevirtual 130	java/io/BufferedReader:close	()V
    //   115: goto +9 -> 124
    //   118: astore_3
    //   119: aload_2
    //   120: aload_3
    //   121: invokevirtual 135	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   124: aload_2
    //   125: athrow
    //   126: astore_1
    //   127: new 39	org/apache/tools/ant/BuildException
    //   130: dup
    //   131: new 141	java/lang/StringBuilder
    //   134: dup
    //   135: invokespecial 143	java/lang/StringBuilder:<init>	()V
    //   138: ldc -112
    //   140: invokevirtual 146	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   143: aload_0
    //   144: getfield 27	org/apache/tools/ant/taskdefs/condition/ResourceContains:resource	Lorg/apache/tools/ant/types/Resource;
    //   147: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   150: invokevirtual 153	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   153: invokespecial 43	org/apache/tools/ant/BuildException:<init>	(Ljava/lang/String;)V
    //   156: athrow
    // Line number table:
    //   Java source line #136	-> byte code offset #0
    //   Java source line #138	-> byte code offset #4
    //   Java source line #139	-> byte code offset #14
    //   Java source line #140	-> byte code offset #21
    //   Java source line #143	-> byte code offset #31
    //   Java source line #145	-> byte code offset #33
    //   Java source line #146	-> byte code offset #45
    //   Java source line #149	-> byte code offset #47
    //   Java source line #150	-> byte code offset #59
    //   Java source line #151	-> byte code offset #69
    //   Java source line #152	-> byte code offset #74
    //   Java source line #153	-> byte code offset #79
    //   Java source line #154	-> byte code offset #86
    //   Java source line #155	-> byte code offset #91
    //   Java source line #157	-> byte code offset #96
    //   Java source line #158	-> byte code offset #103
    //   Java source line #157	-> byte code offset #107
    //   Java source line #149	-> byte code offset #110
    //   Java source line #158	-> byte code offset #126
    //   Java source line #159	-> byte code offset #127
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	157	0	this	ResourceContains
    //   68	44	1	reader	java.io.BufferedReader
    //   126	2	1	e	java.io.IOException
    //   73	24	2	contents	String
    //   110	15	2	localThrowable	Throwable
    //   78	20	3	sub	String
    //   118	3	3	localThrowable1	Throwable
    //   101	7	4	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   69	103	110	java/lang/Throwable
    //   111	115	118	java/lang/Throwable
    //   47	107	126	java/io/IOException
    //   110	126	126	java/io/IOException
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.condition.ResourceContains
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */