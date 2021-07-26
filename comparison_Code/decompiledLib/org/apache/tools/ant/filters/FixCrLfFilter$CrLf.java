package org.apache.tools.ant.filters;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class FixCrLfFilter$CrLf
  extends EnumeratedAttribute
{
  private static final CrLf ASIS = newInstance("asis");
  private static final CrLf CR = newInstance("cr");
  private static final CrLf CRLF = newInstance("crlf");
  private static final CrLf DOS = newInstance("dos");
  private static final CrLf LF = newInstance("lf");
  private static final CrLf MAC = newInstance("mac");
  private static final CrLf UNIX = newInstance("unix");
  
  public String[] getValues()
  {
    return new String[] { "asis", "cr", "lf", "crlf", "mac", "unix", "dos" };
  }
  
  public boolean equals(Object other)
  {
    return ((other instanceof CrLf)) && (getIndex() == ((CrLf)other).getIndex());
  }
  
  public int hashCode()
  {
    return getIndex();
  }
  
  CrLf resolve()
  {
    if (equals(ASIS)) {
      return ASIS;
    }
    if ((equals(CR)) || (equals(MAC))) {
      return CR;
    }
    if ((equals(CRLF)) || (equals(DOS))) {
      return CRLF;
    }
    if ((equals(LF)) || (equals(UNIX))) {
      return LF;
    }
    throw new IllegalStateException("No replacement for " + this);
  }
  
  private CrLf newInstance()
  {
    return newInstance(getValue());
  }
  
  public static CrLf newInstance(String value)
  {
    CrLf c = new CrLf();
    c.setValue(value);
    return c;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.FixCrLfFilter.CrLf
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */