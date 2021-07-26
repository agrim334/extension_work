package org.apache.tools.ant.types.resources;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipExtraField;
import org.apache.tools.zip.ZipFile;

public class ZipResource
  extends ArchiveResource
{
  private String encoding;
  private ZipExtraField[] extras;
  private int method;
  
  public ZipResource() {}
  
  public ZipResource(File z, String enc, ZipEntry e)
  {
    super(z, true);
    setEncoding(enc);
    setEntry(e);
  }
  
  public void setZipfile(File z)
  {
    setArchive(z);
  }
  
  public File getZipfile()
  {
    FileProvider fp = (FileProvider)getArchive().as(FileProvider.class);
    return fp.getFile();
  }
  
  public void addConfigured(ResourceCollection a)
  {
    super.addConfigured(a);
    if (!a.isFilesystemOnly()) {
      throw new BuildException("only filesystem resources are supported");
    }
  }
  
  public void setEncoding(String enc)
  {
    checkAttributesAllowed();
    encoding = enc;
  }
  
  public String getEncoding()
  {
    return isReference() ? 
      getRef().getEncoding() : encoding;
  }
  
  public void setRefid(Reference r)
  {
    if (encoding != null) {
      throw tooManyAttributes();
    }
    super.setRefid(r);
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    if (isReference()) {
      return getRef().getInputStream();
    }
    return getZipEntryStream(new ZipFile(getZipfile(), getEncoding()), getName());
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    if (isReference()) {
      return getRef().getOutputStream();
    }
    throw new UnsupportedOperationException("Use the zip task for zip output.");
  }
  
  public ZipExtraField[] getExtraFields()
  {
    if (isReference()) {
      return getRef().getExtraFields();
    }
    checkEntry();
    if (extras == null) {
      return new ZipExtraField[0];
    }
    return extras;
  }
  
  public int getMethod()
  {
    return method;
  }
  
  public static InputStream getZipEntryStream(final ZipFile zipFile, String zipEntry)
    throws IOException
  {
    ZipEntry ze = zipFile.getEntry(zipEntry);
    if (ze == null)
    {
      zipFile.close();
      throw new BuildException("no entry " + zipEntry + " in " + zipFile.getName());
    }
    new FilterInputStream(zipFile.getInputStream(ze))
    {
      public void close()
        throws IOException
      {
        FileUtils.close(in);
        zipFile.close();
      }
      
      /* Error */
      protected void finalize()
        throws java.lang.Throwable
      {
        // Byte code:
        //   0: aload_0
        //   1: invokevirtual 27	org/apache/tools/ant/types/resources/ZipResource$1:close	()V
        //   4: aload_0
        //   5: invokespecial 28	java/lang/Object:finalize	()V
        //   8: goto +10 -> 18
        //   11: astore_1
        //   12: aload_0
        //   13: invokespecial 28	java/lang/Object:finalize	()V
        //   16: aload_1
        //   17: athrow
        //   18: return
        // Line number table:
        //   Java source line #198	-> byte code offset #0
        //   Java source line #200	-> byte code offset #4
        //   Java source line #201	-> byte code offset #8
        //   Java source line #200	-> byte code offset #11
        //   Java source line #201	-> byte code offset #16
        //   Java source line #202	-> byte code offset #18
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	19	0	this	1
        //   11	6	1	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   0	4	11	finally
      }
    };
  }
  
  protected void fetchEntry()
  {
    ZipFile z = null;
    try
    {
      z = new ZipFile(getZipfile(), getEncoding());
      setEntry(z.getEntry(getName()));
    }
    catch (IOException e)
    {
      log(e.getMessage(), 4);
      throw new BuildException(e);
    }
    finally
    {
      ZipFile.closeQuietly(z);
    }
  }
  
  protected ZipResource getRef()
  {
    return (ZipResource)getCheckedRef(ZipResource.class);
  }
  
  private void setEntry(ZipEntry e)
  {
    if (e == null)
    {
      setExists(false);
      return;
    }
    setName(e.getName());
    setExists(true);
    setLastModified(e.getTime());
    setDirectory(e.isDirectory());
    setSize(e.getSize());
    setMode(e.getUnixMode());
    extras = e.getExtraFields(true);
    method = e.getMethod();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.ZipResource
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */