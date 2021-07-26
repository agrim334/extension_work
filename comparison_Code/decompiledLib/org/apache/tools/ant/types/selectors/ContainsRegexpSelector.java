package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.util.regexp.Regexp;

public class ContainsRegexpSelector
  extends BaseExtendSelector
  implements ResourceSelector
{
  public static final String EXPRESSION_KEY = "expression";
  private static final String CS_KEY = "casesensitive";
  private static final String ML_KEY = "multiline";
  private static final String SL_KEY = "singleline";
  private String userProvidedExpression = null;
  private RegularExpression myRegExp = null;
  private Regexp myExpression = null;
  private boolean caseSensitive = true;
  private boolean multiLine = false;
  private boolean singleLine = false;
  
  public String toString()
  {
    return String.format("{containsregexpselector expression: %s}", new Object[] { userProvidedExpression });
  }
  
  public void setExpression(String theexpression)
  {
    userProvidedExpression = theexpression;
  }
  
  public void setCaseSensitive(boolean b)
  {
    caseSensitive = b;
  }
  
  public void setMultiLine(boolean b)
  {
    multiLine = b;
  }
  
  public void setSingleLine(boolean b)
  {
    singleLine = b;
  }
  
  public void setParameters(Parameter... parameters)
  {
    super.setParameters(parameters);
    if (parameters != null) {
      for (Parameter parameter : parameters)
      {
        String paramname = parameter.getName();
        if ("expression".equalsIgnoreCase(paramname)) {
          setExpression(parameter.getValue());
        } else if ("casesensitive".equalsIgnoreCase(paramname)) {
          setCaseSensitive(Project.toBoolean(parameter.getValue()));
        } else if ("multiline".equalsIgnoreCase(paramname)) {
          setMultiLine(Project.toBoolean(parameter.getValue()));
        } else if ("singleline".equalsIgnoreCase(paramname)) {
          setSingleLine(Project.toBoolean(parameter.getValue()));
        } else {
          setError("Invalid parameter " + paramname);
        }
      }
    }
  }
  
  public void verifySettings()
  {
    if (userProvidedExpression == null) {
      setError("The expression attribute is required");
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
    //   1: invokevirtual 111	org/apache/tools/ant/types/selectors/ContainsRegexpSelector:validate	()V
    //   4: aload_1
    //   5: invokevirtual 114	org/apache/tools/ant/types/Resource:isDirectory	()Z
    //   8: ifeq +5 -> 13
    //   11: iconst_1
    //   12: ireturn
    //   13: aload_0
    //   14: getfield 13	org/apache/tools/ant/types/selectors/ContainsRegexpSelector:myRegExp	Lorg/apache/tools/ant/types/RegularExpression;
    //   17: ifnonnull +40 -> 57
    //   20: aload_0
    //   21: new 120	org/apache/tools/ant/types/RegularExpression
    //   24: dup
    //   25: invokespecial 122	org/apache/tools/ant/types/RegularExpression:<init>	()V
    //   28: putfield 13	org/apache/tools/ant/types/selectors/ContainsRegexpSelector:myRegExp	Lorg/apache/tools/ant/types/RegularExpression;
    //   31: aload_0
    //   32: getfield 13	org/apache/tools/ant/types/selectors/ContainsRegexpSelector:myRegExp	Lorg/apache/tools/ant/types/RegularExpression;
    //   35: aload_0
    //   36: getfield 7	org/apache/tools/ant/types/selectors/ContainsRegexpSelector:userProvidedExpression	Ljava/lang/String;
    //   39: invokevirtual 123	org/apache/tools/ant/types/RegularExpression:setPattern	(Ljava/lang/String;)V
    //   42: aload_0
    //   43: aload_0
    //   44: getfield 13	org/apache/tools/ant/types/selectors/ContainsRegexpSelector:myRegExp	Lorg/apache/tools/ant/types/RegularExpression;
    //   47: aload_0
    //   48: invokevirtual 126	org/apache/tools/ant/types/selectors/ContainsRegexpSelector:getProject	()Lorg/apache/tools/ant/Project;
    //   51: invokevirtual 130	org/apache/tools/ant/types/RegularExpression:getRegexp	(Lorg/apache/tools/ant/Project;)Lorg/apache/tools/ant/util/regexp/Regexp;
    //   54: putfield 17	org/apache/tools/ant/types/selectors/ContainsRegexpSelector:myExpression	Lorg/apache/tools/ant/util/regexp/Regexp;
    //   57: new 134	java/io/BufferedReader
    //   60: dup
    //   61: new 136	java/io/InputStreamReader
    //   64: dup
    //   65: aload_1
    //   66: invokevirtual 138	org/apache/tools/ant/types/Resource:getInputStream	()Ljava/io/InputStream;
    //   69: invokespecial 142	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   72: invokespecial 145	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   75: astore_2
    //   76: aload_2
    //   77: invokevirtual 148	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   80: astore_3
    //   81: aload_3
    //   82: ifnull +49 -> 131
    //   85: aload_0
    //   86: getfield 17	org/apache/tools/ant/types/selectors/ContainsRegexpSelector:myExpression	Lorg/apache/tools/ant/util/regexp/Regexp;
    //   89: aload_3
    //   90: aload_0
    //   91: getfield 21	org/apache/tools/ant/types/selectors/ContainsRegexpSelector:caseSensitive	Z
    //   94: aload_0
    //   95: getfield 25	org/apache/tools/ant/types/selectors/ContainsRegexpSelector:multiLine	Z
    //   98: aload_0
    //   99: getfield 28	org/apache/tools/ant/types/selectors/ContainsRegexpSelector:singleLine	Z
    //   102: invokestatic 151	org/apache/tools/ant/util/regexp/RegexpUtil:asOptions	(ZZZ)I
    //   105: invokeinterface 157 3 0
    //   110: ifeq +13 -> 123
    //   113: iconst_1
    //   114: istore 4
    //   116: aload_2
    //   117: invokevirtual 163	java/io/BufferedReader:close	()V
    //   120: iload 4
    //   122: ireturn
    //   123: aload_2
    //   124: invokevirtual 148	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   127: astore_3
    //   128: goto -47 -> 81
    //   131: iconst_0
    //   132: istore 4
    //   134: aload_2
    //   135: invokevirtual 163	java/io/BufferedReader:close	()V
    //   138: iload 4
    //   140: ireturn
    //   141: astore_3
    //   142: new 168	org/apache/tools/ant/BuildException
    //   145: dup
    //   146: new 85	java/lang/StringBuilder
    //   149: dup
    //   150: invokespecial 87	java/lang/StringBuilder:<init>	()V
    //   153: ldc -86
    //   155: invokevirtual 90	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   158: aload_1
    //   159: invokevirtual 172	org/apache/tools/ant/types/Resource:toLongString	()Ljava/lang/String;
    //   162: invokevirtual 90	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   165: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   168: invokespecial 175	org/apache/tools/ant/BuildException:<init>	(Ljava/lang/String;)V
    //   171: athrow
    //   172: astore_3
    //   173: aload_2
    //   174: invokevirtual 163	java/io/BufferedReader:close	()V
    //   177: goto +11 -> 188
    //   180: astore 4
    //   182: aload_3
    //   183: aload 4
    //   185: invokevirtual 179	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   188: aload_3
    //   189: athrow
    //   190: astore_2
    //   191: new 168	org/apache/tools/ant/BuildException
    //   194: dup
    //   195: new 85	java/lang/StringBuilder
    //   198: dup
    //   199: invokespecial 87	java/lang/StringBuilder:<init>	()V
    //   202: ldc -73
    //   204: invokevirtual 90	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   207: aload_1
    //   208: invokevirtual 172	org/apache/tools/ant/types/Resource:toLongString	()Ljava/lang/String;
    //   211: invokevirtual 90	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   214: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   217: aload_2
    //   218: invokespecial 185	org/apache/tools/ant/BuildException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   221: athrow
    // Line number table:
    //   Java source line #161	-> byte code offset #0
    //   Java source line #163	-> byte code offset #4
    //   Java source line #164	-> byte code offset #11
    //   Java source line #167	-> byte code offset #13
    //   Java source line #168	-> byte code offset #20
    //   Java source line #169	-> byte code offset #31
    //   Java source line #170	-> byte code offset #42
    //   Java source line #173	-> byte code offset #57
    //   Java source line #174	-> byte code offset #66
    //   Java source line #176	-> byte code offset #76
    //   Java source line #178	-> byte code offset #81
    //   Java source line #179	-> byte code offset #85
    //   Java source line #180	-> byte code offset #102
    //   Java source line #179	-> byte code offset #105
    //   Java source line #181	-> byte code offset #113
    //   Java source line #189	-> byte code offset #116
    //   Java source line #181	-> byte code offset #120
    //   Java source line #183	-> byte code offset #123
    //   Java source line #185	-> byte code offset #131
    //   Java source line #189	-> byte code offset #134
    //   Java source line #185	-> byte code offset #138
    //   Java source line #186	-> byte code offset #141
    //   Java source line #187	-> byte code offset #142
    //   Java source line #173	-> byte code offset #172
    //   Java source line #189	-> byte code offset #190
    //   Java source line #190	-> byte code offset #191
    //   Java source line #191	-> byte code offset #208
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	222	0	this	ContainsRegexpSelector
    //   0	222	1	r	org.apache.tools.ant.types.Resource
    //   75	99	2	in	java.io.BufferedReader
    //   190	28	2	e	java.io.IOException
    //   80	48	3	teststr	String
    //   141	2	3	ioe	java.io.IOException
    //   172	17	3	localThrowable	Throwable
    //   114	25	4	bool	boolean
    //   180	4	4	localThrowable1	Throwable
    //   180	4	4	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   76	116	141	java/io/IOException
    //   123	134	141	java/io/IOException
    //   76	116	172	java/lang/Throwable
    //   123	134	172	java/lang/Throwable
    //   141	172	172	java/lang/Throwable
    //   173	177	180	java/lang/Throwable
    //   57	120	190	java/io/IOException
    //   123	138	190	java/io/IOException
    //   141	190	190	java/io/IOException
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.ContainsRegexpSelector
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */