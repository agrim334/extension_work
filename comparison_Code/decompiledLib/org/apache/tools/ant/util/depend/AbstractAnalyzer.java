package org.apache.tools.ant.util.depend;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipFile;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.VectorSet;

public abstract class AbstractAnalyzer
  implements DependencyAnalyzer
{
  public static final int MAX_LOOPS = 1000;
  private Path sourcePath = new Path(null);
  private Path classPath = new Path(null);
  private final Vector<String> rootClasses = new VectorSet();
  private boolean determined = false;
  private Vector<File> fileDependencies;
  private Vector<String> classDependencies;
  private boolean closure = true;
  
  protected AbstractAnalyzer()
  {
    reset();
  }
  
  public void setClosure(boolean closure)
  {
    this.closure = closure;
  }
  
  public Enumeration<File> getFileDependencies()
  {
    if (!supportsFileDependencies()) {
      throw new BuildException("File dependencies are not supported by this analyzer");
    }
    if (!determined) {
      determineDependencies(fileDependencies, classDependencies);
    }
    return fileDependencies.elements();
  }
  
  public Enumeration<String> getClassDependencies()
  {
    if (!determined) {
      determineDependencies(fileDependencies, classDependencies);
    }
    return classDependencies.elements();
  }
  
  public File getClassContainer(String classname)
    throws IOException
  {
    String classLocation = classname.replace('.', '/') + ".class";
    
    return getResourceContainer(classLocation, classPath.list());
  }
  
  public File getSourceContainer(String classname)
    throws IOException
  {
    String sourceLocation = classname.replace('.', '/') + ".java";
    
    return getResourceContainer(sourceLocation, sourcePath.list());
  }
  
  public void addSourcePath(Path sourcePath)
  {
    if (sourcePath == null) {
      return;
    }
    this.sourcePath.append(sourcePath);
    this.sourcePath.setProject(sourcePath.getProject());
  }
  
  public void addClassPath(Path classPath)
  {
    if (classPath == null) {
      return;
    }
    this.classPath.append(classPath);
    this.classPath.setProject(classPath.getProject());
  }
  
  public void addRootClass(String className)
  {
    if (className == null) {
      return;
    }
    if (!rootClasses.contains(className)) {
      rootClasses.addElement(className);
    }
  }
  
  public void config(String name, Object info) {}
  
  public void reset()
  {
    rootClasses.removeAllElements();
    determined = false;
    fileDependencies = new Vector();
    classDependencies = new Vector();
  }
  
  protected Enumeration<String> getRootClasses()
  {
    return rootClasses.elements();
  }
  
  protected boolean isClosureRequired()
  {
    return closure;
  }
  
  protected abstract void determineDependencies(Vector<File> paramVector, Vector<String> paramVector1);
  
  protected abstract boolean supportsFileDependencies();
  
  private File getResourceContainer(String resourceLocation, String[] paths)
    throws IOException
  {
    for (String path : paths)
    {
      File element = new File(path);
      if (element.exists()) {
        if (element.isDirectory())
        {
          File resource = new File(element, resourceLocation);
          if (resource.exists()) {
            return resource;
          }
        }
        else
        {
          ZipFile zipFile = new ZipFile(element);
          try
          {
            if (zipFile.getEntry(resourceLocation) != null)
            {
              File localFile1 = element;
              
              zipFile.close();return localFile1;
            }
            zipFile.close();
          }
          catch (Throwable localThrowable2) {}
          try
          {
            zipFile.close();
          }
          catch (Throwable localThrowable1)
          {
            localThrowable2.addSuppressed(localThrowable1);
          }
          throw localThrowable2;
        }
      }
    }
    return null;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.depend.AbstractAnalyzer
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */