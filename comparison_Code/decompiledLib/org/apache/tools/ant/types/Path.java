package org.apache.tools.ant.types;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PathTokenizer;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.resources.FileResourceIterator;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JavaEnvUtils;

public class Path
  extends DataType
  implements Cloneable, ResourceCollection
{
  public static Path systemClasspath = new Path(null, 
    System.getProperty("java.class.path"));
  public static final Path systemBootClasspath = new Path(null, 
    System.getProperty("sun.boot.class.path"));
  private Boolean preserveBC;
  
  public class PathElement
    implements ResourceCollection
  {
    private String[] parts;
    
    public PathElement() {}
    
    public void setLocation(File loc)
    {
      parts = new String[] { Path.translateFile(loc.getAbsolutePath()) };
    }
    
    public void setPath(String path)
    {
      parts = Path.translatePath(getProject(), path);
    }
    
    public String[] getParts()
    {
      return parts;
    }
    
    public Iterator<Resource> iterator()
    {
      return new FileResourceIterator(getProject(), null, parts);
    }
    
    public boolean isFilesystemOnly()
    {
      return true;
    }
    
    public int size()
    {
      return parts == null ? 0 : parts.length;
    }
  }
  
  private Union union = null;
  private boolean cache = false;
  
  public Path(Project p, String path)
  {
    this(p);
    createPathElement().setPath(path);
  }
  
  public Path(Project project)
  {
    setProject(project);
  }
  
  public void setLocation(File location)
    throws BuildException
  {
    checkAttributesAllowed();
    createPathElement().setLocation(location);
  }
  
  public void setPath(String path)
    throws BuildException
  {
    checkAttributesAllowed();
    createPathElement().setPath(path);
  }
  
  public void setRefid(Reference r)
    throws BuildException
  {
    if (union != null) {
      throw tooManyAttributes();
    }
    super.setRefid(r);
  }
  
  public PathElement createPathElement()
    throws BuildException
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    PathElement pe = new PathElement();
    add(pe);
    return pe;
  }
  
  public void addFileset(FileSet fs)
    throws BuildException
  {
    if (fs.getProject() == null) {
      fs.setProject(getProject());
    }
    add(fs);
  }
  
  public void addFilelist(FileList fl)
    throws BuildException
  {
    if (fl.getProject() == null) {
      fl.setProject(getProject());
    }
    add(fl);
  }
  
  public void addDirset(DirSet dset)
    throws BuildException
  {
    if (dset.getProject() == null) {
      dset.setProject(getProject());
    }
    add(dset);
  }
  
  public void add(Path path)
    throws BuildException
  {
    if (path == this) {
      throw circularReference();
    }
    if (path.getProject() == null) {
      path.setProject(getProject());
    }
    add(path);
  }
  
  public void add(ResourceCollection c)
  {
    checkChildrenAllowed();
    if (c == null) {
      return;
    }
    if (union == null)
    {
      union = new Union();
      union.setProject(getProject());
      union.setCache(cache);
    }
    union.add(c);
    setChecked(false);
  }
  
  public Path createPath()
    throws BuildException
  {
    Path p = new Path(getProject());
    add(p);
    return p;
  }
  
  public void append(Path other)
  {
    if (other == null) {
      return;
    }
    add(other);
  }
  
  public void addExisting(Path source)
  {
    addExisting(source, false);
  }
  
  public void addExisting(Path source, boolean tryUserDir)
  {
    File userDir = tryUserDir ? new File(System.getProperty("user.dir")) : null;
    for (String name : source.list())
    {
      File f = resolveFile(getProject(), name);
      if ((tryUserDir) && (!f.exists())) {
        f = new File(userDir, name);
      }
      if (f.exists())
      {
        setLocation(f);
      }
      else if ((f.getParentFile() != null) && (f.getParentFile().exists()) && 
        (containsWildcards(f.getName())))
      {
        setLocation(f);
        log("adding " + f + " which contains wildcards and may not do what you intend it to do depending on your OS or version of Java", 3);
      }
      else
      {
        log("dropping " + f + " from path as it doesn't exist", 3);
      }
    }
  }
  
  public void setCache(boolean b)
  {
    checkAttributesAllowed();
    cache = b;
    if (union != null) {
      union.setCache(b);
    }
  }
  
  public String[] list()
  {
    if (isReference()) {
      return getRef().list();
    }
    return assertFilesystemOnly(union) == null ? 
      new String[0] : union.list();
  }
  
  public String toString()
  {
    return 
      union == null ? "" : isReference() ? getRef().toString() : union.toString();
  }
  
  public static String[] translatePath(Project project, String source)
  {
    if (source == null) {
      return new String[0];
    }
    List<String> result = new ArrayList();
    PathTokenizer tok = new PathTokenizer(source);
    while (tok.hasMoreTokens())
    {
      StringBuffer element = new StringBuffer();
      String pathElement = tok.nextToken();
      try
      {
        element.append(resolveFile(project, pathElement).getPath());
      }
      catch (BuildException e)
      {
        project.log("Dropping path element " + pathElement + " as it is not valid relative to the project", 3);
      }
      for (int i = 0; i < element.length(); i++) {
        translateFileSep(element, i);
      }
      result.add(element.toString());
    }
    return (String[])result.toArray(new String[result.size()]);
  }
  
  public static String translateFile(String source)
  {
    if (source == null) {
      return "";
    }
    StringBuffer result = new StringBuffer(source);
    for (int i = 0; i < result.length(); i++) {
      translateFileSep(result, i);
    }
    return result.toString();
  }
  
  protected static boolean translateFileSep(StringBuffer buffer, int pos)
  {
    if ((buffer.charAt(pos) == '/') || (buffer.charAt(pos) == '\\'))
    {
      buffer.setCharAt(pos, File.separatorChar);
      return true;
    }
    return false;
  }
  
  public synchronized int size()
  {
    if (isReference()) {
      return getRef().size();
    }
    dieOnCircularReference();
    return union == null ? 0 : assertFilesystemOnly(union).size();
  }
  
  public Object clone()
  {
    try
    {
      Path result = (Path)super.clone();
      union = (union == null ? union : (Union)union.clone());
      return result;
    }
    catch (CloneNotSupportedException e)
    {
      throw new BuildException(e);
    }
  }
  
  protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p)
    throws BuildException
  {
    if (isChecked()) {
      return;
    }
    if (isReference())
    {
      super.dieOnCircularReference(stk, p);
    }
    else
    {
      if (union != null) {
        pushAndInvokeCircularReferenceCheck(union, stk, p);
      }
      setChecked(true);
    }
  }
  
  private static File resolveFile(Project project, String relativeName)
  {
    return FileUtils.getFileUtils().resolveFile(
      project == null ? null : project.getBaseDir(), relativeName);
  }
  
  public Path concatSystemClasspath()
  {
    return concatSystemClasspath("last");
  }
  
  public Path concatSystemClasspath(String defValue)
  {
    return concatSpecialPath(defValue, systemClasspath);
  }
  
  public Path concatSystemBootClasspath(String defValue)
  {
    return concatSpecialPath(defValue, systemBootClasspath);
  }
  
  private Path concatSpecialPath(String defValue, Path p)
  {
    Path result = new Path(getProject());
    
    String order = defValue;
    
    String o = getProject() != null ? getProject().getProperty("build.sysclasspath") : System.getProperty("build.sysclasspath");
    if (o != null) {
      order = o;
    }
    if ("only".equals(order))
    {
      result.addExisting(p, true);
    }
    else if ("first".equals(order))
    {
      result.addExisting(p, true);
      result.addExisting(this);
    }
    else if ("ignore".equals(order))
    {
      result.addExisting(this);
    }
    else
    {
      if (!"last".equals(order)) {
        log("invalid value for build.sysclasspath: " + order, 1);
      }
      result.addExisting(this);
      result.addExisting(p, true);
    }
    return result;
  }
  
  public void addJavaRuntime()
  {
    if (JavaEnvUtils.isKaffe())
    {
      File kaffeShare = new File(JavaEnvUtils.getJavaHome() + File.separator + "share" + File.separator + "kaffe");
      if (kaffeShare.isDirectory())
      {
        FileSet kaffeJarFiles = new FileSet();
        kaffeJarFiles.setDir(kaffeShare);
        kaffeJarFiles.setIncludes("*.jar");
        addFileset(kaffeJarFiles);
      }
    }
    else if ("GNU libgcj".equals(System.getProperty("java.vm.name")))
    {
      addExisting(systemBootClasspath);
    }
    FileSet msZipFiles;
    if (System.getProperty("java.vendor").toLowerCase(Locale.ENGLISH).contains("microsoft"))
    {
      msZipFiles = new FileSet();
      msZipFiles.setDir(new File(JavaEnvUtils.getJavaHome() + File.separator + "Packages"));
      
      msZipFiles.setIncludes("*.ZIP");
      addFileset(msZipFiles);
    }
    else
    {
      addExisting(new Path(null, JavaEnvUtils.getJavaHome() + File.separator + "lib" + File.separator + "rt.jar"));
      
      addExisting(new Path(null, JavaEnvUtils.getJavaHome() + File.separator + "jre" + File.separator + "lib" + File.separator + "rt.jar"));
      for (String secJar : Arrays.asList(new String[] { "jce", "jsse" }))
      {
        addExisting(new Path(null, JavaEnvUtils.getJavaHome() + File.separator + "lib" + File.separator + secJar + ".jar"));
        
        addExisting(new Path(null, JavaEnvUtils.getJavaHome() + File.separator + ".." + File.separator + "Classes" + File.separator + secJar + ".jar"));
      }
      for (String ibmJar : Arrays.asList(new String[] { "core", "graphics", "security", "server", "xml" })) {
        addExisting(new Path(null, JavaEnvUtils.getJavaHome() + File.separator + "lib" + File.separator + ibmJar + ".jar"));
      }
      addExisting(new Path(null, JavaEnvUtils.getJavaHome() + File.separator + ".." + File.separator + "Classes" + File.separator + "classes.jar"));
      
      addExisting(new Path(null, JavaEnvUtils.getJavaHome() + File.separator + ".." + File.separator + "Classes" + File.separator + "ui.jar"));
    }
  }
  
  public void addExtdirs(Path extdirs)
  {
    String extProp;
    if (extdirs == null)
    {
      extProp = System.getProperty("java.ext.dirs");
      if (extProp != null) {
        extdirs = new Path(getProject(), extProp);
      } else {
        return;
      }
    }
    for (String d : extdirs.list())
    {
      File dir = resolveFile(getProject(), d);
      if ((dir.exists()) && (dir.isDirectory()))
      {
        FileSet fs = new FileSet();
        fs.setDir(dir);
        fs.setIncludes("*");
        addFileset(fs);
      }
    }
  }
  
  public final synchronized Iterator<Resource> iterator()
  {
    if (isReference()) {
      return getRef().iterator();
    }
    dieOnCircularReference();
    if (getPreserveBC()) {
      return new FileResourceIterator(getProject(), null, list());
    }
    return union == null ? Collections.emptySet().iterator() : 
      assertFilesystemOnly(union).iterator();
  }
  
  public synchronized boolean isFilesystemOnly()
  {
    if (isReference()) {
      return getRef().isFilesystemOnly();
    }
    dieOnCircularReference();
    assertFilesystemOnly(union);
    return true;
  }
  
  protected ResourceCollection assertFilesystemOnly(ResourceCollection rc)
  {
    if ((rc != null) && (!rc.isFilesystemOnly())) {
      throw new BuildException("%s allows only filesystem resources.", new Object[] {getDataTypeName() });
    }
    return rc;
  }
  
  protected boolean delegateIteratorToList()
  {
    if (getClass().equals(Path.class)) {
      return false;
    }
    try
    {
      Method listMethod = getClass().getMethod("list", new Class[0]);
      return !listMethod.getDeclaringClass().equals(Path.class);
    }
    catch (Exception e) {}
    return false;
  }
  
  private synchronized boolean getPreserveBC()
  {
    if (preserveBC == null) {
      preserveBC = (delegateIteratorToList() ? Boolean.TRUE : Boolean.FALSE);
    }
    return preserveBC.booleanValue();
  }
  
  private static boolean containsWildcards(String path)
  {
    return (path != null) && ((path.contains("*")) || (path.contains("?")));
  }
  
  private Path getRef()
  {
    return (Path)getCheckedRef(Path.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.Path
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */