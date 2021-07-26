package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.BufferedInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;
import org.apache.tools.ant.BuildException;

public class ChecksumAlgorithm
  implements Algorithm
{
  private String algorithm = "CRC";
  private Checksum checksum = null;
  
  public void setAlgorithm(String algorithm)
  {
    this.algorithm = (algorithm != null ? algorithm.toUpperCase(Locale.ENGLISH) : null);
  }
  
  public void initChecksum()
  {
    if (checksum != null) {
      return;
    }
    if ("CRC".equals(algorithm)) {
      checksum = new CRC32();
    } else if ("ADLER".equals(algorithm)) {
      checksum = new Adler32();
    } else {
      throw new BuildException(new NoSuchAlgorithmException());
    }
  }
  
  public boolean isValid()
  {
    return ("CRC".equals(algorithm)) || ("ADLER".equals(algorithm));
  }
  
  public String getValue(File file)
  {
    initChecksum();
    if (file.canRead())
    {
      checksum.reset();
      try
      {
        CheckedInputStream check = new CheckedInputStream(new BufferedInputStream(Files.newInputStream(file.toPath(), new OpenOption[0])), checksum);
        try
        {
          while (check.read() != -1) {}
          String str = Long.toString(check.getChecksum().getValue());
          check.close();return str;
        }
        catch (Throwable localThrowable2)
        {
          try
          {
            check.close();
          }
          catch (Throwable localThrowable1)
          {
            localThrowable2.addSuppressed(localThrowable1);
          }
          throw localThrowable2;
        }
        return null;
      }
      catch (Exception localException) {}
    }
  }
  
  public String toString()
  {
    return String.format("<ChecksumAlgorithm:algorithm=%s>", new Object[] { algorithm });
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.modifiedselector.ChecksumAlgorithm
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */