package org.apache.tools.ant.types.selectors.modifiedselector;

import java.util.Iterator;

public abstract interface Cache
{
  public abstract boolean isValid();
  
  public abstract void delete();
  
  public abstract void load();
  
  public abstract void save();
  
  public abstract Object get(Object paramObject);
  
  public abstract void put(Object paramObject1, Object paramObject2);
  
  public abstract Iterator<String> iterator();
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.selectors.modifiedselector.Cache
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */