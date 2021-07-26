package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.zip.GZIPOutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.bzip2.CBZip2OutputStream;

public final class Tar$TarCompressionMethod
  extends EnumeratedAttribute
{
  private static final String NONE = "none";
  private static final String GZIP = "gzip";
  private static final String BZIP2 = "bzip2";
  private static final String XZ = "xz";
  
  public Tar$TarCompressionMethod()
  {
    setValue("none");
  }
  
  public String[] getValues()
  {
    return new String[] { "none", "gzip", "bzip2", "xz" };
  }
  
  private OutputStream compress(OutputStream ostream)
    throws IOException
  {
    String v = getValue();
    if ("gzip".equals(v)) {
      return new GZIPOutputStream(ostream);
    }
    if ("xz".equals(v)) {
      return newXZOutputStream(ostream);
    }
    if ("bzip2".equals(v))
    {
      ostream.write(66);
      ostream.write(90);
      return new CBZip2OutputStream(ostream);
    }
    return ostream;
  }
  
  private static OutputStream newXZOutputStream(OutputStream ostream)
    throws BuildException
  {
    try
    {
      Class<?> fClazz = Class.forName("org.tukaani.xz.FilterOptions");
      Class<?> oClazz = Class.forName("org.tukaani.xz.LZMA2Options");
      
      Class<? extends OutputStream> sClazz = Class.forName("org.tukaani.xz.XZOutputStream").asSubclass(OutputStream.class);
      
      Constructor<? extends OutputStream> c = sClazz.getConstructor(new Class[] { OutputStream.class, fClazz });
      return (OutputStream)c.newInstance(new Object[] { ostream, oClazz.newInstance() });
    }
    catch (ClassNotFoundException ex)
    {
      throw new BuildException("xz compression requires the XZ for Java library", ex);
    }
    catch (NoSuchMethodException|InstantiationException|IllegalAccessException|InvocationTargetException ex)
    {
      throw new BuildException("failed to create XZOutputStream", ex);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Tar.TarCompressionMethod
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */