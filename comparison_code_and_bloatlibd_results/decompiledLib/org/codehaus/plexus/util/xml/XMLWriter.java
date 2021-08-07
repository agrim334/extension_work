package org.codehaus.plexus.util.xml;

public abstract interface XMLWriter
{
  public abstract void startElement(String paramString);
  
  public abstract void addAttribute(String paramString1, String paramString2);
  
  public abstract void writeText(String paramString);
  
  public abstract void writeMarkup(String paramString);
  
  public abstract void endElement();
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.xml.XMLWriter
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */