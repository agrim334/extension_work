package org.apache.tools.ant.util;

public class DOMElementWriter$XmlNamespacePolicy
{
  private boolean qualifyElements;
  private boolean qualifyAttributes;
  public static final XmlNamespacePolicy IGNORE = new XmlNamespacePolicy(false, false);
  public static final XmlNamespacePolicy ONLY_QUALIFY_ELEMENTS = new XmlNamespacePolicy(true, false);
  public static final XmlNamespacePolicy QUALIFY_ALL = new XmlNamespacePolicy(true, true);
  
  public DOMElementWriter$XmlNamespacePolicy(boolean qualifyElements, boolean qualifyAttributes)
  {
    this.qualifyElements = qualifyElements;
    this.qualifyAttributes = qualifyAttributes;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.DOMElementWriter.XmlNamespacePolicy
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */