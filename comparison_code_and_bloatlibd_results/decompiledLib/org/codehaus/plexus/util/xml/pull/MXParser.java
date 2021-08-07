package org.codehaus.plexus.util.xml.pull;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import org.codehaus.plexus.util.ReaderFactory;

public class MXParser
  implements XmlPullParser
{
  protected static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
  protected static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
  protected static final String FEATURE_XML_ROUNDTRIP = "http://xmlpull.org/v1/doc/features.html#xml-roundtrip";
  protected static final String FEATURE_NAMES_INTERNED = "http://xmlpull.org/v1/doc/features.html#names-interned";
  protected static final String PROPERTY_XMLDECL_VERSION = "http://xmlpull.org/v1/doc/properties.html#xmldecl-version";
  protected static final String PROPERTY_XMLDECL_STANDALONE = "http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone";
  protected static final String PROPERTY_XMLDECL_CONTENT = "http://xmlpull.org/v1/doc/properties.html#xmldecl-content";
  protected static final String PROPERTY_LOCATION = "http://xmlpull.org/v1/doc/properties.html#location";
  protected boolean allStringsInterned;
  private static final boolean TRACE_SIZING = false;
  protected boolean processNamespaces;
  protected boolean roundtripSupported;
  protected String location;
  protected int lineNumber;
  protected int columnNumber;
  protected boolean seenRoot;
  protected boolean reachedEnd;
  protected int eventType;
  protected boolean emptyElementTag;
  protected int depth;
  protected char[][] elRawName;
  protected int[] elRawNameEnd;
  protected int[] elRawNameLine;
  protected String[] elName;
  protected String[] elPrefix;
  protected String[] elUri;
  protected int[] elNamespaceCount;
  protected int attributeCount;
  protected String[] attributeName;
  protected int[] attributeNameHash;
  protected String[] attributePrefix;
  protected String[] attributeUri;
  protected String[] attributeValue;
  protected int namespaceEnd;
  protected String[] namespacePrefix;
  protected int[] namespacePrefixHash;
  protected String[] namespaceUri;
  protected int entityEnd;
  protected String[] entityName;
  protected char[][] entityNameBuf;
  protected String[] entityReplacement;
  protected char[][] entityReplacementBuf;
  protected int[] entityNameHash;
  private final EntityReplacementMap replacementMapTemplate;
  protected static final int READ_CHUNK_SIZE = 8192;
  protected Reader reader;
  protected String inputEncoding;
  
  protected String newString(char[] cbuf, int off, int len)
  {
    return new String(cbuf, off, len);
  }
  
  protected String newStringIntern(char[] cbuf, int off, int len)
  {
    return new String(cbuf, off, len).intern();
  }
  
  protected void ensureElementsCapacity()
  {
    int elStackSize = elName != null ? elName.length : 0;
    if (depth + 1 >= elStackSize)
    {
      int newSize = (depth >= 7 ? 2 * depth : 8) + 2;
      
      boolean needsCopying = elStackSize > 0;
      String[] arr = null;
      
      arr = new String[newSize];
      if (needsCopying) {
        System.arraycopy(elName, 0, arr, 0, elStackSize);
      }
      elName = arr;
      arr = new String[newSize];
      if (needsCopying) {
        System.arraycopy(elPrefix, 0, arr, 0, elStackSize);
      }
      elPrefix = arr;
      arr = new String[newSize];
      if (needsCopying) {
        System.arraycopy(elUri, 0, arr, 0, elStackSize);
      }
      elUri = arr;
      
      int[] iarr = new int[newSize];
      if (needsCopying) {
        System.arraycopy(elNamespaceCount, 0, iarr, 0, elStackSize);
      } else {
        iarr[0] = 0;
      }
      elNamespaceCount = iarr;
      
      iarr = new int[newSize];
      if (needsCopying) {
        System.arraycopy(elRawNameEnd, 0, iarr, 0, elStackSize);
      }
      elRawNameEnd = iarr;
      
      iarr = new int[newSize];
      if (needsCopying) {
        System.arraycopy(elRawNameLine, 0, iarr, 0, elStackSize);
      }
      elRawNameLine = iarr;
      
      char[][] carr = new char[newSize][];
      if (needsCopying) {
        System.arraycopy(elRawName, 0, carr, 0, elStackSize);
      }
      elRawName = carr;
    }
  }
  
  protected void ensureAttributesCapacity(int size)
  {
    int attrPosSize = attributeName != null ? attributeName.length : 0;
    if (size >= attrPosSize)
    {
      int newSize = size > 7 ? 2 * size : 8;
      
      boolean needsCopying = attrPosSize > 0;
      String[] arr = null;
      
      arr = new String[newSize];
      if (needsCopying) {
        System.arraycopy(attributeName, 0, arr, 0, attrPosSize);
      }
      attributeName = arr;
      
      arr = new String[newSize];
      if (needsCopying) {
        System.arraycopy(attributePrefix, 0, arr, 0, attrPosSize);
      }
      attributePrefix = arr;
      
      arr = new String[newSize];
      if (needsCopying) {
        System.arraycopy(attributeUri, 0, arr, 0, attrPosSize);
      }
      attributeUri = arr;
      
      arr = new String[newSize];
      if (needsCopying) {
        System.arraycopy(attributeValue, 0, arr, 0, attrPosSize);
      }
      attributeValue = arr;
      if (!allStringsInterned)
      {
        int[] iarr = new int[newSize];
        if (needsCopying) {
          System.arraycopy(attributeNameHash, 0, iarr, 0, attrPosSize);
        }
        attributeNameHash = iarr;
      }
      arr = null;
    }
  }
  
  protected void ensureNamespacesCapacity(int size)
  {
    int namespaceSize = namespacePrefix != null ? namespacePrefix.length : 0;
    if (size >= namespaceSize)
    {
      int newSize = size > 7 ? 2 * size : 8;
      
      String[] newNamespacePrefix = new String[newSize];
      String[] newNamespaceUri = new String[newSize];
      if (namespacePrefix != null)
      {
        System.arraycopy(namespacePrefix, 0, newNamespacePrefix, 0, namespaceEnd);
        System.arraycopy(namespaceUri, 0, newNamespaceUri, 0, namespaceEnd);
      }
      namespacePrefix = newNamespacePrefix;
      namespaceUri = newNamespaceUri;
      if (!allStringsInterned)
      {
        int[] newNamespacePrefixHash = new int[newSize];
        if (namespacePrefixHash != null) {
          System.arraycopy(namespacePrefixHash, 0, newNamespacePrefixHash, 0, namespaceEnd);
        }
        namespacePrefixHash = newNamespacePrefixHash;
      }
    }
  }
  
  protected static final int fastHash(char[] ch, int off, int len)
  {
    if (len == 0) {
      return 0;
    }
    int hash = ch[off];
    
    hash = (hash << 7) + ch[(off + len - 1)];
    if (len > 16) {
      hash = (hash << 7) + ch[(off + len / 4)];
    }
    if (len > 8) {
      hash = (hash << 7) + ch[(off + len / 2)];
    }
    return hash;
  }
  
  protected void ensureEntityCapacity()
  {
    int entitySize = entityReplacementBuf != null ? entityReplacementBuf.length : 0;
    if (entityEnd >= entitySize)
    {
      int newSize = entityEnd > 7 ? 2 * entityEnd : 8;
      
      String[] newEntityName = new String[newSize];
      char[][] newEntityNameBuf = new char[newSize][];
      String[] newEntityReplacement = new String[newSize];
      char[][] newEntityReplacementBuf = new char[newSize][];
      if (entityName != null)
      {
        System.arraycopy(entityName, 0, newEntityName, 0, entityEnd);
        System.arraycopy(entityNameBuf, 0, newEntityNameBuf, 0, entityEnd);
        System.arraycopy(entityReplacement, 0, newEntityReplacement, 0, entityEnd);
        System.arraycopy(entityReplacementBuf, 0, newEntityReplacementBuf, 0, entityEnd);
      }
      entityName = newEntityName;
      entityNameBuf = newEntityNameBuf;
      entityReplacement = newEntityReplacement;
      entityReplacementBuf = newEntityReplacementBuf;
      if (!allStringsInterned)
      {
        int[] newEntityNameHash = new int[newSize];
        if (entityNameHash != null) {
          System.arraycopy(entityNameHash, 0, newEntityNameHash, 0, entityEnd);
        }
        entityNameHash = newEntityNameHash;
      }
    }
  }
  
  protected int bufLoadFactor = 95;
  protected char[] buf = new char[Runtime.getRuntime().freeMemory() > 1000000L ? ' ' : 'Ā'];
  protected int bufSoftLimit = bufLoadFactor * buf.length / 100;
  protected boolean preventBufferCompaction;
  protected int bufAbsoluteStart;
  protected int bufStart;
  protected int bufEnd;
  protected int pos;
  protected int posStart;
  protected int posEnd;
  protected char[] pc = new char[Runtime.getRuntime().freeMemory() > 1000000L ? 8192 : 64];
  protected int pcStart;
  protected int pcEnd;
  protected boolean usePC;
  protected boolean seenStartTag;
  protected boolean seenEndTag;
  protected boolean pastEndTag;
  protected boolean seenAmpersand;
  protected boolean seenMarkup;
  protected boolean seenDocdecl;
  protected boolean tokenize;
  protected String text;
  protected String entityRefName;
  protected String xmlDeclVersion;
  protected Boolean xmlDeclStandalone;
  protected String xmlDeclContent;
  
  protected void reset()
  {
    location = null;
    lineNumber = 1;
    columnNumber = 0;
    seenRoot = false;
    reachedEnd = false;
    eventType = 0;
    emptyElementTag = false;
    
    depth = 0;
    
    attributeCount = 0;
    
    namespaceEnd = 0;
    
    entityEnd = 0;
    setupFromTemplate();
    
    reader = null;
    inputEncoding = null;
    
    preventBufferCompaction = false;
    bufAbsoluteStart = 0;
    bufEnd = (bufStart = 0);
    pos = (posStart = posEnd = 0);
    
    pcEnd = (pcStart = 0);
    
    usePC = false;
    
    seenStartTag = false;
    seenEndTag = false;
    pastEndTag = false;
    seenAmpersand = false;
    seenMarkup = false;
    seenDocdecl = false;
    
    xmlDeclVersion = null;
    xmlDeclStandalone = null;
    xmlDeclContent = null;
    
    resetStringCache();
  }
  
  public MXParser()
  {
    replacementMapTemplate = null;
  }
  
  public MXParser(EntityReplacementMap entityReplacementMap)
  {
    replacementMapTemplate = entityReplacementMap;
  }
  
  public void setupFromTemplate()
  {
    if (replacementMapTemplate != null)
    {
      int length = replacementMapTemplate.entityEnd;
      
      entityName = replacementMapTemplate.entityName;
      entityNameBuf = replacementMapTemplate.entityNameBuf;
      entityReplacement = replacementMapTemplate.entityReplacement;
      entityReplacementBuf = replacementMapTemplate.entityReplacementBuf;
      entityNameHash = replacementMapTemplate.entityNameHash;
      entityEnd = length;
    }
  }
  
  public void setFeature(String name, boolean state)
    throws XmlPullParserException
  {
    if (name == null) {
      throw new IllegalArgumentException("feature name should not be null");
    }
    if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(name))
    {
      if (eventType != 0) {
        throw new XmlPullParserException("namespace processing feature can only be changed before parsing", this, null);
      }
      processNamespaces = state;
    }
    else if ("http://xmlpull.org/v1/doc/features.html#names-interned".equals(name))
    {
      if (state) {
        throw new XmlPullParserException("interning names in this implementation is not supported");
      }
    }
    else if ("http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(name))
    {
      if (state) {
        throw new XmlPullParserException("processing DOCDECL is not supported");
      }
    }
    else if ("http://xmlpull.org/v1/doc/features.html#xml-roundtrip".equals(name))
    {
      roundtripSupported = state;
    }
    else
    {
      throw new XmlPullParserException("unsupported feature " + name);
    }
  }
  
  public boolean getFeature(String name)
  {
    if (name == null) {
      throw new IllegalArgumentException("feature name should not be null");
    }
    if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(name)) {
      return processNamespaces;
    }
    if ("http://xmlpull.org/v1/doc/features.html#names-interned".equals(name)) {
      return false;
    }
    if ("http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(name)) {
      return false;
    }
    if ("http://xmlpull.org/v1/doc/features.html#xml-roundtrip".equals(name)) {
      return roundtripSupported;
    }
    return false;
  }
  
  public void setProperty(String name, Object value)
    throws XmlPullParserException
  {
    if ("http://xmlpull.org/v1/doc/properties.html#location".equals(name)) {
      location = ((String)value);
    } else {
      throw new XmlPullParserException("unsupported property: '" + name + "'");
    }
  }
  
  public Object getProperty(String name)
  {
    if (name == null) {
      throw new IllegalArgumentException("property name should not be null");
    }
    if ("http://xmlpull.org/v1/doc/properties.html#xmldecl-version".equals(name)) {
      return xmlDeclVersion;
    }
    if ("http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone".equals(name)) {
      return xmlDeclStandalone;
    }
    if ("http://xmlpull.org/v1/doc/properties.html#xmldecl-content".equals(name)) {
      return xmlDeclContent;
    }
    if ("http://xmlpull.org/v1/doc/properties.html#location".equals(name)) {
      return location;
    }
    return null;
  }
  
  public void setInput(Reader in)
    throws XmlPullParserException
  {
    reset();
    reader = in;
  }
  
  public void setInput(InputStream inputStream, String inputEncoding)
    throws XmlPullParserException
  {
    if (inputStream == null) {
      throw new IllegalArgumentException("input stream can not be null");
    }
    try
    {
      Reader reader;
      if (inputEncoding != null) {
        reader = ReaderFactory.newReader(inputStream, inputEncoding);
      } else {
        reader = ReaderFactory.newXmlReader(inputStream);
      }
    }
    catch (UnsupportedEncodingException une)
    {
      Reader reader;
      throw new XmlPullParserException("could not create reader for encoding " + inputEncoding + " : " + une, this, une);
    }
    catch (IOException e)
    {
      throw new XmlPullParserException("could not create reader : " + e, this, e);
    }
    Reader reader;
    setInput(reader);
    
    this.inputEncoding = inputEncoding;
  }
  
  public String getInputEncoding()
  {
    return inputEncoding;
  }
  
  public void defineEntityReplacementText(String entityName, String replacementText)
    throws XmlPullParserException
  {
    if ((!replacementText.startsWith("&#")) && (this.entityName != null) && (replacementText.length() > 1))
    {
      String tmp = replacementText.substring(1, replacementText.length() - 1);
      for (int i = 0; i < this.entityName.length; i++) {
        if ((this.entityName[i] != null) && (this.entityName[i].equals(tmp))) {
          replacementText = entityReplacement[i];
        }
      }
    }
    ensureEntityCapacity();
    
    char[] entityNameCharData = entityName.toCharArray();
    this.entityName[entityEnd] = newString(entityNameCharData, 0, entityName.length());
    entityNameBuf[entityEnd] = entityNameCharData;
    
    entityReplacement[entityEnd] = replacementText;
    entityReplacementBuf[entityEnd] = replacementText.toCharArray();
    if (!allStringsInterned) {
      entityNameHash[entityEnd] = fastHash(entityNameBuf[entityEnd], 0, entityNameBuf[entityEnd].length);
    }
    entityEnd += 1;
  }
  
  public int getNamespaceCount(int depth)
    throws XmlPullParserException
  {
    if ((!processNamespaces) || (depth == 0)) {
      return 0;
    }
    if ((depth < 0) || (depth > this.depth)) {
      throw new IllegalArgumentException("namespace count may be for depth 0.." + this.depth + " not " + depth);
    }
    return elNamespaceCount[depth];
  }
  
  public String getNamespacePrefix(int pos)
    throws XmlPullParserException
  {
    if (pos < namespaceEnd) {
      return namespacePrefix[pos];
    }
    throw new XmlPullParserException("position " + pos + " exceeded number of available namespaces " + namespaceEnd);
  }
  
  public String getNamespaceUri(int pos)
    throws XmlPullParserException
  {
    if (pos < namespaceEnd) {
      return namespaceUri[pos];
    }
    throw new XmlPullParserException("position " + pos + " exceeded number of available namespaces " + namespaceEnd);
  }
  
  public String getNamespace(String prefix)
  {
    if (prefix != null)
    {
      for (int i = namespaceEnd - 1; i >= 0; i--) {
        if (prefix.equals(namespacePrefix[i])) {
          return namespaceUri[i];
        }
      }
      if ("xml".equals(prefix)) {
        return "http://www.w3.org/XML/1998/namespace";
      }
      if ("xmlns".equals(prefix)) {
        return "http://www.w3.org/2000/xmlns/";
      }
    }
    else
    {
      for (int i = namespaceEnd - 1; i >= 0; i--) {
        if (namespacePrefix[i] == null) {
          return namespaceUri[i];
        }
      }
    }
    return null;
  }
  
  public int getDepth()
  {
    return depth;
  }
  
  private static int findFragment(int bufMinPos, char[] b, int start, int end)
  {
    if (start < bufMinPos)
    {
      start = bufMinPos;
      if (start > end) {
        start = end;
      }
      return start;
    }
    if (end - start > 65) {
      start = end - 10;
    }
    int i = start + 1;
    for (;;)
    {
      i--;
      if (i <= bufMinPos) {
        break;
      }
      if (end - i > 65) {
        break;
      }
      char c = b[i];
      if ((c == '<') && (start - i > 10)) {
        break;
      }
    }
    return i;
  }
  
  public String getPositionDescription()
  {
    String fragment = null;
    if (posStart <= pos)
    {
      int start = findFragment(0, buf, posStart, pos);
      if (start < pos) {
        fragment = new String(buf, start, pos - start);
      }
      if ((bufAbsoluteStart > 0) || (start > 0)) {
        fragment = "..." + fragment;
      }
    }
    return " " + TYPES[eventType] + (fragment != null ? " seen " + printable(fragment) + "..." : "") + " " + (location != null ? location : "") + "@" + getLineNumber() + ":" + getColumnNumber();
  }
  
  public int getLineNumber()
  {
    return lineNumber;
  }
  
  public int getColumnNumber()
  {
    return columnNumber;
  }
  
  public boolean isWhitespace()
    throws XmlPullParserException
  {
    if ((eventType == 4) || (eventType == 5))
    {
      if (usePC)
      {
        for (int i = pcStart; i < pcEnd; i++) {
          if (!isS(pc[i])) {
            return false;
          }
        }
        return true;
      }
      for (int i = posStart; i < posEnd; i++) {
        if (!isS(buf[i])) {
          return false;
        }
      }
      return true;
    }
    if (eventType == 7) {
      return true;
    }
    throw new XmlPullParserException("no content available to check for whitespaces");
  }
  
  public String getText()
  {
    if ((eventType == 0) || (eventType == 1)) {
      return null;
    }
    if (eventType == 6) {
      return text;
    }
    if (text == null) {
      if ((!usePC) || (eventType == 2) || (eventType == 3)) {
        text = new String(buf, posStart, posEnd - posStart);
      } else {
        text = new String(pc, pcStart, pcEnd - pcStart);
      }
    }
    return text;
  }
  
  public char[] getTextCharacters(int[] holderForStartAndLength)
  {
    if (eventType == 4)
    {
      if (usePC)
      {
        holderForStartAndLength[0] = pcStart;
        holderForStartAndLength[1] = (pcEnd - pcStart);
        return pc;
      }
      holderForStartAndLength[0] = posStart;
      holderForStartAndLength[1] = (posEnd - posStart);
      return buf;
    }
    if ((eventType == 2) || (eventType == 3) || (eventType == 5) || (eventType == 9) || (eventType == 6) || (eventType == 8) || (eventType == 7) || (eventType == 10))
    {
      holderForStartAndLength[0] = posStart;
      holderForStartAndLength[1] = (posEnd - posStart);
      return buf;
    }
    if ((eventType == 0) || (eventType == 1))
    {
      holderForStartAndLength[0] = (holderForStartAndLength[1] = -1);
      return null;
    }
    throw new IllegalArgumentException("unknown text eventType: " + eventType);
  }
  
  public String getNamespace()
  {
    if (eventType == 2) {
      return processNamespaces ? elUri[depth] : "";
    }
    if (eventType == 3) {
      return processNamespaces ? elUri[depth] : "";
    }
    return null;
  }
  
  public String getName()
  {
    if (eventType == 2) {
      return elName[depth];
    }
    if (eventType == 3) {
      return elName[depth];
    }
    if (eventType == 6)
    {
      if (entityRefName == null) {
        entityRefName = newString(buf, posStart, posEnd - posStart);
      }
      return entityRefName;
    }
    return null;
  }
  
  public String getPrefix()
  {
    if (eventType == 2) {
      return elPrefix[depth];
    }
    if (eventType == 3) {
      return elPrefix[depth];
    }
    return null;
  }
  
  public boolean isEmptyElementTag()
    throws XmlPullParserException
  {
    if (eventType != 2) {
      throw new XmlPullParserException("parser must be on START_TAG to check for empty element", this, null);
    }
    return emptyElementTag;
  }
  
  public int getAttributeCount()
  {
    if (eventType != 2) {
      return -1;
    }
    return attributeCount;
  }
  
  public String getAttributeNamespace(int index)
  {
    if (eventType != 2) {
      throw new IndexOutOfBoundsException("only START_TAG can have attributes");
    }
    if (!processNamespaces) {
      return "";
    }
    if ((index < 0) || (index >= attributeCount)) {
      throw new IndexOutOfBoundsException("attribute position must be 0.." + (attributeCount - 1) + " and not " + index);
    }
    return attributeUri[index];
  }
  
  public String getAttributeName(int index)
  {
    if (eventType != 2) {
      throw new IndexOutOfBoundsException("only START_TAG can have attributes");
    }
    if ((index < 0) || (index >= attributeCount)) {
      throw new IndexOutOfBoundsException("attribute position must be 0.." + (attributeCount - 1) + " and not " + index);
    }
    return attributeName[index];
  }
  
  public String getAttributePrefix(int index)
  {
    if (eventType != 2) {
      throw new IndexOutOfBoundsException("only START_TAG can have attributes");
    }
    if (!processNamespaces) {
      return null;
    }
    if ((index < 0) || (index >= attributeCount)) {
      throw new IndexOutOfBoundsException("attribute position must be 0.." + (attributeCount - 1) + " and not " + index);
    }
    return attributePrefix[index];
  }
  
  public String getAttributeType(int index)
  {
    if (eventType != 2) {
      throw new IndexOutOfBoundsException("only START_TAG can have attributes");
    }
    if ((index < 0) || (index >= attributeCount)) {
      throw new IndexOutOfBoundsException("attribute position must be 0.." + (attributeCount - 1) + " and not " + index);
    }
    return "CDATA";
  }
  
  public boolean isAttributeDefault(int index)
  {
    if (eventType != 2) {
      throw new IndexOutOfBoundsException("only START_TAG can have attributes");
    }
    if ((index < 0) || (index >= attributeCount)) {
      throw new IndexOutOfBoundsException("attribute position must be 0.." + (attributeCount - 1) + " and not " + index);
    }
    return false;
  }
  
  public String getAttributeValue(int index)
  {
    if (eventType != 2) {
      throw new IndexOutOfBoundsException("only START_TAG can have attributes");
    }
    if ((index < 0) || (index >= attributeCount)) {
      throw new IndexOutOfBoundsException("attribute position must be 0.." + (attributeCount - 1) + " and not " + index);
    }
    return attributeValue[index];
  }
  
  public String getAttributeValue(String namespace, String name)
  {
    if (eventType != 2) {
      throw new IndexOutOfBoundsException("only START_TAG can have attributes" + getPositionDescription());
    }
    if (name == null) {
      throw new IllegalArgumentException("attribute name can not be null");
    }
    if (processNamespaces)
    {
      if (namespace == null) {
        namespace = "";
      }
      for (int i = 0; i < attributeCount; i++) {
        if (((namespace == attributeUri[i]) || (namespace.equals(attributeUri[i]))) && (name.equals(attributeName[i]))) {
          return attributeValue[i];
        }
      }
    }
    else
    {
      if ((namespace != null) && (namespace.length() == 0)) {
        namespace = null;
      }
      if (namespace != null) {
        throw new IllegalArgumentException("when namespaces processing is disabled attribute namespace must be null");
      }
      for (int i = 0; i < attributeCount; i++) {
        if (name.equals(attributeName[i])) {
          return attributeValue[i];
        }
      }
    }
    return null;
  }
  
  public int getEventType()
    throws XmlPullParserException
  {
    return eventType;
  }
  
  public void require(int type, String namespace, String name)
    throws XmlPullParserException, IOException
  {
    if ((!processNamespaces) && (namespace != null)) {
      throw new XmlPullParserException("processing namespaces must be enabled on parser (or factory) to have possible namespaces declared on elements" + " (position:" + getPositionDescription() + ")");
    }
    if ((type != getEventType()) || ((namespace != null) && (!namespace.equals(getNamespace()))) || ((name != null) && (!name.equals(getName())))) {
      throw new XmlPullParserException("expected event " + TYPES[type] + (name != null ? " with name '" + name + "'" : "") + ((namespace != null) && (name != null) ? " and" : "") + (namespace != null ? " with namespace '" + namespace + "'" : "") + " but got" + (type != getEventType() ? " " + TYPES[getEventType()] : "") + ((name != null) && (getName() != null) && (!name.equals(getName())) ? " name '" + getName() + "'" : "") + ((namespace != null) && (name != null) && (getName() != null) && (!name.equals(getName())) && (getNamespace() != null) && (!namespace.equals(getNamespace())) ? " and" : "") + ((namespace != null) && (getNamespace() != null) && (!namespace.equals(getNamespace())) ? " namespace '" + getNamespace() + "'" : "") + " (position:" + getPositionDescription() + ")");
    }
  }
  
  public void skipSubTree()
    throws XmlPullParserException, IOException
  {
    require(2, null, null);
    int level = 1;
    while (level > 0)
    {
      int eventType = next();
      if (eventType == 3) {
        level--;
      } else if (eventType == 2) {
        level++;
      }
    }
  }
  
  public String nextText()
    throws XmlPullParserException, IOException
  {
    if (getEventType() != 2) {
      throw new XmlPullParserException("parser must be on START_TAG to read next text", this, null);
    }
    int eventType = next();
    if (eventType == 4)
    {
      String result = getText();
      eventType = next();
      if (eventType != 3) {
        throw new XmlPullParserException("TEXT must be immediately followed by END_TAG and not " + TYPES[getEventType()], this, null);
      }
      return result;
    }
    if (eventType == 3) {
      return "";
    }
    throw new XmlPullParserException("parser must be on START_TAG or TEXT to read text", this, null);
  }
  
  public int nextTag()
    throws XmlPullParserException, IOException
  {
    next();
    if ((eventType == 4) && (isWhitespace())) {
      next();
    }
    if ((eventType != 2) && (eventType != 3)) {
      throw new XmlPullParserException("expected START_TAG or END_TAG not " + TYPES[getEventType()], this, null);
    }
    return eventType;
  }
  
  public int next()
    throws XmlPullParserException, IOException
  {
    tokenize = false;
    return nextImpl();
  }
  
  public int nextToken()
    throws XmlPullParserException, IOException
  {
    tokenize = true;
    return nextImpl();
  }
  
  protected int nextImpl()
    throws XmlPullParserException, IOException
  {
    text = null;
    pcEnd = (pcStart = 0);
    usePC = false;
    bufStart = posEnd;
    if (pastEndTag)
    {
      pastEndTag = false;
      depth -= 1;
      namespaceEnd = elNamespaceCount[depth];
    }
    if (emptyElementTag)
    {
      emptyElementTag = false;
      pastEndTag = true;
      return eventType = 3;
    }
    if (depth > 0)
    {
      if (seenStartTag)
      {
        seenStartTag = false;
        return eventType = parseStartTag();
      }
      if (seenEndTag)
      {
        seenEndTag = false;
        return eventType = parseEndTag();
      }
      char ch;
      char ch;
      if (seenMarkup)
      {
        seenMarkup = false;
        ch = '<';
      }
      else
      {
        char ch;
        if (seenAmpersand)
        {
          seenAmpersand = false;
          ch = '&';
        }
        else
        {
          ch = more();
        }
      }
      posStart = (pos - 1);
      
      boolean hadCharData = false;
      
      boolean needsMerging = false;
      for (;;)
      {
        if (ch == '<')
        {
          if (hadCharData) {
            if (tokenize)
            {
              seenMarkup = true;
              return eventType = 4;
            }
          }
          ch = more();
          if (ch == '/')
          {
            if ((!tokenize) && (hadCharData))
            {
              seenEndTag = true;
              
              return eventType = 4;
            }
            return eventType = parseEndTag();
          }
          if (ch == '!')
          {
            ch = more();
            if (ch == '-')
            {
              parseComment();
              if (tokenize) {
                return eventType = 9;
              }
              if ((!usePC) && (hadCharData)) {
                needsMerging = true;
              } else {
                posStart = pos;
              }
            }
            else if (ch == '[')
            {
              parseCDSect(hadCharData);
              if (tokenize) {
                return eventType = 5;
              }
              int cdStart = posStart;
              int cdEnd = posEnd;
              int cdLen = cdEnd - cdStart;
              if (cdLen > 0)
              {
                hadCharData = true;
                if (!usePC) {
                  needsMerging = true;
                }
              }
            }
            else
            {
              throw new XmlPullParserException("unexpected character in markup " + printable(ch), this, null);
            }
          }
          else if (ch == '?')
          {
            parsePI();
            if (tokenize) {
              return eventType = 8;
            }
            if ((!usePC) && (hadCharData)) {
              needsMerging = true;
            } else {
              posStart = pos;
            }
          }
          else
          {
            if (isNameStartChar(ch))
            {
              if ((!tokenize) && (hadCharData))
              {
                seenStartTag = true;
                
                return eventType = 4;
              }
              return eventType = parseStartTag();
            }
            throw new XmlPullParserException("unexpected character in markup " + printable(ch), this, null);
          }
        }
        else if (ch == '&')
        {
          if ((tokenize) && (hadCharData))
          {
            seenAmpersand = true;
            return eventType = 4;
          }
          int oldStart = posStart + bufAbsoluteStart;
          int oldEnd = posEnd + bufAbsoluteStart;
          char[] resolvedEntity = parseEntityRef();
          if (tokenize) {
            return eventType = 6;
          }
          if (resolvedEntity == null)
          {
            if (entityRefName == null) {
              entityRefName = newString(buf, posStart, posEnd - posStart);
            }
            throw new XmlPullParserException("could not resolve entity named '" + printable(entityRefName) + "'", this, null);
          }
          posStart = (oldStart - bufAbsoluteStart);
          posEnd = (oldEnd - bufAbsoluteStart);
          if (!usePC) {
            if (hadCharData)
            {
              joinPC();
              needsMerging = false;
            }
            else
            {
              usePC = true;
              pcStart = (pcEnd = 0);
            }
          }
          for (char aResolvedEntity : resolvedEntity)
          {
            if (pcEnd >= pc.length) {
              ensurePC(pcEnd);
            }
            pc[(pcEnd++)] = aResolvedEntity;
          }
          hadCharData = true;
        }
        else
        {
          if (needsMerging)
          {
            joinPC();
            
            needsMerging = false;
          }
          hadCharData = true;
          
          boolean normalizedCR = false;
          boolean normalizeInput = (!tokenize) || (!roundtripSupported);
          
          boolean seenBracket = false;
          boolean seenBracketBracket = false;
          do
          {
            if (ch == ']')
            {
              if (seenBracket) {
                seenBracketBracket = true;
              } else {
                seenBracket = true;
              }
            }
            else
            {
              if ((seenBracketBracket) && (ch == '>')) {
                throw new XmlPullParserException("characters ]]> are not allowed in content", this, null);
              }
              if (seenBracket) {
                seenBracketBracket = seenBracket = 0;
              }
            }
            if (normalizeInput) {
              if (ch == '\r')
              {
                normalizedCR = true;
                posEnd = (pos - 1);
                if (!usePC) {
                  if (posEnd > posStart)
                  {
                    joinPC();
                  }
                  else
                  {
                    usePC = true;
                    pcStart = (pcEnd = 0);
                  }
                }
                if (pcEnd >= pc.length) {
                  ensurePC(pcEnd);
                }
                pc[(pcEnd++)] = '\n';
              }
              else if (ch == '\n')
              {
                if ((!normalizedCR) && (usePC))
                {
                  if (pcEnd >= pc.length) {
                    ensurePC(pcEnd);
                  }
                  pc[(pcEnd++)] = '\n';
                }
                normalizedCR = false;
              }
              else
              {
                if (usePC)
                {
                  if (pcEnd >= pc.length) {
                    ensurePC(pcEnd);
                  }
                  pc[(pcEnd++)] = ch;
                }
                normalizedCR = false;
              }
            }
            ch = more();
          } while ((ch != '<') && (ch != '&'));
          posEnd = (pos - 1);
          continue;
        }
        ch = more();
      }
    }
    if (seenRoot) {
      return parseEpilog();
    }
    return parseProlog();
  }
  
  protected int parseProlog()
    throws XmlPullParserException, IOException
  {
    char ch;
    char ch;
    if (seenMarkup) {
      ch = buf[(pos - 1)];
    } else {
      ch = more();
    }
    if (eventType == 0)
    {
      if (ch == 65534) {
        throw new XmlPullParserException("first character in input was UNICODE noncharacter (0xFFFE)- input requires int swapping", this, null);
      }
      if (ch == 65279) {
        ch = more();
      }
    }
    seenMarkup = false;
    boolean gotS = false;
    posStart = (pos - 1);
    boolean normalizeIgnorableWS = (tokenize == true) && (!roundtripSupported);
    boolean normalizedCR = false;
    for (;;)
    {
      if (ch == '<')
      {
        if ((gotS) && (tokenize))
        {
          posEnd = (pos - 1);
          seenMarkup = true;
          return eventType = 7;
        }
        ch = more();
        if (ch == '?')
        {
          boolean isXMLDecl = parsePI();
          if (tokenize)
          {
            if (isXMLDecl) {
              return eventType = 0;
            }
            return eventType = 8;
          }
        }
        else if (ch == '!')
        {
          ch = more();
          if (ch == 'D')
          {
            if (seenDocdecl) {
              throw new XmlPullParserException("only one docdecl allowed in XML document", this, null);
            }
            seenDocdecl = true;
            parseDocdecl();
            if (tokenize) {
              return eventType = 10;
            }
          }
          else if (ch == '-')
          {
            parseComment();
            if (tokenize) {
              return eventType = 9;
            }
          }
          else
          {
            throw new XmlPullParserException("unexpected markup <!" + printable(ch), this, null);
          }
        }
        else
        {
          if (ch == '/') {
            throw new XmlPullParserException("expected start tag name and not " + printable(ch), this, null);
          }
          if (isNameStartChar(ch))
          {
            seenRoot = true;
            return parseStartTag();
          }
          throw new XmlPullParserException("expected start tag name and not " + printable(ch), this, null);
        }
      }
      else if (isS(ch))
      {
        gotS = true;
        if (normalizeIgnorableWS) {
          if (ch == '\r')
          {
            normalizedCR = true;
            if (!usePC)
            {
              posEnd = (pos - 1);
              if (posEnd > posStart)
              {
                joinPC();
              }
              else
              {
                usePC = true;
                pcStart = (pcEnd = 0);
              }
            }
            if (pcEnd >= pc.length) {
              ensurePC(pcEnd);
            }
            pc[(pcEnd++)] = '\n';
          }
          else if (ch == '\n')
          {
            if ((!normalizedCR) && (usePC))
            {
              if (pcEnd >= pc.length) {
                ensurePC(pcEnd);
              }
              pc[(pcEnd++)] = '\n';
            }
            normalizedCR = false;
          }
          else
          {
            if (usePC)
            {
              if (pcEnd >= pc.length) {
                ensurePC(pcEnd);
              }
              pc[(pcEnd++)] = ch;
            }
            normalizedCR = false;
          }
        }
      }
      else
      {
        throw new XmlPullParserException("only whitespace content allowed before start tag and not " + printable(ch), this, null);
      }
      ch = more();
    }
  }
  
  protected int parseEpilog()
    throws XmlPullParserException, IOException
  {
    if (eventType == 1) {
      throw new XmlPullParserException("already reached end of XML input", this, null);
    }
    if (reachedEnd) {
      return eventType = 1;
    }
    boolean gotS = false;
    boolean normalizeIgnorableWS = (tokenize == true) && (!roundtripSupported);
    boolean normalizedCR = false;
    try
    {
      char ch;
      char ch;
      if (seenMarkup) {
        ch = buf[(pos - 1)];
      } else {
        ch = more();
      }
      seenMarkup = false;
      posStart = (pos - 1);
      if (!reachedEnd) {
        for (;;)
        {
          if (ch == '<')
          {
            if ((gotS) && (tokenize))
            {
              posEnd = (pos - 1);
              seenMarkup = true;
              return eventType = 7;
            }
            ch = more();
            if (reachedEnd) {
              break;
            }
            if (ch == '?')
            {
              parsePI();
              if (tokenize) {
                return eventType = 8;
              }
            }
            else if (ch == '!')
            {
              ch = more();
              if (reachedEnd) {
                break;
              }
              if (ch == 'D')
              {
                parseDocdecl();
                if (tokenize) {
                  return eventType = 10;
                }
              }
              else if (ch == '-')
              {
                parseComment();
                if (tokenize) {
                  return eventType = 9;
                }
              }
              else
              {
                throw new XmlPullParserException("unexpected markup <!" + printable(ch), this, null);
              }
            }
            else
            {
              if (ch == '/') {
                throw new XmlPullParserException("end tag not allowed in epilog but got " + printable(ch), this, null);
              }
              if (isNameStartChar(ch)) {
                throw new XmlPullParserException("start tag not allowed in epilog but got " + printable(ch), this, null);
              }
              throw new XmlPullParserException("in epilog expected ignorable content and not " + printable(ch), this, null);
            }
          }
          else if (isS(ch))
          {
            gotS = true;
            if (normalizeIgnorableWS) {
              if (ch == '\r')
              {
                normalizedCR = true;
                if (!usePC)
                {
                  posEnd = (pos - 1);
                  if (posEnd > posStart)
                  {
                    joinPC();
                  }
                  else
                  {
                    usePC = true;
                    pcStart = (pcEnd = 0);
                  }
                }
                if (pcEnd >= pc.length) {
                  ensurePC(pcEnd);
                }
                pc[(pcEnd++)] = '\n';
              }
              else if (ch == '\n')
              {
                if ((!normalizedCR) && (usePC))
                {
                  if (pcEnd >= pc.length) {
                    ensurePC(pcEnd);
                  }
                  pc[(pcEnd++)] = '\n';
                }
                normalizedCR = false;
              }
              else
              {
                if (usePC)
                {
                  if (pcEnd >= pc.length) {
                    ensurePC(pcEnd);
                  }
                  pc[(pcEnd++)] = ch;
                }
                normalizedCR = false;
              }
            }
          }
          else
          {
            throw new XmlPullParserException("in epilog non whitespace content is not allowed but got " + printable(ch), this, null);
          }
          ch = more();
          if (reachedEnd) {
            break;
          }
        }
      }
    }
    catch (EOFException ex)
    {
      reachedEnd = true;
    }
    if (reachedEnd)
    {
      if ((tokenize) && (gotS))
      {
        posEnd = pos;
        return eventType = 7;
      }
      return eventType = 1;
    }
    throw new XmlPullParserException("internal error in parseEpilog");
  }
  
  public int parseEndTag()
    throws XmlPullParserException, IOException
  {
    char ch = more();
    if (!isNameStartChar(ch)) {
      throw new XmlPullParserException("expected name start and not " + printable(ch), this, null);
    }
    posStart = (pos - 3);
    int nameStart = pos - 1 + bufAbsoluteStart;
    do
    {
      ch = more();
    } while (isNameChar(ch));
    int off = nameStart - bufAbsoluteStart;
    
    int len = pos - 1 - off;
    char[] cbuf = elRawName[depth];
    if (elRawNameEnd[depth] != len)
    {
      String startname = new String(cbuf, 0, elRawNameEnd[depth]);
      String endname = new String(buf, off, len);
      throw new XmlPullParserException("end tag name </" + endname + "> must match start tag name <" + startname + ">" + " from line " + elRawNameLine[depth], this, null);
    }
    for (int i = 0; i < len; i++) {
      if (buf[(off++)] != cbuf[i])
      {
        String startname = new String(cbuf, 0, len);
        String endname = new String(buf, off - i - 1, len);
        throw new XmlPullParserException("end tag name </" + endname + "> must be the same as start tag <" + startname + ">" + " from line " + elRawNameLine[depth], this, null);
      }
    }
    while (isS(ch)) {
      ch = more();
    }
    if (ch != '>') {
      throw new XmlPullParserException("expected > to finsh end tag not " + printable(ch) + " from line " + elRawNameLine[depth], this, null);
    }
    posEnd = pos;
    pastEndTag = true;
    return eventType = 3;
  }
  
  public int parseStartTag()
    throws XmlPullParserException, IOException
  {
    depth += 1;
    
    posStart = (pos - 2);
    
    emptyElementTag = false;
    attributeCount = 0;
    
    int nameStart = pos - 1 + bufAbsoluteStart;
    int colonPos = -1;
    char ch = buf[(pos - 1)];
    if ((ch == ':') && (processNamespaces)) {
      throw new XmlPullParserException("when namespaces processing enabled colon can not be at element name start", this, null);
    }
    for (;;)
    {
      ch = more();
      if (!isNameChar(ch)) {
        break;
      }
      if ((ch == ':') && (processNamespaces))
      {
        if (colonPos != -1) {
          throw new XmlPullParserException("only one colon is allowed in name of element when namespaces are enabled", this, null);
        }
        colonPos = pos - 1 + bufAbsoluteStart;
      }
    }
    ensureElementsCapacity();
    
    int elLen = pos - 1 - (nameStart - bufAbsoluteStart);
    if ((elRawName[depth] == null) || (elRawName[depth].length < elLen)) {
      elRawName[depth] = new char[2 * elLen];
    }
    System.arraycopy(buf, nameStart - bufAbsoluteStart, elRawName[depth], 0, elLen);
    elRawNameEnd[depth] = elLen;
    elRawNameLine[depth] = lineNumber;
    
    String name = null;
    
    String prefix = null;
    if (processNamespaces)
    {
      if (colonPos != -1)
      {
        prefix = elPrefix[depth] = newString(buf, nameStart - bufAbsoluteStart, colonPos - nameStart);
        name = elName[depth] = newString(buf, colonPos + 1 - bufAbsoluteStart, pos - 2 - (colonPos - bufAbsoluteStart));
      }
      else
      {
        prefix = elPrefix[depth] = null;
        name = elName[depth] = newString(buf, nameStart - bufAbsoluteStart, elLen);
      }
    }
    else {
      name = elName[depth] = newString(buf, nameStart - bufAbsoluteStart, elLen);
    }
    for (;;)
    {
      if (isS(ch))
      {
        ch = more();
      }
      else
      {
        if (ch == '>') {
          break label568;
        }
        if (ch == '/')
        {
          if (emptyElementTag) {
            throw new XmlPullParserException("repeated / in tag declaration", this, null);
          }
          emptyElementTag = true;
          ch = more();
          if (ch == '>') {
            break label568;
          }
          throw new XmlPullParserException("expected > to end empty tag not " + printable(ch), this, null);
        }
        if (!isNameStartChar(ch)) {
          break;
        }
        ch = parseAttribute();
        ch = more();
      }
    }
    throw new XmlPullParserException("start tag unexpected character " + printable(ch), this, null);
    label568:
    if (processNamespaces)
    {
      String uri = getNamespace(prefix);
      if (uri == null) {
        if (prefix == null) {
          uri = "";
        } else {
          throw new XmlPullParserException("could not determine namespace bound to element prefix " + prefix, this, null);
        }
      }
      elUri[depth] = uri;
      for (int i = 0; i < attributeCount; i++)
      {
        String attrPrefix = attributePrefix[i];
        if (attrPrefix != null)
        {
          String attrUri = getNamespace(attrPrefix);
          if (attrUri == null) {
            throw new XmlPullParserException("could not determine namespace bound to attribute prefix " + attrPrefix, this, null);
          }
          attributeUri[i] = attrUri;
        }
        else
        {
          attributeUri[i] = "";
        }
      }
      for (int i = 1; i < attributeCount; i++) {
        for (int j = 0; j < i; j++) {
          if ((attributeUri[j] == attributeUri[i]) && (((allStringsInterned) && (attributeName[j].equals(attributeName[i]))) || ((!allStringsInterned) && (attributeNameHash[j] == attributeNameHash[i]) && (attributeName[j].equals(attributeName[i])))))
          {
            String attr1 = attributeName[j];
            if (attributeUri[j] != null) {
              attr1 = attributeUri[j] + ":" + attr1;
            }
            String attr2 = attributeName[i];
            if (attributeUri[i] != null) {
              attr2 = attributeUri[i] + ":" + attr2;
            }
            throw new XmlPullParserException("duplicated attributes " + attr1 + " and " + attr2, this, null);
          }
        }
      }
    }
    else
    {
      for (int i = 1; i < attributeCount; i++) {
        for (int j = 0; j < i; j++) {
          if (((allStringsInterned) && (attributeName[j].equals(attributeName[i]))) || ((!allStringsInterned) && (attributeNameHash[j] == attributeNameHash[i]) && (attributeName[j].equals(attributeName[i]))))
          {
            String attr1 = attributeName[j];
            String attr2 = attributeName[i];
            throw new XmlPullParserException("duplicated attributes " + attr1 + " and " + attr2, this, null);
          }
        }
      }
    }
    elNamespaceCount[depth] = namespaceEnd;
    posEnd = pos;
    return eventType = 2;
  }
  
  protected char parseAttribute()
    throws XmlPullParserException, IOException
  {
    int prevPosStart = posStart + bufAbsoluteStart;
    int nameStart = pos - 1 + bufAbsoluteStart;
    int colonPos = -1;
    char ch = buf[(pos - 1)];
    if ((ch == ':') && (processNamespaces)) {
      throw new XmlPullParserException("when namespaces processing enabled colon can not be at attribute name start", this, null);
    }
    boolean startsWithXmlns = (processNamespaces) && (ch == 'x');
    int xmlnsPos = 0;
    
    ch = more();
    while (isNameChar(ch))
    {
      if (processNamespaces)
      {
        if ((startsWithXmlns) && (xmlnsPos < 5))
        {
          xmlnsPos++;
          if (xmlnsPos == 1)
          {
            if (ch != 'm') {
              startsWithXmlns = false;
            }
          }
          else if (xmlnsPos == 2)
          {
            if (ch != 'l') {
              startsWithXmlns = false;
            }
          }
          else if (xmlnsPos == 3)
          {
            if (ch != 'n') {
              startsWithXmlns = false;
            }
          }
          else if (xmlnsPos == 4)
          {
            if (ch != 's') {
              startsWithXmlns = false;
            }
          }
          else if (xmlnsPos == 5) {
            if (ch != ':') {
              throw new XmlPullParserException("after xmlns in attribute name must be colonwhen namespaces are enabled", this, null);
            }
          }
        }
        if (ch == ':')
        {
          if (colonPos != -1) {
            throw new XmlPullParserException("only one colon is allowed in attribute name when namespaces are enabled", this, null);
          }
          colonPos = pos - 1 + bufAbsoluteStart;
        }
      }
      ch = more();
    }
    ensureAttributesCapacity(attributeCount);
    
    String name = null;
    String prefix = null;
    if (processNamespaces)
    {
      if (xmlnsPos < 4) {
        startsWithXmlns = false;
      }
      if (startsWithXmlns)
      {
        if (colonPos != -1)
        {
          int nameLen = pos - 2 - (colonPos - bufAbsoluteStart);
          if (nameLen == 0) {
            throw new XmlPullParserException("namespace prefix is required after xmlns:  when namespaces are enabled", this, null);
          }
          name = newString(buf, colonPos - bufAbsoluteStart + 1, nameLen);
        }
      }
      else
      {
        if (colonPos != -1)
        {
          int prefixLen = colonPos - nameStart;
          prefix = attributePrefix[attributeCount] = newString(buf, nameStart - bufAbsoluteStart, prefixLen);
          
          int nameLen = pos - 2 - (colonPos - bufAbsoluteStart);
          name = attributeName[attributeCount] = newString(buf, colonPos - bufAbsoluteStart + 1, nameLen);
        }
        else
        {
          prefix = attributePrefix[attributeCount] = null;
          name = attributeName[attributeCount] = newString(buf, nameStart - bufAbsoluteStart, pos - 1 - (nameStart - bufAbsoluteStart));
        }
        if (!allStringsInterned) {
          attributeNameHash[attributeCount] = name.hashCode();
        }
      }
    }
    else
    {
      name = attributeName[attributeCount] = newString(buf, nameStart - bufAbsoluteStart, pos - 1 - (nameStart - bufAbsoluteStart));
      if (!allStringsInterned) {
        attributeNameHash[attributeCount] = name.hashCode();
      }
    }
    while (isS(ch)) {
      ch = more();
    }
    if (ch != '=') {
      throw new XmlPullParserException("expected = after attribute name", this, null);
    }
    ch = more();
    while (isS(ch)) {
      ch = more();
    }
    char delimit = ch;
    if ((delimit != '"') && (delimit != '\'')) {
      throw new XmlPullParserException("attribute value must start with quotation or apostrophe not " + printable(delimit), this, null);
    }
    boolean normalizedCR = false;
    usePC = false;
    pcStart = pcEnd;
    posStart = pos;
    for (;;)
    {
      ch = more();
      if (ch == delimit) {
        break;
      }
      if (ch == '<') {
        throw new XmlPullParserException("markup not allowed inside attribute value - illegal < ", this, null);
      }
      if (ch == '&')
      {
        posEnd = (pos - 1);
        if (!usePC)
        {
          boolean hadCharData = posEnd > posStart;
          if (hadCharData)
          {
            joinPC();
          }
          else
          {
            usePC = true;
            pcStart = (pcEnd = 0);
          }
        }
        char[] resolvedEntity = parseEntityRef();
        if (resolvedEntity == null)
        {
          if (entityRefName == null) {
            entityRefName = newString(buf, posStart, posEnd - posStart);
          }
          throw new XmlPullParserException("could not resolve entity named '" + printable(entityRefName) + "'", this, null);
        }
        for (char aResolvedEntity : resolvedEntity)
        {
          if (pcEnd >= pc.length) {
            ensurePC(pcEnd);
          }
          pc[(pcEnd++)] = aResolvedEntity;
        }
      }
      else if ((ch == '\t') || (ch == '\n') || (ch == '\r'))
      {
        if (!usePC)
        {
          posEnd = (pos - 1);
          if (posEnd > posStart)
          {
            joinPC();
          }
          else
          {
            usePC = true;
            pcEnd = (pcStart = 0);
          }
        }
        if (pcEnd >= pc.length) {
          ensurePC(pcEnd);
        }
        if ((ch != '\n') || (!normalizedCR)) {
          pc[(pcEnd++)] = ' ';
        }
      }
      else if (usePC)
      {
        if (pcEnd >= pc.length) {
          ensurePC(pcEnd);
        }
        pc[(pcEnd++)] = ch;
      }
      normalizedCR = ch == '\r';
    }
    if ((processNamespaces) && (startsWithXmlns))
    {
      String ns = null;
      if (!usePC) {
        ns = newStringIntern(buf, posStart, pos - 1 - posStart);
      } else {
        ns = newStringIntern(pc, pcStart, pcEnd - pcStart);
      }
      ensureNamespacesCapacity(namespaceEnd);
      int prefixHash = -1;
      if (colonPos != -1)
      {
        if (ns.length() == 0) {
          throw new XmlPullParserException("non-default namespace can not be declared to be empty string", this, null);
        }
        namespacePrefix[namespaceEnd] = name;
        if (!allStringsInterned) {
          prefixHash = namespacePrefixHash[namespaceEnd] = name.hashCode();
        }
      }
      else
      {
        namespacePrefix[namespaceEnd] = null;
        if (!allStringsInterned) {
          prefixHash = namespacePrefixHash[namespaceEnd] = -1;
        }
      }
      namespaceUri[namespaceEnd] = ns;
      
      int startNs = elNamespaceCount[(depth - 1)];
      for (int i = namespaceEnd - 1; i >= startNs; i--) {
        if (((!allStringsInterned) && (name != null)) || ((namespacePrefix[i] == name) || ((!allStringsInterned) && (name != null) && (namespacePrefixHash[i] == prefixHash) && (name.equals(namespacePrefix[i])))))
        {
          String s = "'" + name + "'";
          throw new XmlPullParserException("duplicated namespace declaration for " + s + " prefix", this, null);
        }
      }
      namespaceEnd += 1;
    }
    else
    {
      if (!usePC) {
        attributeValue[attributeCount] = new String(buf, posStart, pos - 1 - posStart);
      } else {
        attributeValue[attributeCount] = new String(pc, pcStart, pcEnd - pcStart);
      }
      attributeCount += 1;
    }
    posStart = (prevPosStart - bufAbsoluteStart);
    return ch;
  }
  
  protected char[] charRefOneCharBuf = new char[1];
  
  protected char[] parseEntityRef()
    throws XmlPullParserException, IOException
  {
    entityRefName = null;
    posStart = pos;
    char ch = more();
    if (ch == '#')
    {
      char charRef = '\000';
      ch = more();
      StringBuilder sb = new StringBuilder();
      boolean isHex = ch == 'x';
      if (isHex)
      {
        for (;;)
        {
          ch = more();
          if ((ch >= '0') && (ch <= '9'))
          {
            charRef = (char)(charRef * '\020' + (ch - '0'));
            sb.append(ch);
          }
          else if ((ch >= 'a') && (ch <= 'f'))
          {
            charRef = (char)(charRef * '\020' + (ch - 'W'));
            sb.append(ch);
          }
          else
          {
            if ((ch < 'A') || (ch > 'F')) {
              break;
            }
            charRef = (char)(charRef * '\020' + (ch - '7'));
            sb.append(ch);
          }
        }
        if (ch != ';') {
          throw new XmlPullParserException("character reference (with hex value) may not contain " + printable(ch), this, null);
        }
      }
      else
      {
        for (;;)
        {
          if ((ch >= '0') && (ch <= '9'))
          {
            charRef = (char)(charRef * '\n' + (ch - '0'));
            sb.append(ch);
          }
          else
          {
            if (ch == ';') {
              break;
            }
            throw new XmlPullParserException("character reference (with decimal value) may not contain " + printable(ch), this, null);
          }
          ch = more();
        }
      }
      posEnd = (pos - 1);
      try
      {
        charRefOneCharBuf = toChars(Integer.parseInt(sb.toString(), isHex ? 16 : 10));
      }
      catch (IllegalArgumentException e)
      {
        throw new XmlPullParserException("character reference (with " + (isHex ? "hex" : "decimal") + " value " + sb.toString() + ") is invalid", this, null);
      }
      if (tokenize) {
        text = newString(charRefOneCharBuf, 0, charRefOneCharBuf.length);
      }
      return charRefOneCharBuf;
    }
    if (!isNameStartChar(ch)) {
      throw new XmlPullParserException("entity reference names can not start with character '" + printable(ch) + "'", this, null);
    }
    do
    {
      ch = more();
      if (ch == ';') {
        break;
      }
    } while (isNameChar(ch));
    throw new XmlPullParserException("entity reference name can not contain character " + printable(ch) + "'", this, null);
    
    posEnd = (pos - 1);
    
    int len = posEnd - posStart;
    if ((len == 2) && (buf[posStart] == 'l') && (buf[(posStart + 1)] == 't'))
    {
      if (tokenize) {
        text = "<";
      }
      charRefOneCharBuf[0] = '<';
      return charRefOneCharBuf;
    }
    if ((len == 3) && (buf[posStart] == 'a') && (buf[(posStart + 1)] == 'm') && (buf[(posStart + 2)] == 'p'))
    {
      if (tokenize) {
        text = "&";
      }
      charRefOneCharBuf[0] = '&';
      return charRefOneCharBuf;
    }
    if ((len == 2) && (buf[posStart] == 'g') && (buf[(posStart + 1)] == 't'))
    {
      if (tokenize) {
        text = ">";
      }
      charRefOneCharBuf[0] = '>';
      return charRefOneCharBuf;
    }
    if ((len == 4) && (buf[posStart] == 'a') && (buf[(posStart + 1)] == 'p') && (buf[(posStart + 2)] == 'o') && (buf[(posStart + 3)] == 's'))
    {
      if (tokenize) {
        text = "'";
      }
      charRefOneCharBuf[0] = '\'';
      return charRefOneCharBuf;
    }
    if ((len == 4) && (buf[posStart] == 'q') && (buf[(posStart + 1)] == 'u') && (buf[(posStart + 2)] == 'o') && (buf[(posStart + 3)] == 't'))
    {
      if (tokenize) {
        text = "\"";
      }
      charRefOneCharBuf[0] = '"';
      return charRefOneCharBuf;
    }
    char[] result = lookuEntityReplacement(len);
    if (result != null) {
      return result;
    }
    if (tokenize) {
      text = null;
    }
    return null;
  }
  
  protected char[] lookuEntityReplacement(int entityNameLen)
    throws XmlPullParserException, IOException
  {
    if (!allStringsInterned)
    {
      int hash = fastHash(buf, posStart, posEnd - posStart);
      label130:
      for (int i = entityEnd - 1; i >= 0; i--) {
        if ((hash == entityNameHash[i]) && (entityNameLen == entityNameBuf[i].length))
        {
          char[] entityBuf = entityNameBuf[i];
          for (int j = 0; j < entityNameLen; j++) {
            if (buf[(posStart + j)] != entityBuf[j]) {
              break label130;
            }
          }
          if (tokenize) {
            text = entityReplacement[i];
          }
          return entityReplacementBuf[i];
        }
      }
    }
    else
    {
      entityRefName = newString(buf, posStart, posEnd - posStart);
      for (int i = entityEnd - 1; i >= 0; i--) {
        if (entityRefName == entityName[i])
        {
          if (tokenize) {
            text = entityReplacement[i];
          }
          return entityReplacementBuf[i];
        }
      }
    }
    return null;
  }
  
  protected void parseComment()
    throws XmlPullParserException, IOException
  {
    char ch = more();
    if (ch != '-') {
      throw new XmlPullParserException("expected <!-- for comment start", this, null);
    }
    if (tokenize) {
      posStart = pos;
    }
    int curLine = lineNumber;
    int curColumn = columnNumber;
    try
    {
      boolean normalizeIgnorableWS = (tokenize == true) && (!roundtripSupported);
      boolean normalizedCR = false;
      
      boolean seenDash = false;
      boolean seenDashDash = false;
      for (;;)
      {
        ch = more();
        if ((seenDashDash) && (ch != '>')) {
          throw new XmlPullParserException("in comment after two dashes (--) next character must be > not " + printable(ch), this, null);
        }
        if (ch == '-')
        {
          if (!seenDash)
          {
            seenDash = true;
          }
          else
          {
            seenDashDash = true;
            seenDash = false;
          }
        }
        else if (ch == '>')
        {
          if (seenDashDash) {
            break;
          }
          seenDashDash = false;
          
          seenDash = false;
        }
        else
        {
          seenDash = false;
        }
        if (normalizeIgnorableWS) {
          if (ch == '\r')
          {
            normalizedCR = true;
            if (!usePC)
            {
              posEnd = (pos - 1);
              if (posEnd > posStart)
              {
                joinPC();
              }
              else
              {
                usePC = true;
                pcStart = (pcEnd = 0);
              }
            }
            if (pcEnd >= pc.length) {
              ensurePC(pcEnd);
            }
            pc[(pcEnd++)] = '\n';
          }
          else if (ch == '\n')
          {
            if ((!normalizedCR) && (usePC))
            {
              if (pcEnd >= pc.length) {
                ensurePC(pcEnd);
              }
              pc[(pcEnd++)] = '\n';
            }
            normalizedCR = false;
          }
          else
          {
            if (usePC)
            {
              if (pcEnd >= pc.length) {
                ensurePC(pcEnd);
              }
              pc[(pcEnd++)] = ch;
            }
            normalizedCR = false;
          }
        }
      }
    }
    catch (EOFException ex)
    {
      throw new XmlPullParserException("comment started on line " + curLine + " and column " + curColumn + " was not closed", this, ex);
    }
    if (tokenize)
    {
      posEnd = (pos - 3);
      if (usePC) {
        pcEnd -= 2;
      }
    }
  }
  
  protected boolean parsePI()
    throws XmlPullParserException, IOException
  {
    if (tokenize) {
      posStart = pos;
    }
    int curLine = lineNumber;
    int curColumn = columnNumber;
    int piTargetStart = pos;
    int piTargetEnd = -1;
    boolean normalizeIgnorableWS = (tokenize == true) && (!roundtripSupported);
    boolean normalizedCR = false;
    try
    {
      boolean seenPITarget = false;
      boolean seenQ = false;
      char ch = more();
      if (isS(ch)) {
        throw new XmlPullParserException("processing instruction PITarget must be exactly after <? and not white space character", this, null);
      }
      for (;;)
      {
        if (ch == '?')
        {
          if (!seenPITarget) {
            throw new XmlPullParserException("processing instruction PITarget name not found", this, null);
          }
          seenQ = true;
        }
        else if (ch == '>')
        {
          if (seenQ) {
            break;
          }
          if (!seenPITarget) {
            throw new XmlPullParserException("processing instruction PITarget name not found", this, null);
          }
        }
        else
        {
          if ((piTargetEnd == -1) && (isS(ch)))
          {
            piTargetEnd = pos - 1;
            if (piTargetEnd - piTargetStart == 3) {
              if (((buf[piTargetStart] == 'x') || (buf[piTargetStart] == 'X')) && ((buf[(piTargetStart + 1)] == 'm') || (buf[(piTargetStart + 1)] == 'M')) && ((buf[(piTargetStart + 2)] == 'l') || (buf[(piTargetStart + 2)] == 'L')))
              {
                if (piTargetStart > 3) {
                  throw new XmlPullParserException("processing instruction can not have PITarget with reserved xml name", this, null);
                }
                if ((buf[piTargetStart] != 'x') && (buf[(piTargetStart + 1)] != 'm') && (buf[(piTargetStart + 2)] != 'l')) {
                  throw new XmlPullParserException("XMLDecl must have xml name in lowercase", this, null);
                }
                parseXmlDecl(ch);
                if (tokenize) {
                  posEnd = (pos - 2);
                }
                int off = piTargetStart + 3;
                int len = pos - 2 - off;
                xmlDeclContent = newString(buf, off, len);
                return false;
              }
            }
          }
          seenQ = false;
        }
        if (normalizeIgnorableWS) {
          if (ch == '\r')
          {
            normalizedCR = true;
            if (!usePC)
            {
              posEnd = (pos - 1);
              if (posEnd > posStart)
              {
                joinPC();
              }
              else
              {
                usePC = true;
                pcStart = (pcEnd = 0);
              }
            }
            if (pcEnd >= pc.length) {
              ensurePC(pcEnd);
            }
            pc[(pcEnd++)] = '\n';
          }
          else if (ch == '\n')
          {
            if ((!normalizedCR) && (usePC))
            {
              if (pcEnd >= pc.length) {
                ensurePC(pcEnd);
              }
              pc[(pcEnd++)] = '\n';
            }
            normalizedCR = false;
          }
          else
          {
            if (usePC)
            {
              if (pcEnd >= pc.length) {
                ensurePC(pcEnd);
              }
              pc[(pcEnd++)] = ch;
            }
            normalizedCR = false;
          }
        }
        seenPITarget = true;
        ch = more();
      }
    }
    catch (EOFException ex)
    {
      throw new XmlPullParserException("processing instruction started on line " + curLine + " and column " + curColumn + " was not closed", this, ex);
    }
    if (piTargetEnd == -1) {
      piTargetEnd = pos - 2 + bufAbsoluteStart;
    }
    if (tokenize)
    {
      posEnd = (pos - 2);
      if (normalizeIgnorableWS) {
        pcEnd -= 1;
      }
    }
    return true;
  }
  
  protected static final char[] VERSION = "version".toCharArray();
  protected static final char[] NCODING = "ncoding".toCharArray();
  protected static final char[] TANDALONE = "tandalone".toCharArray();
  protected static final char[] YES = "yes".toCharArray();
  protected static final char[] NO = "no".toCharArray();
  protected static final int LOOKUP_MAX = 1024;
  protected static final char LOOKUP_MAX_CHAR = 'Ѐ';
  
  protected void parseXmlDecl(char ch)
    throws XmlPullParserException, IOException
  {
    preventBufferCompaction = true;
    bufStart = 0;
    
    ch = skipS(ch);
    ch = requireInput(ch, VERSION);
    
    ch = skipS(ch);
    if (ch != '=') {
      throw new XmlPullParserException("expected equals sign (=) after version and not " + printable(ch), this, null);
    }
    ch = more();
    ch = skipS(ch);
    if ((ch != '\'') && (ch != '"')) {
      throw new XmlPullParserException("expected apostrophe (') or quotation mark (\") after version and not " + printable(ch), this, null);
    }
    char quotChar = ch;
    
    int versionStart = pos;
    ch = more();
    while (ch != quotChar)
    {
      if (((ch < 'a') || (ch > 'z')) && ((ch < 'A') || (ch > 'Z')) && ((ch < '0') || (ch > '9')) && (ch != '_') && (ch != '.') && (ch != ':') && (ch != '-')) {
        throw new XmlPullParserException("<?xml version value expected to be in ([a-zA-Z0-9_.:] | '-') not " + printable(ch), this, null);
      }
      ch = more();
    }
    int versionEnd = pos - 1;
    parseXmlDeclWithVersion(versionStart, versionEnd);
    preventBufferCompaction = false;
  }
  
  protected void parseXmlDeclWithVersion(int versionStart, int versionEnd)
    throws XmlPullParserException, IOException
  {
    if ((versionEnd - versionStart != 3) || (buf[versionStart] != '1') || (buf[(versionStart + 1)] != '.') || (buf[(versionStart + 2)] != '0')) {
      throw new XmlPullParserException("only 1.0 is supported as <?xml version not '" + printable(new String(buf, versionStart, versionEnd - versionStart)) + "'", this, null);
    }
    xmlDeclVersion = newString(buf, versionStart, versionEnd - versionStart);
    
    char ch = more();
    ch = skipS(ch);
    if (ch == 'e')
    {
      ch = more();
      ch = requireInput(ch, NCODING);
      ch = skipS(ch);
      if (ch != '=') {
        throw new XmlPullParserException("expected equals sign (=) after encoding and not " + printable(ch), this, null);
      }
      ch = more();
      ch = skipS(ch);
      if ((ch != '\'') && (ch != '"')) {
        throw new XmlPullParserException("expected apostrophe (') or quotation mark (\") after encoding and not " + printable(ch), this, null);
      }
      char quotChar = ch;
      int encodingStart = pos;
      ch = more();
      if (((ch < 'a') || (ch > 'z')) && ((ch < 'A') || (ch > 'Z'))) {
        throw new XmlPullParserException("<?xml encoding name expected to start with [A-Za-z] not " + printable(ch), this, null);
      }
      ch = more();
      while (ch != quotChar)
      {
        if (((ch < 'a') || (ch > 'z')) && ((ch < 'A') || (ch > 'Z')) && ((ch < '0') || (ch > '9')) && (ch != '.') && (ch != '_') && (ch != '-')) {
          throw new XmlPullParserException("<?xml encoding value expected to be in ([A-Za-z0-9._] | '-') not " + printable(ch), this, null);
        }
        ch = more();
      }
      int encodingEnd = pos - 1;
      
      inputEncoding = newString(buf, encodingStart, encodingEnd - encodingStart);
      ch = more();
    }
    ch = skipS(ch);
    if (ch == 's')
    {
      ch = more();
      ch = requireInput(ch, TANDALONE);
      ch = skipS(ch);
      if (ch != '=') {
        throw new XmlPullParserException("expected equals sign (=) after standalone and not " + printable(ch), this, null);
      }
      ch = more();
      ch = skipS(ch);
      if ((ch != '\'') && (ch != '"')) {
        throw new XmlPullParserException("expected apostrophe (') or quotation mark (\") after encoding and not " + printable(ch), this, null);
      }
      char quotChar = ch;
      int standaloneStart = pos;
      ch = more();
      if (ch == 'y')
      {
        ch = requireInput(ch, YES);
        
        xmlDeclStandalone = Boolean.valueOf(true);
      }
      else if (ch == 'n')
      {
        ch = requireInput(ch, NO);
        
        xmlDeclStandalone = Boolean.valueOf(false);
      }
      else
      {
        throw new XmlPullParserException("expected 'yes' or 'no' after standalone and not " + printable(ch), this, null);
      }
      if (ch != quotChar) {
        throw new XmlPullParserException("expected " + quotChar + " after standalone value not " + printable(ch), this, null);
      }
      ch = more();
    }
    ch = skipS(ch);
    if (ch != '?') {
      throw new XmlPullParserException("expected ?> as last part of <?xml not " + printable(ch), this, null);
    }
    ch = more();
    if (ch != '>') {
      throw new XmlPullParserException("expected ?> as last part of <?xml not " + printable(ch), this, null);
    }
  }
  
  protected void parseDocdecl()
    throws XmlPullParserException, IOException
  {
    char ch = more();
    if (ch != 'O') {
      throw new XmlPullParserException("expected <!DOCTYPE", this, null);
    }
    ch = more();
    if (ch != 'C') {
      throw new XmlPullParserException("expected <!DOCTYPE", this, null);
    }
    ch = more();
    if (ch != 'T') {
      throw new XmlPullParserException("expected <!DOCTYPE", this, null);
    }
    ch = more();
    if (ch != 'Y') {
      throw new XmlPullParserException("expected <!DOCTYPE", this, null);
    }
    ch = more();
    if (ch != 'P') {
      throw new XmlPullParserException("expected <!DOCTYPE", this, null);
    }
    ch = more();
    if (ch != 'E') {
      throw new XmlPullParserException("expected <!DOCTYPE", this, null);
    }
    posStart = pos;
    
    int bracketLevel = 0;
    boolean normalizeIgnorableWS = (tokenize == true) && (!roundtripSupported);
    boolean normalizedCR = false;
    for (;;)
    {
      ch = more();
      if (ch == '[') {
        bracketLevel++;
      }
      if (ch == ']') {
        bracketLevel--;
      }
      if ((ch == '>') && (bracketLevel == 0)) {
        break;
      }
      if (normalizeIgnorableWS) {
        if (ch == '\r')
        {
          normalizedCR = true;
          if (!usePC)
          {
            posEnd = (pos - 1);
            if (posEnd > posStart)
            {
              joinPC();
            }
            else
            {
              usePC = true;
              pcStart = (pcEnd = 0);
            }
          }
          if (pcEnd >= pc.length) {
            ensurePC(pcEnd);
          }
          pc[(pcEnd++)] = '\n';
        }
        else if (ch == '\n')
        {
          if ((!normalizedCR) && (usePC))
          {
            if (pcEnd >= pc.length) {
              ensurePC(pcEnd);
            }
            pc[(pcEnd++)] = '\n';
          }
          normalizedCR = false;
        }
        else
        {
          if (usePC)
          {
            if (pcEnd >= pc.length) {
              ensurePC(pcEnd);
            }
            pc[(pcEnd++)] = ch;
          }
          normalizedCR = false;
        }
      }
    }
    posEnd = (pos - 1);
  }
  
  protected void parseCDSect(boolean hadCharData)
    throws XmlPullParserException, IOException
  {
    char ch = more();
    if (ch != 'C') {
      throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
    }
    ch = more();
    if (ch != 'D') {
      throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
    }
    ch = more();
    if (ch != 'A') {
      throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
    }
    ch = more();
    if (ch != 'T') {
      throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
    }
    ch = more();
    if (ch != 'A') {
      throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
    }
    ch = more();
    if (ch != '[') {
      throw new XmlPullParserException("expected <![CDATA[ for comment start", this, null);
    }
    int cdStart = pos + bufAbsoluteStart;
    int curLine = lineNumber;
    int curColumn = columnNumber;
    boolean normalizeInput = (!tokenize) || (!roundtripSupported);
    try
    {
      if (normalizeInput) {
        if (hadCharData) {
          if (!usePC) {
            if (posEnd > posStart)
            {
              joinPC();
            }
            else
            {
              usePC = true;
              pcStart = (pcEnd = 0);
            }
          }
        }
      }
      boolean seenBracket = false;
      boolean seenBracketBracket = false;
      boolean normalizedCR = false;
      for (;;)
      {
        ch = more();
        if (ch == ']')
        {
          if (!seenBracket) {
            seenBracket = true;
          } else {
            seenBracketBracket = true;
          }
        }
        else if (ch == '>')
        {
          if ((seenBracket) && (seenBracketBracket)) {
            break;
          }
          seenBracketBracket = false;
          
          seenBracket = false;
        }
        else if (seenBracket)
        {
          seenBracket = false;
        }
        if (normalizeInput) {
          if (ch == '\r')
          {
            normalizedCR = true;
            posStart = (cdStart - bufAbsoluteStart);
            posEnd = (pos - 1);
            if (!usePC) {
              if (posEnd > posStart)
              {
                joinPC();
              }
              else
              {
                usePC = true;
                pcStart = (pcEnd = 0);
              }
            }
            if (pcEnd >= pc.length) {
              ensurePC(pcEnd);
            }
            pc[(pcEnd++)] = '\n';
          }
          else if (ch == '\n')
          {
            if ((!normalizedCR) && (usePC))
            {
              if (pcEnd >= pc.length) {
                ensurePC(pcEnd);
              }
              pc[(pcEnd++)] = '\n';
            }
            normalizedCR = false;
          }
          else
          {
            if (usePC)
            {
              if (pcEnd >= pc.length) {
                ensurePC(pcEnd);
              }
              pc[(pcEnd++)] = ch;
            }
            normalizedCR = false;
          }
        }
      }
    }
    catch (EOFException ex)
    {
      throw new XmlPullParserException("CDATA section started on line " + curLine + " and column " + curColumn + " was not closed", this, ex);
    }
    if (normalizeInput) {
      if (usePC) {
        pcEnd -= 2;
      }
    }
    posStart = (cdStart - bufAbsoluteStart);
    posEnd = (pos - 3);
  }
  
  protected void fillBuf()
    throws IOException, XmlPullParserException
  {
    if (reader == null) {
      throw new XmlPullParserException("reader must be set before parsing is started");
    }
    if (bufEnd > bufSoftLimit)
    {
      boolean compact = bufStart > bufSoftLimit;
      boolean expand = false;
      if (preventBufferCompaction)
      {
        compact = false;
        expand = true;
      }
      else if (!compact)
      {
        if (bufStart < buf.length / 2) {
          expand = true;
        } else {
          compact = true;
        }
      }
      if (compact)
      {
        System.arraycopy(buf, bufStart, buf, 0, bufEnd - bufStart);
      }
      else if (expand)
      {
        int newSize = 2 * buf.length;
        char[] newBuf = new char[newSize];
        
        System.arraycopy(buf, bufStart, newBuf, 0, bufEnd - bufStart);
        buf = newBuf;
        if (bufLoadFactor > 0) {
          bufSoftLimit = (bufLoadFactor * buf.length / 100);
        }
      }
      else
      {
        throw new XmlPullParserException("internal error in fillBuffer()");
      }
      bufEnd -= bufStart;
      pos -= bufStart;
      posStart -= bufStart;
      posEnd -= bufStart;
      bufAbsoluteStart += bufStart;
      bufStart = 0;
    }
    int len = buf.length - bufEnd > 8192 ? 8192 : buf.length - bufEnd;
    int ret = reader.read(buf, bufEnd, len);
    if (ret > 0)
    {
      bufEnd += ret;
      
      return;
    }
    if (ret == -1)
    {
      if ((bufAbsoluteStart == 0) && (pos == 0)) {
        throw new EOFException("input contained no data");
      }
      if ((seenRoot) && (depth == 0))
      {
        reachedEnd = true;
        return;
      }
      StringBuilder expectedTagStack = new StringBuilder();
      if (depth > 0) {
        if ((elRawName == null) || (elRawName[depth] == null))
        {
          String tagName = new String(buf, posStart + 1, pos - posStart - 1);
          expectedTagStack.append(" - expected the opening tag <").append(tagName).append("...>");
        }
        else
        {
          expectedTagStack.append(" - expected end tag");
          if (depth > 1) {
            expectedTagStack.append("s");
          }
          expectedTagStack.append(" ");
          for (int i = depth; i > 0; i--) {
            if ((elRawName == null) || (elRawName[i] == null))
            {
              String tagName = new String(buf, posStart + 1, pos - posStart - 1);
              expectedTagStack.append(" - expected the opening tag <").append(tagName).append("...>");
            }
            else
            {
              String tagName = new String(elRawName[i], 0, elRawNameEnd[i]);
              expectedTagStack.append("</").append(tagName).append('>');
            }
          }
          expectedTagStack.append(" to close");
          for (int i = depth; i > 0; i--)
          {
            if (i != depth) {
              expectedTagStack.append(" and");
            }
            if ((elRawName == null) || (elRawName[i] == null))
            {
              String tagName = new String(buf, posStart + 1, pos - posStart - 1);
              expectedTagStack.append(" start tag <").append(tagName).append(">");
              expectedTagStack.append(" from line ").append(elRawNameLine[i]);
            }
            else
            {
              String tagName = new String(elRawName[i], 0, elRawNameEnd[i]);
              expectedTagStack.append(" start tag <").append(tagName).append(">");
              expectedTagStack.append(" from line ").append(elRawNameLine[i]);
            }
          }
          expectedTagStack.append(", parser stopped on");
        }
      }
      throw new EOFException("no more data available" + expectedTagStack.toString() + getPositionDescription());
    }
    throw new IOException("error reading input, returned " + ret);
  }
  
  protected char more()
    throws IOException, XmlPullParserException
  {
    if (pos >= bufEnd)
    {
      fillBuf();
      if (reachedEnd) {
        return 65535;
      }
    }
    char ch = buf[(pos++)];
    if (ch == '\n')
    {
      lineNumber += 1;
      columnNumber = 1;
    }
    else
    {
      columnNumber += 1;
    }
    return ch;
  }
  
  protected void ensurePC(int end)
  {
    int newSize = end > 8192 ? 2 * end : 16384;
    char[] newPC = new char[newSize];
    
    System.arraycopy(pc, 0, newPC, 0, pcEnd);
    pc = newPC;
  }
  
  protected void joinPC()
  {
    int len = posEnd - posStart;
    int newEnd = pcEnd + len + 1;
    if (newEnd >= pc.length) {
      ensurePC(newEnd);
    }
    System.arraycopy(buf, posStart, pc, pcEnd, len);
    pcEnd += len;
    usePC = true;
  }
  
  protected char requireInput(char ch, char[] input)
    throws XmlPullParserException, IOException
  {
    for (char anInput : input)
    {
      if (ch != anInput) {
        throw new XmlPullParserException("expected " + printable(anInput) + " in " + new String(input) + " and not " + printable(ch), this, null);
      }
      ch = more();
    }
    return ch;
  }
  
  protected char requireNextS()
    throws XmlPullParserException, IOException
  {
    char ch = more();
    if (!isS(ch)) {
      throw new XmlPullParserException("white space is required and not " + printable(ch), this, null);
    }
    return skipS(ch);
  }
  
  protected char skipS(char ch)
    throws XmlPullParserException, IOException
  {
    while (isS(ch)) {
      ch = more();
    }
    return ch;
  }
  
  protected static boolean[] lookupNameStartChar = new boolean['Ѐ'];
  protected static boolean[] lookupNameChar = new boolean['Ѐ'];
  private static final char MIN_HIGH_SURROGATE = '?';
  private static final char MAX_HIGH_SURROGATE = '?';
  private static final int MAX_CODE_POINT = 1114111;
  private static final int MIN_SUPPLEMENTARY_CODE_POINT = 65536;
  
  private static final void setName(char ch)
  {
    lookupNameChar[ch] = true;
  }
  
  private static final void setNameStart(char ch)
  {
    lookupNameStartChar[ch] = true;
    setName(ch);
  }
  
  static
  {
    setNameStart(':');
    for (char ch = 'A'; ch <= 'Z'; ch = (char)(ch + '\001')) {
      setNameStart(ch);
    }
    setNameStart('_');
    for (char ch = 'a'; ch <= 'z'; ch = (char)(ch + '\001')) {
      setNameStart(ch);
    }
    for (char ch = 'À'; ch <= '˿'; ch = (char)(ch + '\001')) {
      setNameStart(ch);
    }
    for (char ch = 'Ͱ'; ch <= 'ͽ'; ch = (char)(ch + '\001')) {
      setNameStart(ch);
    }
    for (char ch = 'Ϳ'; ch < 'Ѐ'; ch = (char)(ch + '\001')) {
      setNameStart(ch);
    }
    setName('-');
    setName('.');
    for (char ch = '0'; ch <= '9'; ch = (char)(ch + '\001')) {
      setName(ch);
    }
    setName('·');
    for (char ch = '̀'; ch <= 'ͯ'; ch = (char)(ch + '\001')) {
      setName(ch);
    }
  }
  
  protected boolean isNameStartChar(char ch)
  {
    return ((ch < 'Ѐ') && (lookupNameStartChar[ch] != 0)) || ((ch >= 'Ѐ') && (ch <= '‧')) || ((ch >= '‪') && (ch <= '↏')) || ((ch >= '⠀') && (ch <= 65519));
  }
  
  protected boolean isNameChar(char ch)
  {
    return ((ch < 'Ѐ') && (lookupNameChar[ch] != 0)) || ((ch >= 'Ѐ') && (ch <= '‧')) || ((ch >= '‪') && (ch <= '↏')) || ((ch >= '⠀') && (ch <= 65519));
  }
  
  protected boolean isS(char ch)
  {
    return (ch == ' ') || (ch == '\n') || (ch == '\r') || (ch == '\t');
  }
  
  protected String printable(char ch)
  {
    if (ch == '\n') {
      return "\\n";
    }
    if (ch == '\r') {
      return "\\r";
    }
    if (ch == '\t') {
      return "\\t";
    }
    if (ch == '\'') {
      return "\\'";
    }
    if ((ch > '') || (ch < ' ')) {
      return "\\u" + Integer.toHexString(ch);
    }
    return "" + ch;
  }
  
  protected String printable(String s)
  {
    if (s == null) {
      return null;
    }
    int sLen = s.length();
    StringBuilder buf = new StringBuilder(sLen + 10);
    for (int i = 0; i < sLen; i++) {
      buf.append(printable(s.charAt(i)));
    }
    s = buf.toString();
    return s;
  }
  
  private static int toCodePoint(char high, char low)
  {
    int h = (high & 0x3FF) << '\n';
    int l = low & 0x3FF;
    return (h | l) + 65536;
  }
  
  private static boolean isHighSurrogate(char ch)
  {
    return (55296 <= ch) && (56319 >= ch);
  }
  
  private static boolean isValidCodePoint(int codePoint)
  {
    return (codePoint == 9) || (codePoint == 10) || (codePoint == 13) || ((32 <= codePoint) && (codePoint <= 55295)) || ((57344 <= codePoint) && (codePoint <= 65533)) || ((65536 <= codePoint) && (codePoint <= 1114111));
  }
  
  private static boolean isSupplementaryCodePoint(int codePoint)
  {
    return (65536 <= codePoint) && (1114111 >= codePoint);
  }
  
  public static char[] toChars(int codePoint)
  {
    if (!isValidCodePoint(codePoint)) {
      throw new IllegalArgumentException();
    }
    if (isSupplementaryCodePoint(codePoint))
    {
      int cpPrime = codePoint - 65536;
      int high = 0xD800 | cpPrime >> 10 & 0x3FF;
      int low = 0xDC00 | cpPrime & 0x3FF;
      return new char[] { (char)high, (char)low };
    }
    return new char[] { (char)codePoint };
  }
  
  protected void resetStringCache() {}
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.xml.pull.MXParser
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */