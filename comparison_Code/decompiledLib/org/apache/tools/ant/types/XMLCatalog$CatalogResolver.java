package org.apache.tools.ant.types;

import javax.xml.transform.URIResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

abstract interface XMLCatalog$CatalogResolver
  extends URIResolver, EntityResolver
{
  public abstract InputSource resolveEntity(String paramString1, String paramString2);
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.XMLCatalog.CatalogResolver
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */