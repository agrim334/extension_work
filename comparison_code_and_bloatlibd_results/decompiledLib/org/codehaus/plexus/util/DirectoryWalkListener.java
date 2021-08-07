package org.codehaus.plexus.util;

import java.io.File;

public abstract interface DirectoryWalkListener
{
  public abstract void directoryWalkStarting(File paramFile);
  
  public abstract void directoryWalkStep(int paramInt, File paramFile);
  
  public abstract void directoryWalkFinished();
  
  public abstract void debug(String paramString);
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.DirectoryWalkListener
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */