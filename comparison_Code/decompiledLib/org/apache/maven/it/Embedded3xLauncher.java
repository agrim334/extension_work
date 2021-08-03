package org.apache.maven.it;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.maven.shared.utils.io.IOUtil;

class Embedded3xLauncher
  implements MavenLauncher
{
  private final Object mavenCli;
  private final Method doMain;
  
  private Embedded3xLauncher(Object mavenCli, Method doMain)
  {
    this.mavenCli = mavenCli;
    this.doMain = doMain;
  }
  
  public static Embedded3xLauncher createFromMavenHome(String mavenHome, String classworldConf, List<URL> classpath)
    throws LauncherException
  {
    if ((mavenHome == null) || (mavenHome.length() <= 0)) {
      throw new LauncherException("Invalid Maven home directory " + mavenHome);
    }
    System.setProperty("maven.home", mavenHome);
    File config;
    File config;
    if (classworldConf != null) {
      config = new File(classworldConf);
    } else {
      config = new File(mavenHome, "bin/m2.conf");
    }
    ClassLoader bootLoader = getBootLoader(mavenHome, classpath);
    
    ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(bootLoader);
    try
    {
      Class<?> launcherClass = bootLoader.loadClass("org.codehaus.plexus.classworlds.launcher.Launcher");
      
      Object launcher = launcherClass.newInstance();
      
      Method configure = launcherClass.getMethod("configure", new Class[] { InputStream.class });
      
      configure.invoke(launcher, new Object[] { new FileInputStream(config) });
      
      Method getWorld = launcherClass.getMethod("getWorld", new Class[0]);
      Object classWorld = getWorld.invoke(launcher, new Object[0]);
      
      Method getMainClass = launcherClass.getMethod("getMainClass", new Class[0]);
      Class<?> cliClass = (Class)getMainClass.invoke(launcher, new Object[0]);
      
      Constructor<?> newMavenCli = cliClass.getConstructor(new Class[] { classWorld.getClass() });
      Object mavenCli = newMavenCli.newInstance(new Object[] { classWorld });
      
      Class<?>[] parameterTypes = { String[].class, String.class, PrintStream.class, PrintStream.class };
      Method doMain = cliClass.getMethod("doMain", parameterTypes);
      
      return new Embedded3xLauncher(mavenCli, doMain);
    }
    catch (ReflectiveOperationException|IOException e)
    {
      throw new LauncherException("Failed to initialize Laucher", e);
    }
    finally
    {
      Thread.currentThread().setContextClassLoader(oldClassLoader);
    }
  }
  
  public static Embedded3xLauncher createFromClasspath()
    throws LauncherException
  {
    ClassLoader coreLoader = Thread.currentThread().getContextClassLoader();
    try
    {
      Class<?> cliClass = coreLoader.loadClass("org.apache.maven.cli.MavenCli");
      
      Object mavenCli = cliClass.newInstance();
      
      Class<?>[] parameterTypes = { String[].class, String.class, PrintStream.class, PrintStream.class };
      Method doMain = cliClass.getMethod("doMain", parameterTypes);
      
      return new Embedded3xLauncher(mavenCli, doMain);
    }
    catch (ReflectiveOperationException e)
    {
      throw new LauncherException(e.getMessage(), e);
    }
  }
  
  private static ClassLoader getBootLoader(String mavenHome, List<URL> classpath)
  {
    List<URL> urls = classpath;
    if (urls == null)
    {
      urls = new ArrayList();
      
      File bootDir = new File(mavenHome, "boot");
      addUrls(urls, bootDir);
    }
    if (urls.isEmpty()) {
      throw new IllegalArgumentException("Invalid Maven home directory " + mavenHome);
    }
    URL[] ucp = (URL[])urls.toArray(new URL[urls.size()]);
    
    return new URLClassLoader(ucp, ClassLoader.getSystemClassLoader().getParent());
  }
  
  private static void addUrls(List<URL> urls, File directory)
  {
    File[] jars = directory.listFiles();
    if (jars != null) {
      for (int i = 0; i < jars.length; i++)
      {
        File jar = jars[i];
        if (jar.getName().endsWith(".jar")) {
          try
          {
            urls.add(jar.toURI().toURL());
          }
          catch (MalformedURLException e)
          {
            throw ((RuntimeException)new IllegalStateException().initCause(e));
          }
        }
      }
    }
  }
  
  /* Error */
  public int run(String[] cliArgs, Properties systemProperties, String workingDirectory, File logFile)
    throws IOException, LauncherException
  {
    // Byte code:
    //   0: aload 4
    //   2: ifnull +22 -> 24
    //   5: new 127	java/io/PrintStream
    //   8: dup
    //   9: new 225	java/io/FileOutputStream
    //   12: dup
    //   13: aload 4
    //   15: invokespecial 227	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   18: invokespecial 228	java/io/PrintStream:<init>	(Ljava/io/OutputStream;)V
    //   21: goto +6 -> 27
    //   24: getstatic 231	java/lang/System:out	Ljava/io/PrintStream;
    //   27: astore 5
    //   29: invokestatic 235	java/lang/System:getProperties	()Ljava/util/Properties;
    //   32: astore 6
    //   34: aconst_null
    //   35: invokestatic 239	java/lang/System:setProperties	(Ljava/util/Properties;)V
    //   38: ldc 41
    //   40: aload 6
    //   42: ldc 41
    //   44: ldc -13
    //   46: invokevirtual 245	java/util/Properties:getProperty	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   49: invokestatic 43	java/lang/System:setProperty	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   52: pop
    //   53: ldc -6
    //   55: new 49	java/io/File
    //   58: dup
    //   59: aload_3
    //   60: invokespecial 51	java/io/File:<init>	(Ljava/lang/String;)V
    //   63: invokevirtual 252	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   66: invokestatic 43	java/lang/System:setProperty	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   69: pop
    //   70: aload_2
    //   71: invokevirtual 255	java/util/Properties:keySet	()Ljava/util/Set;
    //   74: invokeinterface 259 1 0
    //   79: astore 7
    //   81: aload 7
    //   83: invokeinterface 265 1 0
    //   88: ifeq +38 -> 126
    //   91: aload 7
    //   93: invokeinterface 270 1 0
    //   98: astore 8
    //   100: aload 8
    //   102: checkcast 18	java/lang/String
    //   105: astore 9
    //   107: aload_2
    //   108: aload 9
    //   110: invokevirtual 273	java/util/Properties:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   113: astore 10
    //   115: aload 9
    //   117: aload 10
    //   119: invokestatic 43	java/lang/System:setProperty	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   122: pop
    //   123: goto -42 -> 81
    //   126: invokestatic 61	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   129: invokevirtual 67	java/lang/Thread:getContextClassLoader	()Ljava/lang/ClassLoader;
    //   132: astore 7
    //   134: invokestatic 61	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   137: aload_0
    //   138: getfield 7	org/apache/maven/it/Embedded3xLauncher:mavenCli	Ljava/lang/Object;
    //   141: invokevirtual 112	java/lang/Object:getClass	()Ljava/lang/Class;
    //   144: invokevirtual 276	java/lang/Class:getClassLoader	()Ljava/lang/ClassLoader;
    //   147: invokevirtual 71	java/lang/Thread:setContextClassLoader	(Ljava/lang/ClassLoader;)V
    //   150: aload_0
    //   151: getfield 13	org/apache/maven/it/Embedded3xLauncher:doMain	Ljava/lang/reflect/Method;
    //   154: aload_0
    //   155: getfield 7	org/apache/maven/it/Embedded3xLauncher:mavenCli	Ljava/lang/Object;
    //   158: iconst_4
    //   159: anewarray 2	java/lang/Object
    //   162: dup
    //   163: iconst_0
    //   164: aload_1
    //   165: aastore
    //   166: dup
    //   167: iconst_1
    //   168: aload_3
    //   169: aastore
    //   170: dup
    //   171: iconst_2
    //   172: aload 5
    //   174: aastore
    //   175: dup
    //   176: iconst_3
    //   177: aload 5
    //   179: aastore
    //   180: invokevirtual 102	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   183: astore 8
    //   185: aload 8
    //   187: checkcast 279	java/lang/Number
    //   190: invokevirtual 281	java/lang/Number:intValue	()I
    //   193: istore 9
    //   195: invokestatic 61	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   198: aload 7
    //   200: invokevirtual 71	java/lang/Thread:setContextClassLoader	(Ljava/lang/ClassLoader;)V
    //   203: aload 6
    //   205: invokestatic 239	java/lang/System:setProperties	(Ljava/util/Properties;)V
    //   208: aload 4
    //   210: ifnull +8 -> 218
    //   213: aload 5
    //   215: invokevirtual 284	java/io/PrintStream:close	()V
    //   218: iload 9
    //   220: ireturn
    //   221: astore 11
    //   223: invokestatic 61	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   226: aload 7
    //   228: invokevirtual 71	java/lang/Thread:setContextClassLoader	(Ljava/lang/ClassLoader;)V
    //   231: aload 6
    //   233: invokestatic 239	java/lang/System:setProperties	(Ljava/util/Properties;)V
    //   236: aload 11
    //   238: athrow
    //   239: astore 6
    //   241: new 23	org/apache/maven/it/LauncherException
    //   244: dup
    //   245: new 25	java/lang/StringBuilder
    //   248: dup
    //   249: invokespecial 27	java/lang/StringBuilder:<init>	()V
    //   252: ldc_w 291
    //   255: invokevirtual 30	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   258: aload 6
    //   260: invokevirtual 144	java/lang/ReflectiveOperationException:getMessage	()Ljava/lang/String;
    //   263: invokevirtual 30	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   266: invokevirtual 34	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   269: aload 6
    //   271: invokespecial 139	org/apache/maven/it/LauncherException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   274: athrow
    //   275: astore 12
    //   277: aload 4
    //   279: ifnull +8 -> 287
    //   282: aload 5
    //   284: invokevirtual 284	java/io/PrintStream:close	()V
    //   287: aload 12
    //   289: athrow
    // Line number table:
    //   Java source line #196	-> byte code offset #0
    //   Java source line #199	-> byte code offset #29
    //   Java source line #200	-> byte code offset #34
    //   Java source line #201	-> byte code offset #38
    //   Java source line #202	-> byte code offset #53
    //   Java source line #204	-> byte code offset #70
    //   Java source line #206	-> byte code offset #100
    //   Java source line #207	-> byte code offset #107
    //   Java source line #208	-> byte code offset #115
    //   Java source line #209	-> byte code offset #123
    //   Java source line #211	-> byte code offset #126
    //   Java source line #212	-> byte code offset #134
    //   Java source line #215	-> byte code offset #150
    //   Java source line #217	-> byte code offset #185
    //   Java source line #221	-> byte code offset #195
    //   Java source line #223	-> byte code offset #203
    //   Java source line #232	-> byte code offset #208
    //   Java source line #234	-> byte code offset #213
    //   Java source line #217	-> byte code offset #218
    //   Java source line #221	-> byte code offset #221
    //   Java source line #223	-> byte code offset #231
    //   Java source line #224	-> byte code offset #236
    //   Java source line #226	-> byte code offset #239
    //   Java source line #228	-> byte code offset #241
    //   Java source line #232	-> byte code offset #275
    //   Java source line #234	-> byte code offset #282
    //   Java source line #236	-> byte code offset #287
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	290	0	this	Embedded3xLauncher
    //   0	290	1	cliArgs	String[]
    //   0	290	2	systemProperties	Properties
    //   0	290	3	workingDirectory	String
    //   0	290	4	logFile	File
    //   27	256	5	out	PrintStream
    //   32	200	6	originalProperties	Properties
    //   239	31	6	e	ReflectiveOperationException
    //   79	13	7	localIterator	java.util.Iterator
    //   132	95	7	originalClassLoader	ClassLoader
    //   98	3	8	o	Object
    //   183	3	8	result	Object
    //   105	114	9	key	String
    //   113	5	10	value	String
    //   221	16	11	localObject1	Object
    //   275	13	12	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   150	195	221	finally
    //   221	223	221	finally
    //   29	208	239	java/lang/IllegalAccessException
    //   29	208	239	java/lang/reflect/InvocationTargetException
    //   221	239	239	java/lang/IllegalAccessException
    //   221	239	239	java/lang/reflect/InvocationTargetException
    //   29	208	275	finally
    //   221	277	275	finally
  }
  
  public String getMavenVersion()
    throws LauncherException
  {
    Properties props = new Properties();
    
    InputStream is = mavenCli.getClass().getResourceAsStream("/META-INF/maven/org.apache.maven/maven-core/pom.properties");
    if (is != null) {
      try
      {
        props.load(is);
      }
      catch (IOException e)
      {
        throw new LauncherException("Failed to read Maven version", e);
      }
      finally
      {
        IOUtil.close(is);
      }
    }
    String version = props.getProperty("version");
    if (version != null) {
      return version;
    }
    throw new LauncherException("Could not determine embedded Maven version");
  }
}

/* Location:
 * Qualified Name:     org.apache.maven.it.Embedded3xLauncher
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */