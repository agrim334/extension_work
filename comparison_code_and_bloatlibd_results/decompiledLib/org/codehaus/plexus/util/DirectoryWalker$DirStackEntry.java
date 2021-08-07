package org.codehaus.plexus.util;

import java.io.File;

class DirectoryWalker$DirStackEntry
{
  public int count;
  public File dir;
  public int index;
  public double percentageOffset;
  public double percentageSize;
  
  public DirectoryWalker$DirStackEntry(DirectoryWalker paramDirectoryWalker, File d, int length)
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

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.DirectoryWalker.DirStackEntry
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */