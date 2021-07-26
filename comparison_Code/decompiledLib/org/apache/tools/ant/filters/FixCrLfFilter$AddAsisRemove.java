package org.apache.tools.ant.filters;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class FixCrLfFilter$AddAsisRemove
  extends EnumeratedAttribute
{
  private static final AddAsisRemove ASIS = newInstance("asis");
  private static final AddAsisRemove ADD = newInstance("add");
  private static final AddAsisRemove REMOVE = newInstance("remove");
  
  public String[] getValues()
  {
    return new String[] { "add", "asis", "remove" };
  }
  
  public boolean equals(Object other)
  {
    return ((other instanceof AddAsisRemove)) && 
      (getIndex() == ((AddAsisRemove)other).getIndex());
  }
  
  public int hashCode()
  {
    return getIndex();
  }
  
  AddAsisRemove resolve()
    throws IllegalStateException
  {
    if (equals(ASIS)) {
      return ASIS;
    }
    if (equals(ADD)) {
      return ADD;
    }
    if (equals(REMOVE)) {
      return REMOVE;
    }
    throw new IllegalStateException("No replacement for " + this);
  }
  
  private AddAsisRemove newInstance()
  {
    return newInstance(getValue());
  }
  
  public static AddAsisRemove newInstance(String value)
  {
    AddAsisRemove a = new AddAsisRemove();
    a.setValue(value);
    return a;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.FixCrLfFilter.AddAsisRemove
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */