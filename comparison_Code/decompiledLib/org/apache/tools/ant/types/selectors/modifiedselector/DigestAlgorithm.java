package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.security.DigestInputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Locale;
import org.apache.tools.ant.BuildException;

public class DigestAlgorithm
  implements Algorithm
{
  private static final int BYTE_MASK = 255;
  private static final int BUFFER_SIZE = 8192;
  private String algorithm = "MD5";
  private String provider = null;
  private MessageDigest messageDigest = null;
  private int readBufferSize = 8192;
  
  public void setAlgorithm(String algorithm)
  {
    this.algorithm = (algorithm != null ? algorithm.toUpperCase(Locale.ENGLISH) : null);
  }
  
  public void setProvider(String provider)
  {
    this.provider = provider;
  }
  
  public void initMessageDigest()
  {
    if (messageDigest != null) {
      return;
    }
    if ((provider != null) && (!provider.isEmpty()) && (!"null".equals(provider))) {
      try
      {
        messageDigest = MessageDigest.getInstance(algorithm, provider);
      }
      catch (NoSuchAlgorithmException|NoSuchProviderException e)
      {
        throw new BuildException(e);
      }
    } else {
      try
      {
        messageDigest = MessageDigest.getInstance(algorithm);
      }
      catch (NoSuchAlgorithmException noalgo)
      {
        throw new BuildException(noalgo);
      }
    }
  }
  
  public boolean isValid()
  {
    return ("SHA".equals(algorithm)) || ("MD5".equals(algorithm));
  }
  
  public String getValue(File file)
  {
    if (!file.canRead()) {
      return null;
    }
    initMessageDigest();
    byte[] buf = new byte[readBufferSize];
    messageDigest.reset();
    try
    {
      DigestInputStream dis = new DigestInputStream(Files.newInputStream(file.toPath(), new OpenOption[0]), messageDigest);
      try
      {
        while (dis.read(buf, 0, readBufferSize) != -1) {}
        StringBuilder checksumSb = new StringBuilder();
        for (byte digestByte : messageDigest.digest()) {
          checksumSb.append(String.format("%02x", new Object[] { Integer.valueOf(0xFF & digestByte) }));
        }
        ??? = checksumSb.toString();
        dis.close();return (String)???;
      }
      catch (Throwable localThrowable)
      {
        try
        {
          dis.close();
        }
        catch (Throwable localThrowable2)
        {
          localThrowable.addSuppressed(localThrowable2);
        }
        throw localThrowable;
      }
      return null;
    }
    catch (IOException ignored) {}
  }
  
  public String toString()
  {
    return String.format("<DigestAlgorithm:algorithm=%s;provider=%s>", new Object[] { algorithm, provider });
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.modifiedselector.DigestAlgorithm
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */