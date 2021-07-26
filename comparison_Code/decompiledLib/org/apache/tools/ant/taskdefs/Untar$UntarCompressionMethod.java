package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.zip.GZIPInputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.bzip2.CBZip2InputStream;

public final class Untar$UntarCompressionMethod
  extends EnumeratedAttribute
{
  private static final String NONE = "none";
  private static final String GZIP = "gzip";
  private static final String BZIP2 = "bzip2";
  private static final String XZ = "xz";
  
  public Untar$UntarCompressionMethod()
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

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Untar.UntarCompressionMethod
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */