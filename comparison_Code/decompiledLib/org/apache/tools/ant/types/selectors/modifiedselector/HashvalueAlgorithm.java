package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import org.apache.tools.ant.util.FileUtils;

public class HashvalueAlgorithm
  implements Algorithm
{
  public boolean isValid()
  {
    return true;
  }
  
  public String getValue(File file)
  {
    if (!file.canRead()) {
      return null;
    }
    try
    {
      Reader r = new FileReader(file);
      try
      {
        int hash = FileUtils.readFully(r).hashCode();
        String str = Integer.toString(hash);
        r.close();return str;
      }
      catch (Throwable localThrowable)
      {
        try
        {
          r.close();
        }
        catch (Throwable localThrowable2)
        {
          localThrowable.addSuppressed(localThrowable2);
        }
        throw localThrowable;
      }
      return null;
    }
    catch (Exception e) {}
  }
  
  public String toString()
  {
    return "HashvalueAlgorithm";
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.modifiedselector.HashvalueAlgorithm
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */