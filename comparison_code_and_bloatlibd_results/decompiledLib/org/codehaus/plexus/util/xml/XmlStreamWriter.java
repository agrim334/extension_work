package org.codehaus.plexus.util.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlStreamWriter
  extends Writer
{
  private static final int BUFFER_SIZE = 4096;
  private StringWriter xmlPrologWriter = new StringWriter(4096);
  private OutputStream out;
  private Writer writer;
  private String encoding;
  
  public XmlStreamWriter(OutputStream out)
  {
    this.out = out;
  }
  
  public XmlStreamWriter(File file)
    throws FileNotFoundException
  {
    this(new FileOutputStream(file));
  }
  
  public String getEncoding()
  {
    return encoding;
  }
  
  public void close()
    throws IOException
  {
    if (writer == null)
    {
      encoding = "UTF-8";
      writer = new OutputStreamWriter(out, encoding);
      writer.write(xmlPrologWriter.toString());
    }
    writer.close();
  }
  
  public void flush()
    throws IOException
  {
    if (writer != null) {
      writer.flush();
    }
  }
  
  private void detectEncoding(char[] cbuf, int off, int len)
    throws IOException
  {
    int size = len;
    StringBuffer xmlProlog = xmlPrologWriter.getBuffer();
    if (xmlProlog.length() + len > 4096) {
      size = 4096 - xmlProlog.length();
    }
    xmlPrologWriter.write(cbuf, off, size);
    if (xmlProlog.length() >= 5)
    {
      if (xmlProlog.substring(0, 5).equals("<?xml"))
      {
        int xmlPrologEnd = xmlProlog.indexOf("?>");
        if (xmlPrologEnd > 0)
        {
          Matcher m = ENCODING_PATTERN.matcher(xmlProlog.substring(0, xmlPrologEnd));
          if (m.find())
          {
            encoding = m.group(1).toUpperCase(Locale.ENGLISH);
            encoding = encoding.substring(1, encoding.length() - 1);
          }
          else
          {
            encoding = "UTF-8";
          }
        }
        else if (xmlProlog.length() >= 4096)
        {
          encoding = "UTF-8";
        }
      }
      else
      {
        encoding = "UTF-8";
      }
      if (encoding != null)
      {
        xmlPrologWriter = null;
        writer = new OutputStreamWriter(out, encoding);
        writer.write(xmlProlog.toString());
        if (len > size) {
          writer.write(cbuf, off + size, len - size);
        }
      }
    }
  }
  
  public void write(char[] cbuf, int off, int len)
    throws IOException
  {
    if (xmlPrologWriter != null) {
      detectEncoding(cbuf, off, len);
    } else {
      writer.write(cbuf, off, len);
    }
  }
  
  static final Pattern ENCODING_PATTERN = XmlReader.ENCODING_PATTERN;
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.xml.XmlStreamWriter
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */