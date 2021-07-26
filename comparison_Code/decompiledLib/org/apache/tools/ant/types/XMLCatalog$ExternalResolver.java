package org.apache.tools.ant.types;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.FileUtils;
import org.xml.sax.InputSource;

class XMLCatalog$ExternalResolver
  implements XMLCatalog.CatalogResolver
{
  private Method setXMLCatalog = null;
  private Method parseCatalog = null;
  private Method resolveEntity = null;
  private Method resolve = null;
  private Object resolverImpl = null;
  private boolean externalCatalogsProcessed = false;
  
  public XMLCatalog$ExternalResolver(Class<?> arg1, Object resolverImplClass)
  {
    this.resolverImpl = resolverImpl;
    try
    {
      setXMLCatalog = resolverImplClass.getMethod("setXMLCatalog", new Class[] { XMLCatalog.class });
      
      parseCatalog = resolverImplClass.getMethod("parseCatalog", new Class[] { String.class });
      
      resolveEntity = resolverImplClass.getMethod("resolveEntity", new Class[] { String.class, String.class });
      
      resolve = resolverImplClass.getMethod("resolve", new Class[] { String.class, String.class });
    }
    catch (NoSuchMethodException ex)
    {
      throw new BuildException(ex);
    }
    ???.log("Apache resolver library found, xml-commons resolver will be used", 3);
  }
  
  public InputSource resolveEntity(String publicId, String systemId)
  {
    processExternalCatalogs();
    
    ResourceLocation matchingEntry = XMLCatalog.access$000(this$0, publicId);
    if (matchingEntry != null)
    {
      this$0.log("Matching catalog entry found for publicId: '" + matchingEntry
        .getPublicId() + "' location: '" + matchingEntry
        .getLocation() + "'", 4);
      
      InputSource result = XMLCatalog.access$100(this$0, matchingEntry);
      if (result == null) {
        result = XMLCatalog.access$200(this$0, matchingEntry);
      }
      if (result == null) {
        try
        {
          result = (InputSource)resolveEntity.invoke(resolverImpl, new Object[] { publicId, systemId });
        }
        catch (Exception ex)
        {
          throw new BuildException(ex);
        }
      }
    }
    else
    {
      try
      {
        result = (InputSource)resolveEntity.invoke(resolverImpl, new Object[] { publicId, systemId });
      }
      catch (Exception ex)
      {
        InputSource result;
        throw new BuildException(ex);
      }
    }
    InputSource result;
    return result;
  }
  
  public Source resolve(String href, String base)
    throws TransformerException
  {
    processExternalCatalogs();
    
    ResourceLocation matchingEntry = XMLCatalog.access$000(this$0, href);
    SAXSource result;
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
        catch (MalformedURLException localMalformedURLException1) {}
      }
      entryCopy.setPublicId(matchingEntry.getPublicId());
      entryCopy.setLocation(matchingEntry.getLocation());
      
      InputSource source = XMLCatalog.access$100(this$0, entryCopy);
      if (source == null) {
        source = XMLCatalog.access$200(this$0, entryCopy);
      }
      SAXSource result;
      if (source != null) {
        result = new SAXSource(source);
      } else {
        try
        {
          result = (SAXSource)resolve.invoke(resolverImpl, new Object[] { href, base });
        }
        catch (Exception ex)
        {
          SAXSource result;
          throw new BuildException(ex);
        }
      }
    }
    else
    {
      if (base == null) {
        try
        {
          base = XMLCatalog.access$400().getFileURL(this$0.getProject().getBaseDir()).toString();
        }
        catch (MalformedURLException x)
        {
          throw new TransformerException(x);
        }
      }
      try
      {
        result = (SAXSource)resolve.invoke(resolverImpl, new Object[] { href, base });
      }
      catch (Exception ex)
      {
        SAXSource result;
        throw new BuildException(ex);
      }
    }
    SAXSource result;
    return result;
  }
  
  private void processExternalCatalogs()
  {
    if (!externalCatalogsProcessed)
    {
      try
      {
        setXMLCatalog.invoke(resolverImpl, new Object[] { this$0 });
      }
      catch (Exception ex)
      {
        throw new BuildException(ex);
      }
      Path catPath = this$0.getCatalogPath();
      if (catPath != null)
      {
        this$0.log("Using catalogpath '" + this$0.getCatalogPath() + "'", 4);
        for (String catFileName : this$0.getCatalogPath().list())
        {
          File catFile = new File(catFileName);
          this$0.log("Parsing " + catFile, 4);
          try
          {
            parseCatalog.invoke(resolverImpl, new Object[] { catFile.getPath() });
          }
          catch (Exception ex)
          {
            throw new BuildException(ex);
          }
        }
      }
    }
    externalCatalogsProcessed = true;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.XMLCatalog.ExternalResolver
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */