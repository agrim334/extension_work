package org.apache.tools.ant.util;

import java.util.Enumeration;
import java.util.Hashtable;

@Deprecated
public class LazyHashtable<K, V>
  extends Hashtable<K, V>
{
  protected boolean initAllDone = false;
  
  protected void initAll()
  {
    if (initAllDone) {
      return;
    }
    initAllDone = true;
  }
  
  public Enumeration<V> elements()
  {
    initAll();
    return super.elements();
  }
  
  public boolean isEmpty()
  {
    initAll();
    return super.isEmpty();
  }
  
  public int size()
  {
    initAll();
    return super.size();
  }
  
  public boolean contains(Object value)
  {
    initAll();
    return super.contains(value);
  }
  
  public boolean containsKey(Object value)
  {
    initAll();
    return super.containsKey(value);
  }
  
  public boolean containsValue(Object value)
  {
    return contains(value);
  }
  
  public Enumeration<K> keys()
  {
    initAll();
    return super.keys();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.LazyHashtable
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */