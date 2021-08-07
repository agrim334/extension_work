package org.codehaus.plexus.util.xml;

import java.io.IOException;
import java.io.InputStream;

public class XmlReaderException
  extends IOException
{
  private String _bomEncoding;
  private String _xmlGuessEncoding;
  private String _xmlEncoding;
  private String _contentTypeMime;
  private String _contentTypeEncoding;
  private InputStream _is;
  
  public XmlReaderException(String msg, String bomEnc, String xmlGuessEnc, String xmlEnc, InputStream is)
  {
    this(msg, null, null, bomEnc, xmlGuessEnc, xmlEnc, is);
  }
  
  public XmlReaderException(String msg, String ctMime, String ctEnc, String bomEnc, String xmlGuessEnc, String xmlEnc, InputStream is)
  {
    super(msg);
    _contentTypeMime = ctMime;
    _contentTypeEncoding = ctEnc;
    _bomEncoding = bomEnc;
    _xmlGuessEncoding = xmlGuessEnc;
    _xmlEncoding = xmlEnc;
    _is = is;
  }
  
  public String getBomEncoding()
  {
    return _bomEncoding;
  }
  
  public String getXmlGuessEncoding()
  {
    return _xmlGuessEncoding;
  }
  
  public String getXmlEncoding()
  {
    return _xmlEncoding;
  }
  
  public String getContentTypeMime()
  {
    return _contentTypeMime;
  }
  
  public String getContentTypeEncoding()
  {
    return _contentTypeEncoding;
  }
  
  public InputStream getInputStream()
  {
    return _is;
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.xml.XmlReaderException
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */