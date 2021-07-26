package org.apache.tools.ant.util.depend;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import org.apache.tools.ant.types.Path;

public abstract interface DependencyAnalyzer
{
  public abstract void addSourcePath(Path paramPath);
  
  public abstract void addClassPath(Path paramPath);
  
  public abstract void addRootClass(String paramString);
  
  public abstract Enumeration<File> getFileDependencies();
  
  public abstract Enumeration<String> getClassDependencies();
  
  public abstract void reset();
  
  public abstract void config(String paramString, Object paramObject);
  
  public abstract void setClosure(boolean paramBoolean);
  
  public abstract File getClassContainer(String paramString)
    throws IOException;
  
  public abstract File getSourceContainer(String paramString)
    throws IOException;
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.depend.DependencyAnalyzer
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */