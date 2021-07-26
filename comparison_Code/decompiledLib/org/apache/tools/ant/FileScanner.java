package org.apache.tools.ant;

import java.io.File;

public abstract interface FileScanner
{
  public abstract void addDefaultExcludes();
  
  public abstract File getBasedir();
  
  public abstract String[] getExcludedDirectories();
  
  public abstract String[] getExcludedFiles();
  
  public abstract String[] getIncludedDirectories();
  
  public abstract String[] getIncludedFiles();
  
  public abstract String[] getNotIncludedDirectories();
  
  public abstract String[] getNotIncludedFiles();
  
  public abstract void scan()
    throws IllegalStateException;
  
  public abstract void setBasedir(String paramString);
  
  public abstract void setBasedir(File paramFile);
  
  public abstract void setExcludes(String[] paramArrayOfString);
  
  public abstract void setIncludes(String[] paramArrayOfString);
  
  public abstract void setCaseSensitive(boolean paramBoolean);
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.FileScanner
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */