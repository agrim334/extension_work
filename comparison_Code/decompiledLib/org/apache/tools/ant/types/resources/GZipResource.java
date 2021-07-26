package org.apache.tools.ant.types.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.tools.ant.types.ResourceCollection;

public class GZipResource
  extends CompressedResource
{
  public GZipResource() {}
  
  public GZipResource(ResourceCollection other)
  {
    super(other);
  }
  
  protected InputStream wrapStream(InputStream in)
    throws IOException
  {
    return new GZIPInputStream(in);
  }
  
  protected OutputStream wrapStream(OutputStream out)
    throws IOException
  {
    return new GZIPOutputStream(out);
  }
  
  protected String getCompressionName()
  {
    return "GZip";
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.GZipResource
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */