package org.apache.maven.it;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

abstract interface MavenLauncher
{
  public abstract int run(String[] paramArrayOfString, Properties paramProperties, String paramString, File paramFile)
    throws IOException, LauncherException;
  
  public abstract String getMavenVersion()
    throws IOException, LauncherException;
}

/* Location:
 * Qualified Name:     org.apache.maven.it.MavenLauncher
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */