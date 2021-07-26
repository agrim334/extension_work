package org.apache.tools.zip;

import java.io.Serializable;

public class UnsupportedZipFeatureException$Feature
  implements Serializable
{
  public static final Feature ENCRYPTION = new Feature("encryption");
  public static final Feature METHOD = new Feature("compression method");
  public static final Feature DATA_DESCRIPTOR = new Feature("data descriptor");
  private final String name;
  
  private UnsupportedZipFeatureException$Feature(String name)
  {
    this.name = name;
  }
  
  public String toString()
  {
    return name;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.zip.UnsupportedZipFeatureException.Feature
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */