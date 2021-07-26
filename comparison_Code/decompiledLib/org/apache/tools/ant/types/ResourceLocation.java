package org.apache.tools.ant.types;

import java.net.URL;

public class ResourceLocation
{
  private String publicId = null;
  private String location = null;
  private URL base = null;
  
  public void setPublicId(String publicId)
  {
    this.publicId = publicId;
  }
  
  public void setLocation(String location)
  {
    this.location = location;
  }
  
  public void setBase(URL base)
  {
    this.base = base;
  }
  
  public String getPublicId()
  {
    return publicId;
  }
  
  public String getLocation()
  {
    return location;
  }
  
  public URL getBase()
  {
    return base;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.ResourceLocation
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */