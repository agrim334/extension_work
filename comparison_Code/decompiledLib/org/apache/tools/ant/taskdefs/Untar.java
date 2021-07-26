package org.apache.tools.ant.taskdefs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.zip.GZIPInputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

public class Untar
  extends Expand
{
  private UntarCompressionMethod compression = new UntarCompressionMethod();
  
  public Untar()
  {
    super(null);
  }
  
  public void setCompression(UntarCompressionMethod method)
  {
    compression = method;
  }
  
  public void setScanForUnicodeExtraFields(boolean b)
  {
    throw new BuildException("The " + getTaskName() + " task doesn't support the encoding attribute", getLocation());
  }
  
  protected void expandFile(FileUtils fileUtils, File srcF, File dir)
  {
    if (!srcF.exists()) {
      throw new BuildException("Unable to untar " + srcF + " as the file does not exist", getLocation());
    }
    try
    {
      InputStream fis = Files.newInputStream(srcF.toPath(), new OpenOption[0]);
      try
      {
        expandStream(srcF.getPath(), fis, dir);
        if (fis == null) {
          return;
        }
        fis.close();
      }
      catch (Throwable localThrowable)
      {
        if (fis == null) {
          break label104;
        }
      }
      try
      {
        fis.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      label104:
      throw localThrowable;
    }
    catch (IOException ioe)
    {
      throw new BuildException("Error while expanding " + srcF.getPath() + "\n" + ioe.toString(), ioe, getLocation());
    }
  }
  
  protected void expandResource(Resource srcR, File dir)
  {
    if (!srcR.isExists()) {
      throw new BuildException("Unable to untar " + srcR.getName() + " as the it does not exist", getLocation());
    }
    try
    {
      InputStream i = srcR.getInputStream();
      try
      {
        expandStream(srcR.getName(), i, dir);
        if (i == null) {
          return;
        }
        i.close();
      }
      catch (Throwable localThrowable)
      {
        if (i == null) {
          break label94;
        }
      }
      try
      {
        i.close();
      }
      catch (Throwable localThrowable1)
      {
        localThrowable.addSuppressed(localThrowable1);
      }
      label94:
      throw localThrowable;
    }
    catch (IOException ioe)
    {
      throw new BuildException("Error while expanding " + srcR.getName(), ioe, getLocation());
    }
  }
  
  private void expandStream(String name, InputStream stream, File dir)
    throws IOException
  {
    TarInputStream tis = new TarInputStream(compression.decompress(name, new BufferedInputStream(stream)), getEncoding());
    try
    {
      log("Expanding: " + name + " into " + dir, 2);
      boolean empty = true;
      FileNameMapper mapper = getMapper();
      TarEntry te;
      while ((te = tis.getNextEntry()) != null)
      {
        empty = false;
        extractFile(FileUtils.getFileUtils(), null, dir, tis, te
          .getName(), te.getModTime(), te
          .isDirectory(), mapper);
      }
      if ((empty) && (getFailOnEmptyArchive())) {
        throw new BuildException("archive '%s' is empty", new Object[] { name });
      }
      log("expand complete", 3);
      tis.close();
    }
    catch (Throwable localThrowable) {}
    try
    {
      tis.close();
    }
    catch (Throwable localThrowable1)
    {
      localThrowable.addSuppressed(localThrowable1);
    }
    throw localThrowable;
  }
  
  public static final class UntarCompressionMethod
    extends EnumeratedAttribute
  {
    private static final String NONE = "none";
    private static final String GZIP = "gzip";
    private static final String BZIP2 = "bzip2";
    private static final String XZ = "xz";
    
    public UntarCompressionMethod()
    {
      setValue("none");
    }
    
    public String[] getValues()
    {
      return new String[] { "none", "gzip", "bzip2", "xz" };
    }
    
    public InputStream decompress(String name, InputStream istream)
      throws IOException, BuildException
    {
      String v = getValue();
      if ("gzip".equals(v)) {
        return new GZIPInputStream(istream);
      }
      if ("xz".equals(v)) {
        return newXZInputStream(istream);
      }
      if ("bzip2".equals(v))
      {
        char[] magic = { 'B', 'Z' };
        for (char c : magic) {
          if (istream.read() != c) {
            throw new BuildException("Invalid bz2 file." + name);
          }
        }
        return new CBZip2InputStream(istream);
      }
      return istream;
    }
    
    private static InputStream newXZInputStream(InputStream istream)
      throws BuildException
    {
      try
      {
        Class<? extends InputStream> clazz = Class.forName("org.tukaani.xz.XZInputStream").asSubclass(InputStream.class);
        
        Constructor<? extends InputStream> c = clazz.getConstructor(new Class[] { InputStream.class });
        return (InputStream)c.newInstance(new Object[] { istream });
      }
      catch (ClassNotFoundException ex)
      {
        throw new BuildException("xz decompression requires the XZ for Java library", ex);
      }
      catch (NoSuchMethodException|InstantiationException|IllegalAccessException|InvocationTargetException ex)
      {
        throw new BuildException("failed to create XZInputStream", ex);
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Untar
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */