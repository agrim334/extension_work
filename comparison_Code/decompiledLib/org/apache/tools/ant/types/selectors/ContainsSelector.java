package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;

public class ContainsSelector
  extends BaseExtendSelector
  implements ResourceSelector
{
  public static final String EXPRESSION_KEY = "expression";
  public static final String CONTAINS_KEY = "text";
  public static final String CASE_KEY = "casesensitive";
  public static final String WHITESPACE_KEY = "ignorewhitespace";
  private String contains = null;
  private boolean casesensitive = true;
  private boolean ignorewhitespace = false;
  private String encoding = null;
  
  public String toString()
  {
    return String.format("{containsselector text: \"%s\" casesensitive: %s ignorewhitespace: %s}", new Object[] { contains, 
      Boolean.valueOf(casesensitive), Boolean.valueOf(ignorewhitespace) });
  }
  
  public void setText(String contains)
  {
    this.contains = contains;
  }
  
  public void setEncoding(String encoding)
  {
    this.encoding = encoding;
  }
  
  public void setCasesensitive(boolean casesensitive)
  {
    this.casesensitive = casesensitive;
  }
  
  public void setIgnorewhitespace(boolean ignorewhitespace)
  {
    this.ignorewhitespace = ignorewhitespace;
  }
  
  public void setParameters(Parameter... parameters)
  {
    super.setParameters(parameters);
    if (parameters != null) {
      for (Parameter parameter : parameters)
      {
        String paramname = parameter.getName();
        if ("text".equalsIgnoreCase(paramname)) {
          setText(parameter.getValue());
        } else if ("casesensitive".equalsIgnoreCase(paramname)) {
          setCasesensitive(Project.toBoolean(parameter
            .getValue()));
        } else if ("ignorewhitespace".equalsIgnoreCase(paramname)) {
          setIgnorewhitespace(Project.toBoolean(parameter
            .getValue()));
        } else {
          setError("Invalid parameter " + paramname);
        }
      }
    }
  }
  
  public void verifySettings()
  {
    if (contains == null) {
      setError("The text attribute is required");
    }
  }
  
  public boolean isSelected(File basedir, String filename, File file)
  {
    return isSelected(new FileResource(file));
  }
  
  /* Error */
  public boolean isSelected(org.apache.tools.ant.types.Resource r)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 102	org/apache/tools/ant/types/selectors/ContainsSelector:validate	()V
    //   4: aload_1
    //   5: invokevirtual 105	org/apache/tools/ant/types/Resource:isDirectory	()Z
    //   8: ifne +13 -> 21
    //   11: aload_0
    //   12: getfield 7	org/apache/tools/ant/types/selectors/ContainsSelector:contains	Ljava/lang/String;
    //   15: invokevirtual 111	java/lang/String:isEmpty	()Z
    //   18: ifeq +5 -> 23
    //   21: iconst_1
    //   22: ireturn
    //   23: aload_0
    //   24: getfield 7	org/apache/tools/ant/types/selectors/ContainsSelector:contains	Ljava/lang/String;
    //   27: astore_2
    //   28: aload_0
    //   29: getfield 13	org/apache/tools/ant/types/selectors/ContainsSelector:casesensitive	Z
    //   32: ifne +11 -> 43
    //   35: aload_0
    //   36: getfield 7	org/apache/tools/ant/types/selectors/ContainsSelector:contains	Ljava/lang/String;
    //   39: invokevirtual 114	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   42: astore_2
    //   43: aload_0
    //   44: getfield 17	org/apache/tools/ant/types/selectors/ContainsSelector:ignorewhitespace	Z
    //   47: ifeq +8 -> 55
    //   50: aload_2
    //   51: invokestatic 117	org/apache/tools/ant/types/selectors/SelectorUtils:removeWhitespace	(Ljava/lang/String;)Ljava/lang/String;
    //   54: astore_2
    //   55: new 123	java/io/BufferedReader
    //   58: dup
    //   59: new 125	java/io/InputStreamReader
    //   62: dup
    //   63: aload_1
    //   64: invokevirtual 127	org/apache/tools/ant/types/Resource:getInputStream	()Ljava/io/InputStream;
    //   67: aload_0
    //   68: getfield 20	org/apache/tools/ant/types/selectors/ContainsSelector:encoding	Ljava/lang/String;
    //   71: ifnonnull +9 -> 80
    //   74: invokestatic 131	java/nio/charset/Charset:defaultCharset	()Ljava/nio/charset/Charset;
    //   77: goto +10 -> 87
    //   80: aload_0
    //   81: getfield 20	org/apache/tools/ant/types/selectors/ContainsSelector:encoding	Ljava/lang/String;
    //   84: invokestatic 137	java/nio/charset/Charset:forName	(Ljava/lang/String;)Ljava/nio/charset/Charset;
    //   87: invokespecial 141	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/nio/charset/Charset;)V
    //   90: invokespecial 144	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   93: astore_3
    //   94: aload_3
    //   95: invokevirtual 147	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   98: astore 4
    //   100: aload 4
    //   102: ifnull +59 -> 161
    //   105: aload_0
    //   106: getfield 13	org/apache/tools/ant/types/selectors/ContainsSelector:casesensitive	Z
    //   109: ifne +10 -> 119
    //   112: aload 4
    //   114: invokevirtual 114	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   117: astore 4
    //   119: aload_0
    //   120: getfield 17	org/apache/tools/ant/types/selectors/ContainsSelector:ignorewhitespace	Z
    //   123: ifeq +10 -> 133
    //   126: aload 4
    //   128: invokestatic 117	org/apache/tools/ant/types/selectors/SelectorUtils:removeWhitespace	(Ljava/lang/String;)Ljava/lang/String;
    //   131: astore 4
    //   133: aload 4
    //   135: aload_2
    //   136: invokevirtual 150	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   139: ifeq +13 -> 152
    //   142: iconst_1
    //   143: istore 5
    //   145: aload_3
    //   146: invokevirtual 153	java/io/BufferedReader:close	()V
    //   149: iload 5
    //   151: ireturn
    //   152: aload_3
    //   153: invokevirtual 147	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   156: astore 4
    //   158: goto -58 -> 100
    //   161: iconst_0
    //   162: istore 5
    //   164: aload_3
    //   165: invokevirtual 153	java/io/BufferedReader:close	()V
    //   168: iload 5
    //   170: ireturn
    //   171: astore 4
    //   173: new 158	org/apache/tools/ant/BuildException
    //   176: dup
    //   177: new 76	java/lang/StringBuilder
    //   180: dup
    //   181: invokespecial 78	java/lang/StringBuilder:<init>	()V
    //   184: ldc -96
    //   186: invokevirtual 81	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   189: aload_1
    //   190: invokevirtual 162	org/apache/tools/ant/types/Resource:toLongString	()Ljava/lang/String;
    //   193: invokevirtual 81	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   196: invokevirtual 85	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   199: invokespecial 165	org/apache/tools/ant/BuildException:<init>	(Ljava/lang/String;)V
    //   202: athrow
    //   203: astore 4
    //   205: aload_3
    //   206: invokevirtual 153	java/io/BufferedReader:close	()V
    //   209: goto +12 -> 221
    //   212: astore 5
    //   214: aload 4
    //   216: aload 5
    //   218: invokevirtual 169	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   221: aload 4
    //   223: athrow
    //   224: astore_3
    //   225: new 158	org/apache/tools/ant/BuildException
    //   228: dup
    //   229: new 76	java/lang/StringBuilder
    //   232: dup
    //   233: invokespecial 78	java/lang/StringBuilder:<init>	()V
    //   236: ldc -83
    //   238: invokevirtual 81	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   241: aload_1
    //   242: invokevirtual 162	org/apache/tools/ant/types/Resource:toLongString	()Ljava/lang/String;
    //   245: invokevirtual 81	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   248: invokevirtual 85	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   251: aload_3
    //   252: invokespecial 175	org/apache/tools/ant/BuildException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   255: athrow
    // Line number table:
    //   Java source line #160	-> byte code offset #0
    //   Java source line #162	-> byte code offset #4
    //   Java source line #163	-> byte code offset #21
    //   Java source line #166	-> byte code offset #23
    //   Java source line #167	-> byte code offset #28
    //   Java source line #168	-> byte code offset #35
    //   Java source line #170	-> byte code offset #43
    //   Java source line #171	-> byte code offset #50
    //   Java source line #173	-> byte code offset #55
    //   Java source line #174	-> byte code offset #64
    //   Java source line #175	-> byte code offset #74
    //   Java source line #177	-> byte code offset #94
    //   Java source line #178	-> byte code offset #100
    //   Java source line #179	-> byte code offset #105
    //   Java source line #180	-> byte code offset #112
    //   Java source line #182	-> byte code offset #119
    //   Java source line #183	-> byte code offset #126
    //   Java source line #185	-> byte code offset #133
    //   Java source line #186	-> byte code offset #142
    //   Java source line #194	-> byte code offset #145
    //   Java source line #186	-> byte code offset #149
    //   Java source line #188	-> byte code offset #152
    //   Java source line #190	-> byte code offset #161
    //   Java source line #194	-> byte code offset #164
    //   Java source line #190	-> byte code offset #168
    //   Java source line #191	-> byte code offset #171
    //   Java source line #192	-> byte code offset #173
    //   Java source line #173	-> byte code offset #203
    //   Java source line #194	-> byte code offset #224
    //   Java source line #195	-> byte code offset #225
    //   Java source line #196	-> byte code offset #242
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	256	0	this	ContainsSelector
    //   0	256	1	r	org.apache.tools.ant.types.Resource
    //   27	109	2	userstr	String
    //   93	113	3	in	java.io.BufferedReader
    //   224	28	3	e	java.io.IOException
    //   98	59	4	teststr	String
    //   171	3	4	ioe	java.io.IOException
    //   203	19	4	localThrowable	Throwable
    //   143	26	5	bool	boolean
    //   212	5	5	localThrowable1	Throwable
    //   212	5	5	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   94	145	171	java/io/IOException
    //   152	164	171	java/io/IOException
    //   94	145	203	java/lang/Throwable
    //   152	164	203	java/lang/Throwable
    //   171	203	203	java/lang/Throwable
    //   205	209	212	java/lang/Throwable
    //   55	149	224	java/io/IOException
    //   152	168	224	java/io/IOException
    //   171	224	224	java/io/IOException
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.ContainsSelector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */