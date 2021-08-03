package org.apache.maven.it;

import java.io.File;
import java.io.FilenameFilter;

class Verifier$1
  implements FilenameFilter
{
  public boolean accept(File dir, String name)
  {
    return (name.startsWith("maven-metadata")) && (name.endsWith(".xml"));
  }
}

/* Location:
 * Qualified Name:     org.apache.maven.it.Verifier.1
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */