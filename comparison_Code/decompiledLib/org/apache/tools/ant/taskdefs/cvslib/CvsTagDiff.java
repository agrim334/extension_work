package org.apache.tools.ant.taskdefs.cvslib;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.AbstractCvsTask;
import org.apache.tools.ant.taskdefs.AbstractCvsTask.Module;
import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.util.DOMUtils;
import org.apache.tools.ant.util.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CvsTagDiff
  extends AbstractCvsTask
{
  private static final FileUtils FILE_UTILS = ;
  private static final DOMElementWriter DOM_WRITER = new DOMElementWriter();
  static final String FILE_STRING = "File ";
  static final int FILE_STRING_LENGTH = "File ".length();
  static final String TO_STRING = " to ";
  static final String FILE_IS_NEW = " is new;";
  static final String REVISION = "revision ";
  static final String FILE_HAS_CHANGED = " changed from revision ";
  static final String FILE_WAS_REMOVED = " is removed";
  private String mypackage;
  private String mystartTag;
  private String myendTag;
  private String mystartDate;
  private String myendDate;
  private File mydestfile;
  private boolean ignoreRemoved = false;
  private List<String> packageNames = new ArrayList();
  private String[] packageNamePrefixes = null;
  private int[] packageNamePrefixLengths = null;
  
  public void setPackage(String p)
  {
    mypackage = p;
  }
  
  public void setStartTag(String s)
  {
    mystartTag = s;
  }
  
  public void setStartDate(String s)
  {
    mystartDate = s;
  }
  
  public void setEndTag(String s)
  {
    myendTag = s;
  }
  
  public void setEndDate(String s)
  {
    myendDate = s;
  }
  
  public void setDestFile(File f)
  {
    mydestfile = f;
  }
  
  public void setIgnoreRemoved(boolean b)
  {
    ignoreRemoved = b;
  }
  
  /* Error */
  public void execute()
    throws BuildException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 48	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:validate	()V
    //   4: aload_0
    //   5: ldc 51
    //   7: invokevirtual 53	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:addCommandArgument	(Ljava/lang/String;)V
    //   10: aload_0
    //   11: ldc 57
    //   13: invokevirtual 53	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:addCommandArgument	(Ljava/lang/String;)V
    //   16: aload_0
    //   17: getfield 32	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:mystartTag	Ljava/lang/String;
    //   20: ifnull +20 -> 40
    //   23: aload_0
    //   24: ldc 59
    //   26: invokevirtual 53	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:addCommandArgument	(Ljava/lang/String;)V
    //   29: aload_0
    //   30: aload_0
    //   31: getfield 32	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:mystartTag	Ljava/lang/String;
    //   34: invokevirtual 53	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:addCommandArgument	(Ljava/lang/String;)V
    //   37: goto +17 -> 54
    //   40: aload_0
    //   41: ldc 61
    //   43: invokevirtual 53	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:addCommandArgument	(Ljava/lang/String;)V
    //   46: aload_0
    //   47: aload_0
    //   48: getfield 35	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:mystartDate	Ljava/lang/String;
    //   51: invokevirtual 53	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:addCommandArgument	(Ljava/lang/String;)V
    //   54: aload_0
    //   55: getfield 38	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:myendTag	Ljava/lang/String;
    //   58: ifnull +20 -> 78
    //   61: aload_0
    //   62: ldc 59
    //   64: invokevirtual 53	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:addCommandArgument	(Ljava/lang/String;)V
    //   67: aload_0
    //   68: aload_0
    //   69: getfield 38	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:myendTag	Ljava/lang/String;
    //   72: invokevirtual 53	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:addCommandArgument	(Ljava/lang/String;)V
    //   75: goto +17 -> 92
    //   78: aload_0
    //   79: ldc 61
    //   81: invokevirtual 53	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:addCommandArgument	(Ljava/lang/String;)V
    //   84: aload_0
    //   85: aload_0
    //   86: getfield 41	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:myendDate	Ljava/lang/String;
    //   89: invokevirtual 53	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:addCommandArgument	(Ljava/lang/String;)V
    //   92: aload_0
    //   93: ldc 63
    //   95: invokevirtual 65	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:setCommand	(Ljava/lang/String;)V
    //   98: aconst_null
    //   99: astore_1
    //   100: aload_0
    //   101: invokespecial 68	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:handlePackageNames	()V
    //   104: getstatic 71	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:FILE_UTILS	Lorg/apache/tools/ant/util/FileUtils;
    //   107: aload_0
    //   108: invokevirtual 75	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:getProject	()Lorg/apache/tools/ant/Project;
    //   111: ldc 79
    //   113: ldc 81
    //   115: aconst_null
    //   116: iconst_1
    //   117: iconst_1
    //   118: invokevirtual 83	org/apache/tools/ant/util/FileUtils:createTempFile	(Lorg/apache/tools/ant/Project;Ljava/lang/String;Ljava/lang/String;Ljava/io/File;ZZ)Ljava/io/File;
    //   121: astore_1
    //   122: aload_0
    //   123: aload_1
    //   124: invokevirtual 89	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:setOutput	(Ljava/io/File;)V
    //   127: aload_0
    //   128: invokespecial 93	org/apache/tools/ant/taskdefs/AbstractCvsTask:execute	()V
    //   131: aload_0
    //   132: aload_1
    //   133: invokespecial 96	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:parseRDiff	(Ljava/io/File;)[Lorg/apache/tools/ant/taskdefs/cvslib/CvsTagEntry;
    //   136: astore_2
    //   137: aload_0
    //   138: aload_2
    //   139: invokespecial 100	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:writeTagDiff	([Lorg/apache/tools/ant/taskdefs/cvslib/CvsTagEntry;)V
    //   142: aload_0
    //   143: aconst_null
    //   144: putfield 20	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:packageNamePrefixes	[Ljava/lang/String;
    //   147: aload_0
    //   148: aconst_null
    //   149: putfield 24	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:packageNamePrefixLengths	[I
    //   152: aload_0
    //   153: getfield 16	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:packageNames	Ljava/util/List;
    //   156: invokeinterface 104 1 0
    //   161: aload_1
    //   162: ifnull +42 -> 204
    //   165: aload_1
    //   166: invokevirtual 109	java/io/File:delete	()Z
    //   169: pop
    //   170: goto +34 -> 204
    //   173: astore_3
    //   174: aload_0
    //   175: aconst_null
    //   176: putfield 20	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:packageNamePrefixes	[Ljava/lang/String;
    //   179: aload_0
    //   180: aconst_null
    //   181: putfield 24	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:packageNamePrefixLengths	[I
    //   184: aload_0
    //   185: getfield 16	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:packageNames	Ljava/util/List;
    //   188: invokeinterface 104 1 0
    //   193: aload_1
    //   194: ifnull +8 -> 202
    //   197: aload_1
    //   198: invokevirtual 109	java/io/File:delete	()Z
    //   201: pop
    //   202: aload_3
    //   203: athrow
    //   204: return
    // Line number table:
    //   Java source line #237	-> byte code offset #0
    //   Java source line #240	-> byte code offset #4
    //   Java source line #241	-> byte code offset #10
    //   Java source line #242	-> byte code offset #16
    //   Java source line #243	-> byte code offset #23
    //   Java source line #244	-> byte code offset #29
    //   Java source line #246	-> byte code offset #40
    //   Java source line #247	-> byte code offset #46
    //   Java source line #249	-> byte code offset #54
    //   Java source line #250	-> byte code offset #61
    //   Java source line #251	-> byte code offset #67
    //   Java source line #253	-> byte code offset #78
    //   Java source line #254	-> byte code offset #84
    //   Java source line #258	-> byte code offset #92
    //   Java source line #259	-> byte code offset #98
    //   Java source line #261	-> byte code offset #100
    //   Java source line #263	-> byte code offset #104
    //   Java source line #265	-> byte code offset #122
    //   Java source line #268	-> byte code offset #127
    //   Java source line #271	-> byte code offset #131
    //   Java source line #274	-> byte code offset #137
    //   Java source line #277	-> byte code offset #142
    //   Java source line #278	-> byte code offset #147
    //   Java source line #279	-> byte code offset #152
    //   Java source line #280	-> byte code offset #161
    //   Java source line #281	-> byte code offset #165
    //   Java source line #277	-> byte code offset #173
    //   Java source line #278	-> byte code offset #179
    //   Java source line #279	-> byte code offset #184
    //   Java source line #280	-> byte code offset #193
    //   Java source line #281	-> byte code offset #197
    //   Java source line #283	-> byte code offset #202
    //   Java source line #284	-> byte code offset #204
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	205	0	this	CvsTagDiff
    //   99	99	1	tmpFile	File
    //   136	3	2	entries	CvsTagEntry[]
    //   173	30	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   100	142	173	finally
  }
  
  /* Error */
  private CvsTagEntry[] parseRDiff(File tmpFile)
    throws BuildException
  {
    // Byte code:
    //   0: new 115	java/io/BufferedReader
    //   3: dup
    //   4: new 117	java/io/FileReader
    //   7: dup
    //   8: aload_1
    //   9: invokespecial 119	java/io/FileReader:<init>	(Ljava/io/File;)V
    //   12: invokespecial 121	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   15: astore_2
    //   16: new 13	java/util/ArrayList
    //   19: dup
    //   20: invokespecial 15	java/util/ArrayList:<init>	()V
    //   23: astore_3
    //   24: aload_2
    //   25: invokevirtual 124	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   28: astore 4
    //   30: aconst_null
    //   31: aload 4
    //   33: if_acmpeq +69 -> 102
    //   36: aload 4
    //   38: aload_0
    //   39: getfield 20	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:packageNamePrefixes	[Ljava/lang/String;
    //   42: aload_0
    //   43: getfield 24	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:packageNamePrefixLengths	[I
    //   46: invokestatic 128	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:removePackageName	(Ljava/lang/String;[Ljava/lang/String;[I)Ljava/lang/String;
    //   49: astore 4
    //   51: aload 4
    //   53: ifnull +40 -> 93
    //   56: aload_0
    //   57: aload_3
    //   58: aload 4
    //   60: invokespecial 132	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:doFileIsNew	(Ljava/util/List;Ljava/lang/String;)Z
    //   63: ifne +23 -> 86
    //   66: aload_0
    //   67: aload_3
    //   68: aload 4
    //   70: invokespecial 136	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:doFileHasChanged	(Ljava/util/List;Ljava/lang/String;)Z
    //   73: ifne +13 -> 86
    //   76: aload_0
    //   77: aload_3
    //   78: aload 4
    //   80: invokespecial 139	org/apache/tools/ant/taskdefs/cvslib/CvsTagDiff:doFileWasRemoved	(Ljava/util/List;Ljava/lang/String;)Z
    //   83: ifeq +7 -> 90
    //   86: iconst_1
    //   87: goto +4 -> 91
    //   90: iconst_0
    //   91: istore 5
    //   93: aload_2
    //   94: invokevirtual 124	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   97: astore 4
    //   99: goto -69 -> 30
    //   102: aload_3
    //   103: aload_3
    //   104: invokeinterface 142 1 0
    //   109: anewarray 146	org/apache/tools/ant/taskdefs/cvslib/CvsTagEntry
    //   112: invokeinterface 148 2 0
    //   117: checkcast 152	[Lorg/apache/tools/ant/taskdefs/cvslib/CvsTagEntry;
    //   120: astore 5
    //   122: aload_2
    //   123: invokevirtual 154	java/io/BufferedReader:close	()V
    //   126: aload 5
    //   128: areturn
    //   129: astore_3
    //   130: aload_2
    //   131: invokevirtual 154	java/io/BufferedReader:close	()V
    //   134: goto +11 -> 145
    //   137: astore 4
    //   139: aload_3
    //   140: aload 4
    //   142: invokevirtual 159	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   145: aload_3
    //   146: athrow
    //   147: astore_2
    //   148: new 165	org/apache/tools/ant/BuildException
    //   151: dup
    //   152: ldc -89
    //   154: aload_2
    //   155: invokespecial 169	org/apache/tools/ant/BuildException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   158: athrow
    // Line number table:
    //   Java source line #297	-> byte code offset #0
    //   Java source line #314	-> byte code offset #16
    //   Java source line #316	-> byte code offset #24
    //   Java source line #318	-> byte code offset #30
    //   Java source line #319	-> byte code offset #36
    //   Java source line #321	-> byte code offset #51
    //   Java source line #324	-> byte code offset #56
    //   Java source line #325	-> byte code offset #60
    //   Java source line #326	-> byte code offset #70
    //   Java source line #327	-> byte code offset #80
    //   Java source line #329	-> byte code offset #93
    //   Java source line #332	-> byte code offset #102
    //   Java source line #333	-> byte code offset #122
    //   Java source line #332	-> byte code offset #126
    //   Java source line #297	-> byte code offset #129
    //   Java source line #333	-> byte code offset #147
    //   Java source line #334	-> byte code offset #148
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	159	0	this	CvsTagDiff
    //   0	159	1	tmpFile	File
    //   15	116	2	reader	java.io.BufferedReader
    //   147	8	2	e	IOException
    //   23	81	3	entries	List<CvsTagEntry>
    //   129	17	3	localThrowable	Throwable
    //   28	70	4	line	String
    //   137	4	4	localThrowable1	Throwable
    //   91	1	5	i	int
    //   120	7	5	arrayOfCvsTagEntry	CvsTagEntry[]
    // Exception table:
    //   from	to	target	type
    //   16	122	129	java/lang/Throwable
    //   130	134	137	java/lang/Throwable
    //   0	126	147	java/io/IOException
    //   129	147	147	java/io/IOException
  }
  
  private boolean doFileIsNew(List<CvsTagEntry> entries, String line)
  {
    int index = line.indexOf(" is new;");
    if (index == -1) {
      return false;
    }
    String filename = line.substring(0, index);
    String rev = null;
    int indexrev = line.indexOf("revision ", index);
    if (indexrev != -1) {
      rev = line.substring(indexrev + "revision ".length());
    }
    CvsTagEntry entry = new CvsTagEntry(filename, rev);
    entries.add(entry);
    log(entry.toString(), 3);
    return true;
  }
  
  private boolean doFileHasChanged(List<CvsTagEntry> entries, String line)
  {
    int index = line.indexOf(" changed from revision ");
    if (index == -1) {
      return false;
    }
    String filename = line.substring(0, index);
    int revSeparator = line.indexOf(" to ", index);
    
    String prevRevision = line.substring(index + " changed from revision ".length(), revSeparator);
    
    String revision = line.substring(revSeparator + " to ".length());
    CvsTagEntry entry = new CvsTagEntry(filename, revision, prevRevision);
    
    entries.add(entry);
    log(entry.toString(), 3);
    return true;
  }
  
  private boolean doFileWasRemoved(List<CvsTagEntry> entries, String line)
  {
    if (ignoreRemoved) {
      return false;
    }
    int index = line.indexOf(" is removed");
    if (index == -1) {
      return false;
    }
    String filename = line.substring(0, index);
    String rev = null;
    int indexrev = line.indexOf("revision ", index);
    if (indexrev != -1) {
      rev = line.substring(indexrev + "revision ".length());
    }
    CvsTagEntry entry = new CvsTagEntry(filename, null, rev);
    entries.add(entry);
    log(entry.toString(), 3);
    return true;
  }
  
  private void writeTagDiff(CvsTagEntry[] entries)
    throws BuildException
  {
    try
    {
      PrintWriter writer = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(mydestfile.toPath(), new OpenOption[0]), StandardCharsets.UTF_8));
      try
      {
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        Document doc = DOMUtils.newDocument();
        Element root = doc.createElement("tagdiff");
        if (mystartTag != null) {
          root.setAttribute("startTag", mystartTag);
        } else {
          root.setAttribute("startDate", mystartDate);
        }
        if (myendTag != null) {
          root.setAttribute("endTag", myendTag);
        } else {
          root.setAttribute("endDate", myendDate);
        }
        root.setAttribute("cvsroot", getCvsRoot());
        root.setAttribute("package", 
          String.join(",", packageNames));
        DOM_WRITER.openElement(root, writer, 0, "\t");
        writer.println();
        for (CvsTagEntry entry : entries) {
          writeTagEntry(doc, writer, entry);
        }
        DOM_WRITER.closeElement(root, writer, 0, "\t", true);
        writer.flush();
        if (writer.checkError()) {
          throw new IOException("Encountered an error writing tagdiff");
        }
        writer.close();
      }
      catch (Throwable localThrowable) {}
      try
      {
        writer.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      throw localThrowable;
    }
    catch (UnsupportedEncodingException uee)
    {
      log(uee.toString(), 0);
    }
    catch (IOException ioe)
    {
      throw new BuildException(ioe.toString(), ioe);
    }
  }
  
  private void writeTagEntry(Document doc, PrintWriter writer, CvsTagEntry entry)
    throws IOException
  {
    Element ent = doc.createElement("entry");
    Element f = DOMUtils.createChildElement(ent, "file");
    DOMUtils.appendCDATAElement(f, "name", entry.getFile());
    if (entry.getRevision() != null) {
      DOMUtils.appendTextElement(f, "revision", entry.getRevision());
    }
    if (entry.getPreviousRevision() != null) {
      DOMUtils.appendTextElement(f, "prevrevision", entry
        .getPreviousRevision());
    }
    DOM_WRITER.write(ent, writer, 1, "\t");
  }
  
  private void validate()
    throws BuildException
  {
    if ((null == mypackage) && (getModules().isEmpty())) {
      throw new BuildException("Package/module must be set.");
    }
    if (null == mydestfile) {
      throw new BuildException("Destfile must be set.");
    }
    if ((null == mystartTag) && (null == mystartDate)) {
      throw new BuildException("Start tag or start date must be set.");
    }
    if ((null != mystartTag) && (null != mystartDate)) {
      throw new BuildException("Only one of start tag and start date must be set.");
    }
    if ((null == myendTag) && (null == myendDate)) {
      throw new BuildException("End tag or end date must be set.");
    }
    if ((null != myendTag) && (null != myendDate)) {
      throw new BuildException("Only one of end tag and end date must be set.");
    }
  }
  
  private void handlePackageNames()
  {
    StringTokenizer myTokenizer;
    if (mypackage != null)
    {
      myTokenizer = new StringTokenizer(mypackage);
      while (myTokenizer.hasMoreTokens())
      {
        String pack = myTokenizer.nextToken();
        packageNames.add(pack);
        addCommandArgument(pack);
      }
    }
    for (AbstractCvsTask.Module m : getModules()) {
      packageNames.add(m.getName());
    }
    packageNamePrefixes = new String[packageNames.size()];
    packageNamePrefixLengths = new int[packageNames.size()];
    for (int i = 0; i < packageNamePrefixes.length; i++)
    {
      packageNamePrefixes[i] = ("File " + (String)packageNames.get(i) + "/");
      packageNamePrefixLengths[i] = packageNamePrefixes[i].length();
    }
  }
  
  private static String removePackageName(String line, String[] packagePrefixes, int[] prefixLengths)
  {
    if (line.length() < FILE_STRING_LENGTH) {
      return null;
    }
    for (int i = 0; i < packagePrefixes.length; i++) {
      if (line.startsWith(packagePrefixes[i])) {
        return line.substring(prefixLengths[i]);
      }
    }
    return line.substring(FILE_STRING_LENGTH);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.cvslib.CvsTagDiff
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */