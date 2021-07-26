package org.apache.tools.ant.types.selectors;

public abstract interface SelectorScanner
{
  public abstract void setSelectors(FileSelector[] paramArrayOfFileSelector);
  
  public abstract String[] getDeselectedDirectories();
  
  public abstract String[] getDeselectedFiles();
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.SelectorScanner
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */