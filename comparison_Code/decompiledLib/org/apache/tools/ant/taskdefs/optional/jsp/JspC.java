package org.apache.tools.ant.taskdefs.optional.jsp;

import java.io.File;
import java.time.Instant;
import java.util.Vector;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.taskdefs.optional.jsp.compilers.JspCompilerAdapter;
import org.apache.tools.ant.taskdefs.optional.jsp.compilers.JspCompilerAdapterFactory;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

public class JspC
  extends MatchingTask
{
  private Path classpath;
  private Path compilerClasspath;
  private Path src;
  private File destDir;
  private String packageName;
  private String compilerName = "jasper";
  private String iepluginid;
  private boolean mapped;
  private int verbose = 0;
  protected Vector<String> compileList = new Vector();
  Vector<File> javaFiles = new Vector();
  protected boolean failOnError = true;
  private File uriroot;
  private File webinc;
  private File webxml;
  protected WebAppParameter webApp;
  private static final String FAIL_MSG = "Compile failed, messages should have been provided.";
  
  public void setSrcDir(Path srcDir)
  {
    if (src == null) {
      src = srcDir;
    } else {
      src.append(srcDir);
    }
  }
  
  public Path getSrcDir()
  {
    return src;
  }
  
  public void setDestdir(File destDir)
  {
    this.destDir = destDir;
  }
  
  public File getDestdir()
  {
    return destDir;
  }
  
  public void setPackage(String pkg)
  {
    packageName = pkg;
  }
  
  public String getPackage()
  {
    return packageName;
  }
  
  public void setVerbose(int i)
  {
    verbose = i;
  }
  
  public int getVerbose()
  {
    return verbose;
  }
  
  public void setFailonerror(boolean fail)
  {
    failOnError = fail;
  }
  
  public boolean getFailonerror()
  {
    return failOnError;
  }
  
  public String getIeplugin()
  {
    return iepluginid;
  }
  
  public void setIeplugin(String iepluginid)
  {
    this.iepluginid = iepluginid;
  }
  
  public boolean isMapped()
  {
    return mapped;
  }
  
  public void setMapped(boolean mapped)
  {
    this.mapped = mapped;
  }
  
  public void setUribase(File uribase)
  {
    log("Uribase is currently an unused parameter", 1);
  }
  
  public File getUribase()
  {
    return uriroot;
  }
  
  public void setUriroot(File uriroot)
  {
    this.uriroot = uriroot;
  }
  
  public File getUriroot()
  {
    return uriroot;
  }
  
  public void setClasspath(Path cp)
  {
    if (classpath == null) {
      classpath = cp;
    } else {
      classpath.append(cp);
    }
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
  
  public Path getClasspath()
  {
    return classpath;
  }
  
  public void setCompilerclasspath(Path cp)
  {
    if (compilerClasspath == null) {
      compilerClasspath = cp;
    } else {
      compilerClasspath.append(cp);
    }
  }
  
  public Path getCompilerclasspath()
  {
    return compilerClasspath;
  }
  
  public Path createCompilerclasspath()
  {
    if (compilerClasspath == null) {
      compilerClasspath = new Path(getProject());
    }
    return compilerClasspath.createPath();
  }
  
  public void setWebxml(File webxml)
  {
    this.webxml = webxml;
  }
  
  public File getWebxml()
  {
    return webxml;
  }
  
  public void setWebinc(File webinc)
  {
    this.webinc = webinc;
  }
  
  public File getWebinc()
  {
    return webinc;
  }
  
  public void addWebApp(WebAppParameter webappParam)
    throws BuildException
  {
    if (webApp == null) {
      webApp = webappParam;
    } else {
      throw new BuildException("Only one webapp can be specified");
    }
  }
  
  public WebAppParameter getWebApp()
  {
    return webApp;
  }
  
  public void setCompiler(String compiler)
  {
    compilerName = compiler;
  }
  
  public Vector<String> getCompileList()
  {
    return compileList;
  }
  
  public void execute()
    throws BuildException
  {
    if (destDir == null) {
      throw new BuildException("destdir attribute must be set!", getLocation());
    }
    if (!destDir.isDirectory()) {
      throw new BuildException("destination directory \"" + destDir + "\" does not exist or is not a directory", getLocation());
    }
    File dest = getActualDestDir();
    
    AntClassLoader al = getProject().createClassLoader(compilerClasspath);
    try
    {
      JspCompilerAdapter compiler = JspCompilerAdapterFactory.getCompiler(compilerName, this, al);
      if (webApp != null)
      {
        doCompilation(compiler);
        if (al != null) {
          al.close();
        }
      }
      else
      {
        if (src == null) {
          throw new BuildException("srcdir attribute must be set!", getLocation());
        }
        String[] list = src.list();
        if (list.length == 0) {
          throw new BuildException("srcdir attribute must be set!", getLocation());
        }
        if (compiler.implementsOwnDependencyChecking())
        {
          doCompilation(compiler);
          if (al != null) {
            al.close();
          }
        }
        else
        {
          JspMangler mangler = compiler.createMangler();
          
          resetFileLists();
          int filecount = 0;
          for (String fileName : list)
          {
            File srcDir = getProject().resolveFile(fileName);
            if (!srcDir.exists()) {
              throw new BuildException("srcdir \"" + srcDir.getPath() + "\" does not exist!", getLocation());
            }
            DirectoryScanner ds = getDirectoryScanner(srcDir);
            String[] files = ds.getIncludedFiles();
            filecount = files.length;
            scanDir(srcDir, dest, mangler, files);
          }
          log("compiling " + compileList.size() + " files", 3);
          if (!compileList.isEmpty())
          {
            log("Compiling " + compileList.size() + " source file" + (
              compileList.size() == 1 ? "" : "s") + " to " + dest);
            
            doCompilation(compiler);
          }
          else if (filecount == 0)
          {
            log("there were no files to compile", 2);
          }
          else
          {
            log("all files are up to date", 3);
          }
          if (al != null) {
            al.close();
          }
        }
      }
    }
    catch (Throwable localThrowable)
    {
      if (al == null) {
        break label498;
      }
    }
    try
    {
      al.close();
    }
    catch (Throwable localThrowable1)
    {
      localThrowable.addSuppressed(localThrowable1);
    }
    label498:
    throw localThrowable;
  }
  
  private File getActualDestDir()
  {
    if (packageName == null) {
      return destDir;
    }
    return new File(destDir.getPath() + File.separatorChar + packageName
      .replace('.', File.separatorChar));
  }
  
  private void doCompilation(JspCompilerAdapter compiler)
    throws BuildException
  {
    compiler.setJspc(this);
    if (!compiler.execute())
    {
      if (failOnError) {
        throw new BuildException("Compile failed, messages should have been provided.", getLocation());
      }
      log("Compile failed, messages should have been provided.", 0);
    }
  }
  
  protected void resetFileLists()
  {
    compileList.removeAllElements();
  }
  
  protected void scanDir(File srcDir, File dest, JspMangler mangler, String[] files)
  {
    long now = Instant.now().toEpochMilli();
    for (String filename : files)
    {
      File srcFile = new File(srcDir, filename);
      File javaFile = mapToJavaFile(mangler, srcFile, srcDir, dest);
      if (javaFile != null)
      {
        if (srcFile.lastModified() > now) {
          log("Warning: file modified in the future: " + filename, 1);
        }
        if (isCompileNeeded(srcFile, javaFile))
        {
          compileList.addElement(srcFile.getAbsolutePath());
          javaFiles.addElement(javaFile);
        }
      }
    }
  }
  
  private boolean isCompileNeeded(File srcFile, File javaFile)
  {
    boolean shouldCompile = false;
    if (!javaFile.exists())
    {
      shouldCompile = true;
      log("Compiling " + srcFile.getPath() + " because java file " + javaFile
        .getPath() + " does not exist", 3);
    }
    else if (srcFile.lastModified() > javaFile.lastModified())
    {
      shouldCompile = true;
      log("Compiling " + srcFile.getPath() + " because it is out of date with respect to " + javaFile
      
        .getPath(), 3);
    }
    else if (javaFile.length() == 0L)
    {
      shouldCompile = true;
      log("Compiling " + srcFile.getPath() + " because java file " + javaFile
        .getPath() + " is empty", 3);
    }
    return shouldCompile;
  }
  
  protected File mapToJavaFile(JspMangler mangler, File srcFile, File srcDir, File dest)
  {
    if (!srcFile.getName().endsWith(".jsp")) {
      return null;
    }
    String javaFileName = mangler.mapJspToJavaName(srcFile);
    return new File(dest, javaFileName);
  }
  
  public void deleteEmptyJavaFiles()
  {
    if (javaFiles != null) {
      for (File file : javaFiles) {
        if ((file.exists()) && (file.length() == 0L))
        {
          log("deleting empty output file " + file);
          file.delete();
        }
      }
    }
  }
  
  public static class WebAppParameter
  {
    private File directory;
    
    public File getDirectory()
    {
      return directory;
    }
    
    public void setBaseDir(File directory)
    {
      this.directory = directory;
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.jsp.JspC
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */