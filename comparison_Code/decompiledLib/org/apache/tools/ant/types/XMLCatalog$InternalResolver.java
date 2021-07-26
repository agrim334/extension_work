package org.apache.tools.ant.types;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;

class XMLCatalog$InternalResolver
  implements XMLCatalog.CatalogResolver
{
  public XMLCatalog$InternalResolver(XMLCatalog paramXMLCatalog)
  {
    paramXMLCatalog.log("Apache resolver library not found, internal resolver will be used", 3);
  }
  
  public InputSource resolveEntity(String publicId, String systemId)
  {
    InputSource result = null;
    ResourceLocation matchingEntry = XMLCatalog.access$000(this$0, publicId);
    if (matchingEntry != null)
    {
      this$0.log("Matching catalog entry found for publicId: '" + matchingEntry
        .getPublicId() + "' location: '" + matchingEntry
        .getLocation() + "'", 4);
      
      result = XMLCatalog.access$100(this$0, matchingEntry);
      if (result == null) {
        result = XMLCatalog.access$200(this$0, matchingEntry);
      }
      if (result == null) {
        result = XMLCatalog.access$300(this$0, matchingEntry);
      }
    }
    return result;
  }
  
  public Source resolve(String href, String base)
    throws TransformerException
  {
    SAXSource result = null;
    InputSource source = null;
    
    ResourceLocation matchingEntry = XMLCatalog.access$000(this$0, href);
    if (matchingEntry != null)
    {
      this$0.log("Matching catalog entry found for uri: '" + matchingEntry
        .getPublicId() + "' location: '" + matchingEntry
        .getLocation() + "'", 4);
      
      ResourceLocation entryCopy = matchingEntry;
      if (base != null) {
        try
        {
          URL baseURL = new URL(base);
          entryCopy = new ResourceLocation();
          entryCopy.setBase(baseURL);
        }
        catch (MalformedURLException localMalformedURLException) {}
      }
      entryCopy.setPublicId(matchingEntry.getPublicId());
      entryCopy.setLocation(matchingEntry.getLocation());
      
      source = XMLCatalog.access$100(this$0, entryCopy);
      if (source == null) {
        source = XMLCatalog.access$200(this$0, entryCopy);
      }
      if (source == null) {
        source = XMLCatalog.access$300(this$0, entryCopy);
      }
      if (source != null) {
        result = new SAXSource(source);
      }
    }
    return result;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.XMLCatalog.InternalResolver
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */