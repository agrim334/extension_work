package org.apache.tools.ant.types;

import java.io.IOException;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.resources.TarResource;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

public class TarScanner
  extends ArchiveScanner
{
  protected void fillMapsFromArchive(Resource src, String encoding, Map<String, Resource> fileEntries, Map<String, Resource> matchFileEntries, Map<String, Resource> dirEntries, Map<String, Resource> matchDirEntries)
  {
    try
    {
      TarInputStream ti = new TarInputStream(src.getInputStream(), encoding);
      try
      {
        try
        {
          TarEntry entry = null;
          while ((entry = ti.getNextEntry()) != null)
          {
            Resource r = new TarResource(src, entry);
            String name = entry.getName();
            if (entry.isDirectory())
            {
              name = trimSeparator(name);
              dirEntries.put(name, r);
              if (match(name)) {
                matchDirEntries.put(name, r);
              }
            }
            else
            {
              fileEntries.put(name, r);
              if (match(name)) {
                matchFileEntries.put(name, r);
              }
            }
          }
        }
        catch (IOException ex)
        {
          throw new BuildException("problem reading " + srcFile, ex);
        }
        ti.close();
      }
      catch (Throwable localThrowable) {}
      try
      {
        ti.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      throw localThrowable;
    }
    catch (IOException ex)
    {
      throw new BuildException("problem opening " + srcFile, ex);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.TarScanner
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */