package org.codehaus.plexus.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class DirectoryWalker
{
  private File baseDir;
  private int baseDirOffset;
  private Stack<DirStackEntry> dirStack;
  private List<String> excludes;
  private List<String> includes;
  
  class DirStackEntry
  {
    public int count;
    public File dir;
    public int index;
    public double percentageOffset;
    public double percentageSize;
    
    public DirStackEntry(File d, int length)
    {
      dir = d;
      count = length;
    }
    
    public double getNextPercentageOffset()
    {
      return percentageOffset + index * (percentageSize / count);
    }
    
    public double getNextPercentageSize()
    {
      return percentageSize / count;
    }
    
    public int getPercentage()
    {
      double percentageWithinDir = index / count;
      return (int)Math.floor(percentageOffset + percentageWithinDir * percentageSize);
    }
    
    public String toString()
    {
      return "DirStackEntry[dir=" + dir.getAbsolutePath() + ",count=" + count + ",index=" + index + ",percentageOffset=" + percentageOffset + ",percentageSize=" + percentageSize + ",percentage()=" + getPercentage() + ",getNextPercentageOffset()=" + getNextPercentageOffset() + ",getNextPercentageSize()=" + getNextPercentageSize() + "]";
    }
  }
  
  private boolean isCaseSensitive = true;
  private List<DirectoryWalkListener> listeners;
  private boolean debugEnabled = false;
  
  public DirectoryWalker()
  {
    includes = new ArrayList();
    excludes = new ArrayList();
    listeners = new ArrayList();
  }
  
  public void addDirectoryWalkListener(DirectoryWalkListener listener)
  {
    listeners.add(listener);
  }
  
  public void addExclude(String exclude)
  {
    excludes.add(fixPattern(exclude));
  }
  
  public void addInclude(String include)
  {
    includes.add(fixPattern(include));
  }
  
  public void addSCMExcludes()
  {
    String[] scmexcludes = DirectoryScanner.DEFAULTEXCLUDES;
    for (String scmexclude : scmexcludes) {
      addExclude(scmexclude);
    }
  }
  
  private void fireStep(File file)
  {
    DirStackEntry dsEntry = (DirStackEntry)dirStack.peek();
    int percentage = dsEntry.getPercentage();
    for (Object listener1 : listeners)
    {
      DirectoryWalkListener listener = (DirectoryWalkListener)listener1;
      listener.directoryWalkStep(percentage, file);
    }
  }
  
  private void fireWalkFinished()
  {
    for (DirectoryWalkListener listener1 : listeners) {
      listener1.directoryWalkFinished();
    }
  }
  
  private void fireWalkStarting()
  {
    for (DirectoryWalkListener listener1 : listeners) {
      listener1.directoryWalkStarting(baseDir);
    }
  }
  
  private void fireDebugMessage(String message)
  {
    for (DirectoryWalkListener listener1 : listeners) {
      listener1.debug(message);
    }
  }
  
  private String fixPattern(String pattern)
  {
    String cleanPattern = pattern;
    if (File.separatorChar != '/') {
      cleanPattern = cleanPattern.replace('/', File.separatorChar);
    }
    if (File.separatorChar != '\\') {
      cleanPattern = cleanPattern.replace('\\', File.separatorChar);
    }
    return cleanPattern;
  }
  
  public void setDebugMode(boolean debugEnabled)
  {
    this.debugEnabled = debugEnabled;
  }
  
  public File getBaseDir()
  {
    return baseDir;
  }
  
  public List<String> getExcludes()
  {
    return excludes;
  }
  
  public List<String> getIncludes()
  {
    return includes;
  }
  
  private boolean isExcluded(String name)
  {
    return isMatch(excludes, name);
  }
  
  private boolean isIncluded(String name)
  {
    return isMatch(includes, name);
  }
  
  private boolean isMatch(List<String> patterns, String name)
  {
    for (String pattern1 : patterns) {
      if (SelectorUtils.matchPath(pattern1, name, isCaseSensitive)) {
        return true;
      }
    }
    return false;
  }
  
  private String relativeToBaseDir(File file)
  {
    return file.getAbsolutePath().substring(baseDirOffset + 1);
  }
  
  public void removeDirectoryWalkListener(DirectoryWalkListener listener)
  {
    listeners.remove(listener);
  }
  
  public void scan()
  {
    if (baseDir == null) {
      throw new IllegalStateException("Scan Failure.  BaseDir not specified.");
    }
    if (!baseDir.exists()) {
      throw new IllegalStateException("Scan Failure.  BaseDir does not exist.");
    }
    if (!baseDir.isDirectory()) {
      throw new IllegalStateException("Scan Failure.  BaseDir is not a directory.");
    }
    if (includes.isEmpty()) {
      addInclude("**");
    }
    if (debugEnabled)
    {
      StringBuilder dbg = new StringBuilder();
      dbg.append("DirectoryWalker Scan");
      dbg.append("\n  Base Dir: ").append(baseDir.getAbsolutePath());
      dbg.append("\n  Includes: ");
      Iterator<String> it = includes.iterator();
      while (it.hasNext())
      {
        String include = (String)it.next();
        dbg.append("\n    - \"").append(include).append("\"");
      }
      dbg.append("\n  Excludes: ");
      it = excludes.iterator();
      while (it.hasNext())
      {
        String exclude = (String)it.next();
        dbg.append("\n    - \"").append(exclude).append("\"");
      }
      fireDebugMessage(dbg.toString());
    }
    fireWalkStarting();
    dirStack = new Stack();
    scanDir(baseDir);
    fireWalkFinished();
  }
  
  private void scanDir(File dir)
  {
    File[] files = dir.listFiles();
    if (files == null) {
      return;
    }
    DirStackEntry curStackEntry = new DirStackEntry(dir, files.length);
    if (dirStack.isEmpty())
    {
      percentageOffset = 0.0D;
      percentageSize = 100.0D;
    }
    else
    {
      DirStackEntry previousStackEntry = (DirStackEntry)dirStack.peek();
      percentageOffset = previousStackEntry.getNextPercentageOffset();
      percentageSize = previousStackEntry.getNextPercentageSize();
    }
    dirStack.push(curStackEntry);
    for (int idx = 0; idx < files.length; idx++)
    {
      index = idx;
      String name = relativeToBaseDir(files[idx]);
      if (isExcluded(name)) {
        fireDebugMessage(name + " is excluded.");
      } else if (files[idx].isDirectory()) {
        scanDir(files[idx]);
      } else if (isIncluded(name)) {
        fireStep(files[idx]);
      }
    }
    dirStack.pop();
  }
  
  public void setBaseDir(File baseDir)
  {
    this.baseDir = baseDir;
    baseDirOffset = baseDir.getAbsolutePath().length();
  }
  
  public void setExcludes(List<String> entries)
  {
    excludes.clear();
    if (entries != null) {
      for (String entry : entries) {
        excludes.add(fixPattern(entry));
      }
    }
  }
  
  public void setIncludes(List<String> entries)
  {
    includes.clear();
    if (entries != null) {
      for (String entry : entries) {
        includes.add(fixPattern(entry));
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.DirectoryWalker
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */