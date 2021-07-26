package org.apache.tools.ant.util;

class LayoutPreservingProperties$Pair
  extends LayoutPreservingProperties.LogicalLine
  implements Cloneable
{
  private static final long serialVersionUID = 1L;
  private String name;
  private String value;
  private boolean added;
  
  public LayoutPreservingProperties$Pair(String text)
  {
    super(text);
    parsePair(text);
  }
  
  public LayoutPreservingProperties$Pair(String name, String value)
  {
    this(name + "=" + value);
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public void setValue(String value)
  {
    this.value = value;
    setText(name + "=" + value);
  }
  
  public boolean isNew()
  {
    return added;
  }
  
  public void setNew(boolean val)
  {
    added = val;
  }
  
  public Object clone()
  {
    Object dolly = null;
    try
    {
      dolly = super.clone();
    }
    catch (CloneNotSupportedException e)
    {
      e.printStackTrace();
    }
    return dolly;
  }
  
  private void parsePair(String text)
  {
    int pos = findFirstSeparator(text);
    if (pos == -1)
    {
      name = text;
      setValue(null);
    }
    else
    {
      name = text.substring(0, pos);
      setValue(text.substring(pos + 1));
    }
    name = stripStart(name, " \t\f");
  }
  
  private String stripStart(String s, String chars)
  {
    if (s == null) {
      return null;
    }
    for (int i = 0; i < s.length(); i++) {
      if (chars.indexOf(s.charAt(i)) == -1) {
        break;
      }
    }
    if (i == s.length()) {
      return "";
    }
    return s.substring(i);
  }
  
  private int findFirstSeparator(String s)
  {
    s = s.replaceAll("\\\\\\\\", "__");
    
    s = s.replaceAll("\\\\=", "__");
    s = s.replaceAll("\\\\:", "__");
    s = s.replaceAll("\\\\ ", "__");
    s = s.replaceAll("\\\\t", "__");
    
    return indexOfAny(s, " :=\t");
  }
  
  private int indexOfAny(String s, String chars)
  {
    if ((s == null) || (chars == null)) {
      return -1;
    }
    int p = s.length() + 1;
    for (int i = 0; i < chars.length(); i++)
    {
      int x = s.indexOf(chars.charAt(i));
      if ((x != -1) && (x < p)) {
        p = x;
      }
    }
    if (p == s.length() + 1) {
      return -1;
    }
    return p;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.LayoutPreservingProperties.Pair
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */