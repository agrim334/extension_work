package org.codehaus.plexus.util;

import java.io.File;
import java.util.Comparator;

public abstract interface Scanner
{
  public abstract void setIncludes(String[] paramArrayOfString);
  
  public abstract void setExcludes(String[] paramArrayOfString);
  
  public abstract void addDefaultExcludes();
  
  public abstract void scan();
  
  public abstract String[] getIncludedFiles();
  
  public abstract String[] getIncludedDirectories();
  
  public abstract File getBasedir();
  
  public abstract void setFilenameComparator(Comparator<String> paramComparator);
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.Scanner
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */