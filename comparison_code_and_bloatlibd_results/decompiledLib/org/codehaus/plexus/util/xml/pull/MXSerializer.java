package org.codehaus.plexus.util.xml.pull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class MXSerializer
  implements XmlSerializer
{
  protected static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
  protected static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
  private static final boolean TRACE_SIZING = false;
  protected final String FEATURE_SERIALIZER_ATTVALUE_USE_APOSTROPHE = "http://xmlpull.org/v1/doc/features.html#serializer-attvalue-use-apostrophe";
  protected final String FEATURE_NAMES_INTERNED = "http://xmlpull.org/v1/doc/features.html#names-interned";
  protected final String PROPERTY_SERIALIZER_INDENTATION = "http://xmlpull.org/v1/doc/properties.html#serializer-indentation";
  protected final String PROPERTY_SERIALIZER_LINE_SEPARATOR = "http://xmlpull.org/v1/doc/properties.html#serializer-line-separator";
  protected static final String PROPERTY_LOCATION = "http://xmlpull.org/v1/doc/properties.html#location";
  protected boolean namesInterned;
  protected boolean attributeUseApostrophe;
  protected String indentationString = null;
  protected String lineSeparator = "\n";
  protected String location;
  protected Writer out;
  protected int autoDeclaredPrefixes;
  protected int depth = 0;
  protected String[] elNamespace = new String[2];
  protected String[] elName = new String[elNamespace.length];
  protected int[] elNamespaceCount = new int[elNamespace.length];
  protected int namespaceEnd = 0;
  protected String[] namespacePrefix = new String[8];
  protected String[] namespaceUri = new String[namespacePrefix.length];
  protected boolean finished;
  protected boolean pastRoot;
  protected boolean setPrefixCalled;
  protected boolean startTagIncomplete;
  protected boolean doIndent;
  protected boolean seenTag;
  protected boolean seenBracket;
  protected boolean seenBracketBracket;
  private static final int BUF_LEN = Runtime.getRuntime().freeMemory() > 1000000L ? 8192 : 256;
  protected char[] buf = new char[BUF_LEN];
  protected static final String[] precomputedPrefixes = new String[32];
  
  static
  {
    for (int i = 0; i < precomputedPrefixes.length; i++) {
      precomputedPrefixes[i] = ("n" + i).intern();
    }
  }
  
  private boolean checkNamesInterned = false;
  protected int offsetNewLine;
  protected int indentationJump;
  protected char[] indentationBuf;
  protected int maxIndentLevel;
  protected boolean writeLineSeparator;
  protected boolean writeIndentation;
  
  private void checkInterning(String name)
  {
    if ((namesInterned) && (name != name.intern())) {
      throw new IllegalArgumentException("all names passed as arguments must be internedwhen NAMES INTERNED feature is enabled");
    }
  }
  
  protected void reset()
  {
    location = null;
    out = null;
    autoDeclaredPrefixes = 0;
    depth = 0;
    for (int i = 0; i < elNamespaceCount.length; i++)
    {
      elName[i] = null;
      elNamespace[i] = null;
      elNamespaceCount[i] = 2;
    }
    namespaceEnd = 0;
    
    namespacePrefix[namespaceEnd] = "xmlns";
    namespaceUri[namespaceEnd] = "http://www.w3.org/2000/xmlns/";
    namespaceEnd += 1;
    
    namespacePrefix[namespaceEnd] = "xml";
    namespaceUri[namespaceEnd] = "http://www.w3.org/XML/1998/namespace";
    namespaceEnd += 1;
    
    finished = false;
    pastRoot = false;
    setPrefixCalled = false;
    startTagIncomplete = false;
    
    seenTag = false;
    
    seenBracket = false;
    seenBracketBracket = false;
  }
  
  protected void ensureElementsCapacity()
  {
    int elStackSize = elName.length;
    
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
      System.arraycopy(elNamespace, 0, arr, 0, elStackSize);
    }
    elNamespace = arr;
    
    int[] iarr = new int[newSize];
    if (needsCopying) {
      System.arraycopy(elNamespaceCount, 0, iarr, 0, elStackSize);
    } else {
      iarr[0] = 0;
    }
    elNamespaceCount = iarr;
  }
  
  protected void ensureNamespacesCapacity()
  {
    int newSize = namespaceEnd > 7 ? 2 * namespaceEnd : 8;
    
    String[] newNamespacePrefix = new String[newSize];
    String[] newNamespaceUri = new String[newSize];
    if (namespacePrefix != null)
    {
      System.arraycopy(namespacePrefix, 0, newNamespacePrefix, 0, namespaceEnd);
      System.arraycopy(namespaceUri, 0, newNamespaceUri, 0, namespaceEnd);
    }
    namespacePrefix = newNamespacePrefix;
    namespaceUri = newNamespaceUri;
  }
  
  public void setFeature(String name, boolean state)
    throws IllegalArgumentException, IllegalStateException
  {
    if (name == null) {
      throw new IllegalArgumentException("feature name can not be null");
    }
    if ("http://xmlpull.org/v1/doc/features.html#names-interned".equals(name)) {
      namesInterned = state;
    } else if ("http://xmlpull.org/v1/doc/features.html#serializer-attvalue-use-apostrophe".equals(name)) {
      attributeUseApostrophe = state;
    } else {
      throw new IllegalStateException("unsupported feature " + name);
    }
  }
  
  public boolean getFeature(String name)
    throws IllegalArgumentException
  {
    if (name == null) {
      throw new IllegalArgumentException("feature name can not be null");
    }
    if ("http://xmlpull.org/v1/doc/features.html#names-interned".equals(name)) {
      return namesInterned;
    }
    if ("http://xmlpull.org/v1/doc/features.html#serializer-attvalue-use-apostrophe".equals(name)) {
      return attributeUseApostrophe;
    }
    return false;
  }
  
  protected void rebuildIndentationBuf()
  {
    if (!doIndent) {
      return;
    }
    int maxIndent = 65;
    int bufSize = 0;
    offsetNewLine = 0;
    if (writeLineSeparator)
    {
      offsetNewLine = lineSeparator.length();
      bufSize += offsetNewLine;
    }
    maxIndentLevel = 0;
    if (writeIndentation)
    {
      indentationJump = indentationString.length();
      maxIndentLevel = (65 / indentationJump);
      bufSize += maxIndentLevel * indentationJump;
    }
    if ((indentationBuf == null) || (indentationBuf.length < bufSize)) {
      indentationBuf = new char[bufSize + 8];
    }
    int bufPos = 0;
    if (writeLineSeparator) {
      for (int i = 0; i < lineSeparator.length(); i++) {
        indentationBuf[(bufPos++)] = lineSeparator.charAt(i);
      }
    }
    if (writeIndentation) {
      for (int i = 0; i < maxIndentLevel; i++) {
        for (int j = 0; j < indentationString.length(); j++) {
          indentationBuf[(bufPos++)] = indentationString.charAt(j);
        }
      }
    }
  }
  
  protected void writeIndent()
    throws IOException
  {
    int start = writeLineSeparator ? 0 : offsetNewLine;
    int level = depth > maxIndentLevel ? maxIndentLevel : depth;
    out.write(indentationBuf, start, level * indentationJump + offsetNewLine);
  }
  
  public void setProperty(String name, Object value)
    throws IllegalArgumentException, IllegalStateException
  {
    if (name == null) {
      throw new IllegalArgumentException("property name can not be null");
    }
    if ("http://xmlpull.org/v1/doc/properties.html#serializer-indentation".equals(name)) {
      indentationString = ((String)value);
    } else if ("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator".equals(name)) {
      lineSeparator = ((String)value);
    } else if ("http://xmlpull.org/v1/doc/properties.html#location".equals(name)) {
      location = ((String)value);
    } else {
      throw new IllegalStateException("unsupported property " + name);
    }
    writeLineSeparator = ((lineSeparator != null) && (lineSeparator.length() > 0));
    writeIndentation = ((indentationString != null) && (indentationString.length() > 0));
    
    doIndent = ((indentationString != null) && ((writeLineSeparator) || (writeIndentation)));
    
    rebuildIndentationBuf();
    seenTag = false;
  }
  
  public Object getProperty(String name)
    throws IllegalArgumentException
  {
    if (name == null) {
      throw new IllegalArgumentException("property name can not be null");
    }
    if ("http://xmlpull.org/v1/doc/properties.html#serializer-indentation".equals(name)) {
      return indentationString;
    }
    if ("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator".equals(name)) {
      return lineSeparator;
    }
    if ("http://xmlpull.org/v1/doc/properties.html#location".equals(name)) {
      return location;
    }
    return null;
  }
  
  private String getLocation()
  {
    return location != null ? " @" + location : "";
  }
  
  public Writer getWriter()
  {
    return out;
  }
  
  public void setOutput(Writer writer)
  {
    reset();
    out = writer;
  }
  
  public void setOutput(OutputStream os, String encoding)
    throws IOException
  {
    if (os == null) {
      throw new IllegalArgumentException("output stream can not be null");
    }
    reset();
    if (encoding != null) {
      out = new OutputStreamWriter(os, encoding);
    } else {
      out = new OutputStreamWriter(os);
    }
  }
  
  public void startDocument(String encoding, Boolean standalone)
    throws IOException
  {
    char apos = attributeUseApostrophe ? '\'' : '"';
    if (attributeUseApostrophe) {
      out.write("<?xml version='1.0'");
    } else {
      out.write("<?xml version=\"1.0\"");
    }
    if (encoding != null)
    {
      out.write(" encoding=");
      out.write(attributeUseApostrophe ? 39 : 34);
      out.write(encoding);
      out.write(attributeUseApostrophe ? 39 : 34);
    }
    if (standalone != null)
    {
      out.write(" standalone=");
      out.write(attributeUseApostrophe ? 39 : 34);
      if (standalone.booleanValue()) {
        out.write("yes");
      } else {
        out.write("no");
      }
      out.write(attributeUseApostrophe ? 39 : 34);
    }
    out.write("?>");
    if (writeLineSeparator) {
      out.write(lineSeparator);
    }
  }
  
  public void endDocument()
    throws IOException
  {
    while (depth > 0) {
      endTag(elNamespace[depth], elName[depth]);
    }
    if (writeLineSeparator) {
      out.write(lineSeparator);
    }
    finished = (pastRoot = startTagIncomplete = 1);
    out.flush();
  }
  
  public void setPrefix(String prefix, String namespace)
    throws IOException
  {
    if (startTagIncomplete) {
      closeStartTag();
    }
    if (prefix == null) {
      prefix = "";
    }
    if (!namesInterned) {
      prefix = prefix.intern();
    } else if (checkNamesInterned) {
      checkInterning(prefix);
    } else if (prefix == null) {
      throw new IllegalArgumentException("prefix must be not null" + getLocation());
    }
    for (int i = elNamespaceCount[depth]; i < namespaceEnd; i++) {
      if (prefix == namespacePrefix[i]) {
        throw new IllegalStateException("duplicated prefix " + printable(prefix) + getLocation());
      }
    }
    if (!namesInterned) {
      namespace = namespace.intern();
    } else if (checkNamesInterned) {
      checkInterning(namespace);
    } else if (namespace == null) {
      throw new IllegalArgumentException("namespace must be not null" + getLocation());
    }
    if (namespaceEnd >= namespacePrefix.length) {
      ensureNamespacesCapacity();
    }
    namespacePrefix[namespaceEnd] = prefix;
    namespaceUri[namespaceEnd] = namespace;
    namespaceEnd += 1;
    setPrefixCalled = true;
  }
  
  protected String lookupOrDeclarePrefix(String namespace)
  {
    return getPrefix(namespace, true);
  }
  
  public String getPrefix(String namespace, boolean generatePrefix)
  {
    if (!namesInterned) {
      namespace = namespace.intern();
    } else if (checkNamesInterned) {
      checkInterning(namespace);
    }
    if (namespace == null) {
      throw new IllegalArgumentException("namespace must be not null" + getLocation());
    }
    if (namespace.length() == 0) {
      throw new IllegalArgumentException("default namespace cannot have prefix" + getLocation());
    }
    for (int i = namespaceEnd - 1; i >= 0; i--) {
      if (namespace == namespaceUri[i])
      {
        String prefix = namespacePrefix[i];
        for (int p = namespaceEnd - 1; p > i; p--) {
          if (prefix != namespacePrefix[p]) {}
        }
        return prefix;
      }
    }
    if (!generatePrefix) {
      return null;
    }
    return generatePrefix(namespace);
  }
  
  private String generatePrefix(String namespace)
  {
    autoDeclaredPrefixes += 1;
    
    String prefix = autoDeclaredPrefixes < precomputedPrefixes.length ? precomputedPrefixes[autoDeclaredPrefixes] : ("n" + autoDeclaredPrefixes).intern();
    for (int i = namespaceEnd - 1; i >= 0; i--) {
      if (prefix != namespacePrefix[i]) {}
    }
    if (namespaceEnd >= namespacePrefix.length) {
      ensureNamespacesCapacity();
    }
    namespacePrefix[namespaceEnd] = prefix;
    namespaceUri[namespaceEnd] = namespace;
    namespaceEnd += 1;
    
    return prefix;
  }
  
  public int getDepth()
  {
    return depth;
  }
  
  public String getNamespace()
  {
    return elNamespace[depth];
  }
  
  public String getName()
  {
    return elName[depth];
  }
  
  public XmlSerializer startTag(String namespace, String name)
    throws IOException
  {
    if (startTagIncomplete) {
      closeStartTag();
    }
    seenBracket = (seenBracketBracket = 0);
    if ((doIndent) && (depth > 0) && (seenTag)) {
      writeIndent();
    }
    seenTag = true;
    setPrefixCalled = false;
    startTagIncomplete = true;
    depth += 1;
    if (depth + 1 >= elName.length) {
      ensureElementsCapacity();
    }
    if ((checkNamesInterned) && (namesInterned)) {
      checkInterning(namespace);
    }
    elNamespace[depth] = ((namesInterned) || (namespace == null) ? namespace : namespace.intern());
    if ((checkNamesInterned) && (namesInterned)) {
      checkInterning(name);
    }
    elName[depth] = ((namesInterned) || (name == null) ? name : name.intern());
    if (out == null) {
      throw new IllegalStateException("setOutput() must called set before serialization can start");
    }
    out.write(60);
    if (namespace != null) {
      if (namespace.length() > 0)
      {
        String prefix = null;
        if ((depth > 0) && (namespaceEnd - elNamespaceCount[(depth - 1)] == 1))
        {
          String uri = namespaceUri[(namespaceEnd - 1)];
          if ((uri == namespace) || (uri.equals(namespace)))
          {
            String elPfx = namespacePrefix[(namespaceEnd - 1)];
            for (int pos = elNamespaceCount[(depth - 1)] - 1; pos >= 2; pos--)
            {
              String pf = namespacePrefix[pos];
              if ((pf == elPfx) || (pf.equals(elPfx)))
              {
                String n = namespaceUri[pos];
                if ((n != uri) && (!n.equals(uri))) {
                  break;
                }
                namespaceEnd -= 1;
                prefix = elPfx; break;
              }
            }
          }
        }
        if (prefix == null) {
          prefix = lookupOrDeclarePrefix(namespace);
        }
        if (prefix.length() > 0)
        {
          out.write(prefix);
          out.write(58);
        }
      }
      else
      {
        for (int i = namespaceEnd - 1; i >= 0; i--) {
          if (namespacePrefix[i] == "")
          {
            String uri = namespaceUri[i];
            if (uri == null)
            {
              setPrefix("", ""); break;
            }
            if (uri.length() <= 0) {
              break;
            }
            throw new IllegalStateException("start tag can not be written in empty default namespace as default namespace is currently bound to '" + uri + "'" + getLocation());
          }
        }
      }
    }
    out.write(name);
    return this;
  }
  
  public XmlSerializer attribute(String namespace, String name, String value)
    throws IOException
  {
    if (!startTagIncomplete) {
      throw new IllegalArgumentException("startTag() must be called before attribute()" + getLocation());
    }
    out.write(32);
    if ((namespace != null) && (namespace.length() > 0))
    {
      if (!namesInterned) {
        namespace = namespace.intern();
      } else if (checkNamesInterned) {
        checkInterning(namespace);
      }
      String prefix = lookupOrDeclarePrefix(namespace);
      if (prefix.length() == 0) {
        prefix = generatePrefix(namespace);
      }
      out.write(prefix);
      out.write(58);
    }
    out.write(name);
    out.write(61);
    
    out.write(attributeUseApostrophe ? 39 : 34);
    writeAttributeValue(value, out);
    out.write(attributeUseApostrophe ? 39 : 34);
    return this;
  }
  
  protected void closeStartTag()
    throws IOException
  {
    if (finished) {
      throw new IllegalArgumentException("trying to write past already finished output" + getLocation());
    }
    if (seenBracket) {
      seenBracket = (seenBracketBracket = 0);
    }
    if ((startTagIncomplete) || (setPrefixCalled))
    {
      if (setPrefixCalled) {
        throw new IllegalArgumentException("startTag() must be called immediately after setPrefix()" + getLocation());
      }
      if (!startTagIncomplete) {
        throw new IllegalArgumentException("trying to close start tag that is not opened" + getLocation());
      }
      writeNamespaceDeclarations();
      out.write(62);
      elNamespaceCount[depth] = namespaceEnd;
      startTagIncomplete = false;
    }
  }
  
  private void writeNamespaceDeclarations()
    throws IOException
  {
    for (int i = elNamespaceCount[(depth - 1)]; i < namespaceEnd; i++)
    {
      if ((doIndent) && (namespaceUri[i].length() > 40))
      {
        writeIndent();
        out.write(" ");
      }
      if (namespacePrefix[i] != "")
      {
        out.write(" xmlns:");
        out.write(namespacePrefix[i]);
        out.write(61);
      }
      else
      {
        out.write(" xmlns=");
      }
      out.write(attributeUseApostrophe ? 39 : 34);
      
      writeAttributeValue(namespaceUri[i], out);
      
      out.write(attributeUseApostrophe ? 39 : 34);
    }
  }
  
  public XmlSerializer endTag(String namespace, String name)
    throws IOException
  {
    seenBracket = (seenBracketBracket = 0);
    if (namespace != null) {
      if (!namesInterned) {
        namespace = namespace.intern();
      } else if (checkNamesInterned) {
        checkInterning(namespace);
      }
    }
    if (namespace != elNamespace[depth]) {
      throw new IllegalArgumentException("expected namespace " + printable(elNamespace[depth]) + " and not " + printable(namespace) + getLocation());
    }
    if (name == null) {
      throw new IllegalArgumentException("end tag name can not be null" + getLocation());
    }
    if ((checkNamesInterned) && (namesInterned)) {
      checkInterning(name);
    }
    if (((!namesInterned) && (!name.equals(elName[depth]))) || ((namesInterned) && (name != elName[depth]))) {
      throw new IllegalArgumentException("expected element name " + printable(elName[depth]) + " and not " + printable(name) + getLocation());
    }
    if (startTagIncomplete)
    {
      writeNamespaceDeclarations();
      out.write(" />");
      depth -= 1;
    }
    else
    {
      depth -= 1;
      if ((doIndent) && (seenTag)) {
        writeIndent();
      }
      out.write("</");
      if ((namespace != null) && (namespace.length() > 0))
      {
        String prefix = lookupOrDeclarePrefix(namespace);
        if (prefix.length() > 0)
        {
          out.write(prefix);
          out.write(58);
        }
      }
      out.write(name);
      out.write(62);
    }
    namespaceEnd = elNamespaceCount[depth];
    startTagIncomplete = false;
    seenTag = true;
    return this;
  }
  
  public XmlSerializer text(String text)
    throws IOException
  {
    if ((startTagIncomplete) || (setPrefixCalled)) {
      closeStartTag();
    }
    if ((doIndent) && (seenTag)) {
      seenTag = false;
    }
    writeElementContent(text, out);
    return this;
  }
  
  public XmlSerializer text(char[] buf, int start, int len)
    throws IOException
  {
    if ((startTagIncomplete) || (setPrefixCalled)) {
      closeStartTag();
    }
    if ((doIndent) && (seenTag)) {
      seenTag = false;
    }
    writeElementContent(buf, start, len, out);
    return this;
  }
  
  public void cdsect(String text)
    throws IOException
  {
    if ((startTagIncomplete) || (setPrefixCalled) || (seenBracket)) {
      closeStartTag();
    }
    if ((doIndent) && (seenTag)) {
      seenTag = false;
    }
    out.write("<![CDATA[");
    out.write(text);
    out.write("]]>");
  }
  
  public void entityRef(String text)
    throws IOException
  {
    if ((startTagIncomplete) || (setPrefixCalled) || (seenBracket)) {
      closeStartTag();
    }
    if ((doIndent) && (seenTag)) {
      seenTag = false;
    }
    out.write(38);
    out.write(text);
    out.write(59);
  }
  
  public void processingInstruction(String text)
    throws IOException
  {
    if ((startTagIncomplete) || (setPrefixCalled) || (seenBracket)) {
      closeStartTag();
    }
    if ((doIndent) && (seenTag)) {
      seenTag = false;
    }
    out.write("<?");
    out.write(text);
    out.write("?>");
  }
  
  public void comment(String text)
    throws IOException
  {
    if ((startTagIncomplete) || (setPrefixCalled) || (seenBracket)) {
      closeStartTag();
    }
    if ((doIndent) && (seenTag)) {
      seenTag = false;
    }
    out.write("<!--");
    out.write(text);
    out.write("-->");
  }
  
  public void docdecl(String text)
    throws IOException
  {
    if ((startTagIncomplete) || (setPrefixCalled) || (seenBracket)) {
      closeStartTag();
    }
    if ((doIndent) && (seenTag)) {
      seenTag = false;
    }
    out.write("<!DOCTYPE ");
    out.write(text);
    out.write(">");
  }
  
  public void ignorableWhitespace(String text)
    throws IOException
  {
    if ((startTagIncomplete) || (setPrefixCalled) || (seenBracket)) {
      closeStartTag();
    }
    if ((doIndent) && (seenTag)) {
      seenTag = false;
    }
    if (text.length() == 0) {
      throw new IllegalArgumentException("empty string is not allowed for ignorable whitespace" + getLocation());
    }
    out.write(text);
  }
  
  public void flush()
    throws IOException
  {
    if ((!finished) && (startTagIncomplete)) {
      closeStartTag();
    }
    out.flush();
  }
  
  protected void writeAttributeValue(String value, Writer out)
    throws IOException
  {
    char quot = attributeUseApostrophe ? '\'' : '"';
    String quotEntity = attributeUseApostrophe ? "&apos;" : "&quot;";
    
    int pos = 0;
    for (int i = 0; i < value.length(); i++)
    {
      char ch = value.charAt(i);
      if (ch == '&')
      {
        if (i > pos) {
          out.write(value.substring(pos, i));
        }
        out.write("&amp;");
        pos = i + 1;
      }
      if (ch == '<')
      {
        if (i > pos) {
          out.write(value.substring(pos, i));
        }
        out.write("&lt;");
        pos = i + 1;
      }
      else if (ch == quot)
      {
        if (i > pos) {
          out.write(value.substring(pos, i));
        }
        out.write(quotEntity);
        pos = i + 1;
      }
      else if (ch < ' ')
      {
        if ((ch == '\r') || (ch == '\n') || (ch == '\t'))
        {
          if (i > pos) {
            out.write(value.substring(pos, i));
          }
          out.write("&#");
          out.write(Integer.toString(ch));
          out.write(59);
          pos = i + 1;
        }
        else
        {
          throw new IllegalStateException("character " + Integer.toString(ch) + " is not allowed in output" + getLocation());
        }
      }
    }
    if (pos > 0) {
      out.write(value.substring(pos));
    } else {
      out.write(value);
    }
  }
  
  protected void writeElementContent(String text, Writer out)
    throws IOException
  {
    int pos = 0;
    for (int i = 0; i < text.length(); i++)
    {
      char ch = text.charAt(i);
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
        if (ch == '&')
        {
          if (i > pos) {
            out.write(text.substring(pos, i));
          }
          out.write("&amp;");
          pos = i + 1;
        }
        else if (ch == '<')
        {
          if (i > pos) {
            out.write(text.substring(pos, i));
          }
          out.write("&lt;");
          pos = i + 1;
        }
        else if ((seenBracketBracket) && (ch == '>'))
        {
          if (i > pos) {
            out.write(text.substring(pos, i));
          }
          out.write("&gt;");
          pos = i + 1;
        }
        else if (ch < ' ')
        {
          if ((ch != '\t') && (ch != '\n') && (ch != '\r')) {
            throw new IllegalStateException("character " + Integer.toString(ch) + " is not allowed in output" + getLocation());
          }
        }
        if (seenBracket) {
          seenBracketBracket = (seenBracket = 0);
        }
      }
    }
    if (pos > 0) {
      out.write(text.substring(pos));
    } else {
      out.write(text);
    }
  }
  
  protected void writeElementContent(char[] buf, int off, int len, Writer out)
    throws IOException
  {
    int end = off + len;
    int pos = off;
    for (int i = off; i < end; i++)
    {
      char ch = buf[i];
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
        if (ch == '&')
        {
          if (i > pos) {
            out.write(buf, pos, i - pos);
          }
          out.write("&amp;");
          pos = i + 1;
        }
        else if (ch == '<')
        {
          if (i > pos) {
            out.write(buf, pos, i - pos);
          }
          out.write("&lt;");
          pos = i + 1;
        }
        else if ((seenBracketBracket) && (ch == '>'))
        {
          if (i > pos) {
            out.write(buf, pos, i - pos);
          }
          out.write("&gt;");
          pos = i + 1;
        }
        else if (ch < ' ')
        {
          if ((ch != '\t') && (ch != '\n') && (ch != '\r')) {
            throw new IllegalStateException("character " + Integer.toString(ch) + " is not allowed in output" + getLocation());
          }
        }
        if (seenBracket) {
          seenBracketBracket = (seenBracket = 0);
        }
      }
    }
    if (end > pos) {
      out.write(buf, pos, end - pos);
    }
  }
  
  protected static final String printable(String s)
  {
    if (s == null) {
      return "null";
    }
    StringBuilder retval = new StringBuilder(s.length() + 16);
    retval.append("'");
    for (int i = 0; i < s.length(); i++) {
      addPrintable(retval, s.charAt(i));
    }
    retval.append("'");
    return retval.toString();
  }
  
  protected static final String printable(char ch)
  {
    StringBuilder retval = new StringBuilder();
    addPrintable(retval, ch);
    return retval.toString();
  }
  
  private static void addPrintable(StringBuilder retval, char ch)
  {
    switch (ch)
    {
    case '\b': 
      retval.append("\\b");
      break;
    case '\t': 
      retval.append("\\t");
      break;
    case '\n': 
      retval.append("\\n");
      break;
    case '\f': 
      retval.append("\\f");
      break;
    case '\r': 
      retval.append("\\r");
      break;
    case '"': 
      retval.append("\\\"");
      break;
    case '\'': 
      retval.append("\\'");
      break;
    case '\\': 
      retval.append("\\\\");
      break;
    default: 
      if ((ch < ' ') || (ch > '~'))
      {
        String ss = "0000" + Integer.toString(ch, 16);
        retval.append("\\u").append(ss, ss.length() - 4, ss.length());
      }
      else
      {
        retval.append(ch);
      }
      break;
    }
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.xml.pull.MXSerializer
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */