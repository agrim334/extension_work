package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.InputStream;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.FileUtils;

public class Available
  extends Task
  implements Condition
{
  private static final FileUtils FILE_UTILS = ;
  private String property;
  private String classname;
  private String filename;
  private File file;
  private Path filepath;
  private String resource;
  private FileDir type;
  private Path classpath;
  private AntClassLoader loader;
  private Object value = "true";
  private boolean isTask = false;
  private boolean ignoreSystemclasses = false;
  private boolean searchParents = false;
  
  public void setSearchParents(boolean searchParents)
  {
    this.searchParents = searchParents;
  }
  
  public void setClasspath(Path classpath)
  {
    createClasspath().append(classpath);
  }
  
  public Path createClasspath()
  {
    if (classpath == null) {
      classpath = new Path(getProject());
    }
    return classpath.createPath();
  }
  
  public void setClasspathRef(Reference r)
  {
    createClasspath().setRefid(r);
  }
  
  public void setFilepath(Path filepath)
  {
    createFilepath().append(filepath);
  }
  
  public Path createFilepath()
  {
    if (filepath == null) {
      filepath = new Path(getProject());
    }
    return filepath.createPath();
  }
  
  public void setProperty(String property)
  {
    this.property = property;
  }
  
  public void setValue(Object value)
  {
    this.value = value;
  }
  
  public void setValue(String value)
  {
    setValue(value);
  }
  
  public void setClassname(String classname)
  {
    if (!classname.isEmpty()) {
      this.classname = classname;
    }
  }
  
  public void setFile(File file)
  {
    this.file = file;
    filename = FILE_UTILS.removeLeadingPath(getProject().getBaseDir(), file);
  }
  
  public void setResource(String resource)
  {
    this.resource = resource;
  }
  
  @Deprecated
  public void setType(String type)
  {
    log("DEPRECATED - The setType(String) method has been deprecated. Use setType(Available.FileDir) instead.", 1);
    
    this.type = new FileDir();
    this.type.setValue(type);
  }
  
  public void setType(FileDir type)
  {
    this.type = type;
  }
  
  public void setIgnoresystemclasses(boolean ignore)
  {
    ignoreSystemclasses = ignore;
  }
  
  /* Error */
  public void execute()
    throws BuildException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 59	org/apache/tools/ant/taskdefs/Available:property	Ljava/lang/String;
    //   4: ifnonnull +17 -> 21
    //   7: new 118	org/apache/tools/ant/BuildException
    //   10: dup
    //   11: ldc 120
    //   13: aload_0
    //   14: invokevirtual 122	org/apache/tools/ant/taskdefs/Available:getLocation	()Lorg/apache/tools/ant/Location;
    //   17: invokespecial 126	org/apache/tools/ant/BuildException:<init>	(Ljava/lang/String;Lorg/apache/tools/ant/Location;)V
    //   20: athrow
    //   21: aload_0
    //   22: iconst_1
    //   23: putfield 15	org/apache/tools/ant/taskdefs/Available:isTask	Z
    //   26: aload_0
    //   27: invokevirtual 129	org/apache/tools/ant/taskdefs/Available:eval	()Z
    //   30: ifeq +64 -> 94
    //   33: aload_0
    //   34: invokevirtual 39	org/apache/tools/ant/taskdefs/Available:getProject	()Lorg/apache/tools/ant/Project;
    //   37: invokestatic 132	org/apache/tools/ant/PropertyHelper:getPropertyHelper	(Lorg/apache/tools/ant/Project;)Lorg/apache/tools/ant/PropertyHelper;
    //   40: astore_1
    //   41: aload_1
    //   42: aload_0
    //   43: getfield 59	org/apache/tools/ant/taskdefs/Available:property	Ljava/lang/String;
    //   46: invokevirtual 138	org/apache/tools/ant/PropertyHelper:getProperty	(Ljava/lang/String;)Ljava/lang/Object;
    //   49: astore_2
    //   50: aconst_null
    //   51: aload_2
    //   52: if_acmpeq +28 -> 80
    //   55: aload_2
    //   56: aload_0
    //   57: getfield 9	org/apache/tools/ant/taskdefs/Available:value	Ljava/lang/Object;
    //   60: invokevirtual 142	java/lang/Object:equals	(Ljava/lang/Object;)Z
    //   63: ifne +17 -> 80
    //   66: aload_0
    //   67: ldc -108
    //   69: iconst_0
    //   70: anewarray 143	java/lang/Object
    //   73: invokestatic 150	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   76: iconst_1
    //   77: invokevirtual 104	org/apache/tools/ant/taskdefs/Available:log	(Ljava/lang/String;I)V
    //   80: aload_1
    //   81: aload_0
    //   82: getfield 59	org/apache/tools/ant/taskdefs/Available:property	Ljava/lang/String;
    //   85: aload_0
    //   86: getfield 9	org/apache/tools/ant/taskdefs/Available:value	Ljava/lang/Object;
    //   89: iconst_1
    //   90: invokevirtual 154	org/apache/tools/ant/PropertyHelper:setProperty	(Ljava/lang/String;Ljava/lang/Object;Z)Z
    //   93: pop
    //   94: aload_0
    //   95: iconst_0
    //   96: putfield 15	org/apache/tools/ant/taskdefs/Available:isTask	Z
    //   99: goto +11 -> 110
    //   102: astore_3
    //   103: aload_0
    //   104: iconst_0
    //   105: putfield 15	org/apache/tools/ant/taskdefs/Available:isTask	Z
    //   108: aload_3
    //   109: athrow
    //   110: return
    // Line number table:
    //   Java source line #231	-> byte code offset #0
    //   Java source line #232	-> byte code offset #7
    //   Java source line #233	-> byte code offset #14
    //   Java source line #236	-> byte code offset #21
    //   Java source line #238	-> byte code offset #26
    //   Java source line #239	-> byte code offset #33
    //   Java source line #240	-> byte code offset #41
    //   Java source line #241	-> byte code offset #50
    //   Java source line #242	-> byte code offset #66
    //   Java source line #248	-> byte code offset #80
    //   Java source line #251	-> byte code offset #94
    //   Java source line #252	-> byte code offset #99
    //   Java source line #251	-> byte code offset #102
    //   Java source line #252	-> byte code offset #108
    //   Java source line #253	-> byte code offset #110
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	111	0	this	Available
    //   40	41	1	ph	org.apache.tools.ant.PropertyHelper
    //   49	7	2	oldvalue	Object
    //   102	7	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   26	94	102	finally
  }
  
  public boolean eval()
    throws BuildException
  {
    try
    {
      if ((classname == null) && (file == null) && (resource == null)) {
        throw new BuildException("At least one of (classname|file|resource) is required", getLocation());
      }
      if ((type != null) && 
        (file == null)) {
        throw new BuildException("The type attribute is only valid when specifying the file attribute.", getLocation());
      }
      if (classpath != null)
      {
        classpath.setProject(getProject());
        loader = getProject().createClassLoader(classpath);
      }
      String appendix = "";
      if (isTask) {
        appendix = " to set property " + property;
      } else {
        setTaskName("available");
      }
      if ((classname != null) && (!checkClass(classname)))
      {
        log("Unable to load class " + classname + appendix, 3);
        
        return false;
      }
      StringBuilder buf;
      if ((file != null) && (!checkFile()))
      {
        buf = new StringBuilder("Unable to find ");
        if (type != null) {
          buf.append(type).append(' ');
        }
        buf.append(filename).append(appendix);
        log(buf.toString(), 3);
        return false;
      }
      if ((resource != null) && (!checkResource(resource)))
      {
        log("Unable to load resource " + resource + appendix, 3);
        
        return false;
      }
    }
    finally
    {
      if (loader != null)
      {
        loader.cleanup();
        loader = null;
      }
      if (!isTask) {
        setTaskName(null);
      }
    }
    return true;
  }
  
  private boolean checkFile()
  {
    if (filepath == null) {
      return checkFile(file, filename);
    }
    String[] paths = filepath.list();
    for (String p : paths)
    {
      log("Searching " + p, 3);
      File path = new File(p);
      if ((path.exists()) && (
        (filename.equals(p)) || 
        (filename.equals(path.getName()))))
      {
        if (type == null)
        {
          log("Found: " + path, 3);
          return true;
        }
        if ((type.isDir()) && 
          (path.isDirectory()))
        {
          log("Found directory: " + path, 3);
          return true;
        }
        if ((type.isFile()) && 
          (path.isFile()))
        {
          log("Found file: " + path, 3);
          return true;
        }
        return false;
      }
      File parent = path.getParentFile();
      if ((parent != null) && (parent.exists()) && 
        (filename.equals(parent.getAbsolutePath())))
      {
        if (type == null)
        {
          log("Found: " + parent, 3);
          return true;
        }
        if (type.isDir())
        {
          log("Found directory: " + parent, 3);
          return true;
        }
        return false;
      }
      if ((path.exists()) && (path.isDirectory()) && 
        (checkFile(new File(path, filename), filename + " in " + path))) {
        return true;
      }
      while ((searchParents) && (parent != null) && (parent.exists()))
      {
        if (checkFile(new File(parent, filename), filename + " in " + parent)) {
          return true;
        }
        parent = parent.getParentFile();
      }
    }
    return false;
  }
  
  private boolean checkFile(File f, String text)
  {
    if (type != null)
    {
      if (type.isDir())
      {
        if (f.isDirectory()) {
          log("Found directory: " + text, 3);
        }
        return f.isDirectory();
      }
      if (type.isFile())
      {
        if (f.isFile()) {
          log("Found file: " + text, 3);
        }
        return f.isFile();
      }
    }
    if (f.exists()) {
      log("Found: " + text, 3);
    }
    return f.exists();
  }
  
  private boolean checkResource(String resource)
  {
    InputStream is = null;
    try
    {
      ClassLoader cL;
      if (loader != null)
      {
        is = loader.getResourceAsStream(resource);
      }
      else
      {
        cL = getClass().getClassLoader();
        if (cL != null) {
          is = cL.getResourceAsStream(resource);
        } else {
          is = ClassLoader.getSystemResourceAsStream(resource);
        }
      }
      return is != null ? 1 : 0;
    }
    finally
    {
      FileUtils.close(is);
    }
  }
  
  private boolean checkClass(String classname)
  {
    try
    {
      if (ignoreSystemclasses)
      {
        loader = getProject().createClassLoader(classpath);
        loader.setParentFirst(false);
        loader.addJavaLibraries();
        try
        {
          loader.findClass(classname);
        }
        catch (SecurityException se)
        {
          return true;
        }
      }
      else if (loader != null)
      {
        loader.loadClass(classname);
      }
      else
      {
        ClassLoader l = getClass().getClassLoader();
        if (l != null) {
          Class.forName(classname, true, l);
        } else {
          Class.forName(classname);
        }
      }
      return true;
    }
    catch (ClassNotFoundException e)
    {
      log("class \"" + classname + "\" was not found", 4);
      
      return false;
    }
    catch (NoClassDefFoundError e)
    {
      log("Could not load dependent class \"" + e.getMessage() + "\" for class \"" + classname + "\"", 4);
    }
    return false;
  }
  
  public static class FileDir
    extends EnumeratedAttribute
  {
    private static final String[] VALUES = { "file", "dir" };
    
    public String[] getValues()
    {
      return VALUES;
    }
    
    public boolean isDir()
    {
      return "dir".equalsIgnoreCase(getValue());
    }
    
    public boolean isFile()
    {
      return "file".equalsIgnoreCase(getValue());
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Available
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */