package org.codehaus.plexus.util.cli;

import java.io.File;

public abstract interface Arg
{
  public abstract void setValue(String paramString);
  
  public abstract void setLine(String paramString);
  
  public abstract void setFile(File paramFile);
  
  public abstract String[] getParts();
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.cli.Arg
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */