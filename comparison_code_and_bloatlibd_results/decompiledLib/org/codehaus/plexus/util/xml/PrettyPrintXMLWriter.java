package org.codehaus.plexus.util.xml;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.plexus.util.StringUtils;

public class PrettyPrintXMLWriter
  implements XMLWriter
{
  protected static final String LS = System.getProperty("line.separator");
  private PrintWriter writer;
  private LinkedList<String> elementStack = new LinkedList();
  private boolean tagInProgress;
  private int depth;
  private String lineIndenter;
  private String lineSeparator;
  private String encoding;
  private String docType;
  private boolean readyForNewLine;
  private boolean tagIsEmpty;
  
  public PrettyPrintXMLWriter(PrintWriter writer, String lineIndenter)
  {
    this(writer, lineIndenter, null, null);
  }
  
  public PrettyPrintXMLWriter(Writer writer, String lineIndenter)
  {
    this(new PrintWriter(writer), lineIndenter);
  }
  
  public PrettyPrintXMLWriter(PrintWriter writer)
  {
    this(writer, null, null);
  }
  
  public PrettyPrintXMLWriter(Writer writer)
  {
    this(new PrintWriter(writer));
  }
  
  public PrettyPrintXMLWriter(PrintWriter writer, String lineIndenter, String encoding, String doctype)
  {
    this(writer, lineIndenter, LS, encoding, doctype);
  }
  
  public PrettyPrintXMLWriter(Writer writer, String lineIndenter, String encoding, String doctype)
  {
    this(new PrintWriter(writer), lineIndenter, encoding, doctype);
  }
  
  public PrettyPrintXMLWriter(PrintWriter writer, String encoding, String doctype)
  {
    this(writer, "  ", encoding, doctype);
  }
  
  public PrettyPrintXMLWriter(Writer writer, String encoding, String doctype)
  {
    this(new PrintWriter(writer), encoding, doctype);
  }
  
  public PrettyPrintXMLWriter(PrintWriter writer, String lineIndenter, String lineSeparator, String encoding, String doctype)
  {
    setWriter(writer);
    
    setLineIndenter(lineIndenter);
    
    setLineSeparator(lineSeparator);
    
    setEncoding(encoding);
    
    setDocType(doctype);
    if ((doctype != null) || (encoding != null)) {
      writeDocumentHeaders();
    }
  }
  
  public void startElement(String name)
  {
    tagIsEmpty = false;
    
    finishTag();
    
    write("<");
    
    write(name);
    
    elementStack.addLast(name);
    
    tagInProgress = true;
    
    setDepth(getDepth() + 1);
    
    readyForNewLine = true;
    
    tagIsEmpty = true;
  }
  
  public void writeText(String text)
  {
    writeText(text, true);
  }
  
  public void writeMarkup(String text)
  {
    writeText(text, false);
  }
  
  private void writeText(String text, boolean escapeXml)
  {
    readyForNewLine = false;
    
    tagIsEmpty = false;
    
    finishTag();
    if (escapeXml) {
      text = escapeXml(text);
    }
    write(StringUtils.unifyLineSeparators(text, lineSeparator));
  }
  
  private static final Pattern amp = Pattern.compile("&");
  private static final Pattern lt = Pattern.compile("<");
  private static final Pattern gt = Pattern.compile(">");
  private static final Pattern dqoute = Pattern.compile("\"");
  private static final Pattern sqoute = Pattern.compile("'");
  private static final String crlf_str = "\r\n";
  
  private static String escapeXml(String text)
  {
    if (text.indexOf('&') >= 0) {
      text = amp.matcher(text).replaceAll("&amp;");
    }
    if (text.indexOf('<') >= 0) {
      text = lt.matcher(text).replaceAll("&lt;");
    }
    if (text.indexOf('>') >= 0) {
      text = gt.matcher(text).replaceAll("&gt;");
    }
    if (text.indexOf('"') >= 0) {
      text = dqoute.matcher(text).replaceAll("&quot;");
    }
    if (text.indexOf('\'') >= 0) {
      text = sqoute.matcher(text).replaceAll("&apos;");
    }
    return text;
  }
  
  private static final Pattern crlf = Pattern.compile("\r\n");
  private static final Pattern lowers = Pattern.compile("([\000-\037])");
  
  private static String escapeXmlAttribute(String text)
  {
    text = escapeXml(text);
    
    Matcher crlfmatcher = crlf.matcher(text);
    if (text.contains("\r\n")) {
      text = crlfmatcher.replaceAll("&#10;");
    }
    Matcher m = lowers.matcher(text);
    StringBuffer b = new StringBuffer();
    while (m.find()) {
      m = m.appendReplacement(b, "&#" + Integer.toString(m.group(1).charAt(0)) + ";");
    }
    m.appendTail(b);
    
    return b.toString();
  }
  
  public void addAttribute(String key, String value)
  {
    write(" ");
    
    write(key);
    
    write("=\"");
    
    write(escapeXmlAttribute(value));
    
    write("\"");
  }
  
  public void endElement()
  {
    setDepth(getDepth() - 1);
    if (tagIsEmpty)
    {
      write("/");
      
      readyForNewLine = false;
      
      finishTag();
      
      elementStack.removeLast();
    }
    else
    {
      finishTag();
      
      write("</");
      write((String)elementStack.removeLast());
      write(">");
    }
    readyForNewLine = true;
  }
  
  private void write(String str)
  {
    getWriter().write(str);
  }
  
  private void finishTag()
  {
    if (tagInProgress) {
      write(">");
    }
    tagInProgress = false;
    if (readyForNewLine) {
      endOfLine();
    }
    readyForNewLine = false;
    
    tagIsEmpty = false;
  }
  
  protected String getLineIndenter()
  {
    return lineIndenter;
  }
  
  protected void setLineIndenter(String lineIndenter)
  {
    this.lineIndenter = lineIndenter;
  }
  
  protected String getLineSeparator()
  {
    return lineSeparator;
  }
  
  protected void setLineSeparator(String lineSeparator)
  {
    this.lineSeparator = lineSeparator;
  }
  
  protected void endOfLine()
  {
    write(getLineSeparator());
    for (int i = 0; i < getDepth(); i++) {
      write(getLineIndenter());
    }
  }
  
  private void writeDocumentHeaders()
  {
    write("<?xml version=\"1.0\"");
    if (getEncoding() != null) {
      write(" encoding=\"" + getEncoding() + "\"");
    }
    write("?>");
    
    endOfLine();
    if (getDocType() != null)
    {
      write("<!DOCTYPE ");
      
      write(getDocType());
      
      write(">");
      
      endOfLine();
    }
  }
  
  protected void setWriter(PrintWriter writer)
  {
    if (writer == null) {
      throw new IllegalArgumentException("writer could not be null");
    }
    this.writer = writer;
  }
  
  protected PrintWriter getWriter()
  {
    return writer;
  }
  
  protected void setDepth(int depth)
  {
    this.depth = depth;
  }
  
  protected int getDepth()
  {
    return depth;
  }
  
  protected void setEncoding(String encoding)
  {
    this.encoding = encoding;
  }
  
  protected String getEncoding()
  {
    return encoding;
  }
  
  protected void setDocType(String docType)
  {
    this.docType = docType;
  }
  
  protected String getDocType()
  {
    return docType;
  }
  
  protected LinkedList<String> getElementStack()
  {
    return elementStack;
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.xml.PrettyPrintXMLWriter
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */