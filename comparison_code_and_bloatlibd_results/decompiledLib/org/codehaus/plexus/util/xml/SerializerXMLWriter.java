package org.codehaus.plexus.util.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

public class SerializerXMLWriter
  implements XMLWriter
{
  private final XmlSerializer serializer;
  private final String namespace;
  private final Stack<String> elements = new Stack();
  private List<Exception> exceptions;
  
  public SerializerXMLWriter(String namespace, XmlSerializer serializer)
  {
    this.serializer = serializer;
    this.namespace = namespace;
  }
  
  public void startElement(String name)
  {
    try
    {
      serializer.startTag(namespace, name);
      elements.push(name);
    }
    catch (IOException e)
    {
      storeException(e);
    }
  }
  
  public void addAttribute(String key, String value)
  {
    try
    {
      serializer.attribute(namespace, key, value);
    }
    catch (IOException e)
    {
      storeException(e);
    }
  }
  
  public void writeText(String text)
  {
    try
    {
      serializer.text(text);
    }
    catch (IOException e)
    {
      storeException(e);
    }
  }
  
  public void writeMarkup(String text)
  {
    try
    {
      serializer.cdsect(text);
    }
    catch (IOException e)
    {
      storeException(e);
    }
  }
  
  public void endElement()
  {
    try
    {
      serializer.endTag(namespace, (String)elements.pop());
    }
    catch (IOException e)
    {
      storeException(e);
    }
  }
  
  private void storeException(IOException e)
  {
    if (exceptions == null) {
      exceptions = new ArrayList();
    }
    exceptions.add(e);
  }
  
  public List<Exception> getExceptions()
  {
    return exceptions == null ? Collections.emptyList() : exceptions;
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.xml.SerializerXMLWriter
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */