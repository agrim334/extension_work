package org.apache.tools.ant.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LinkedHashtable<K, V>
  extends Hashtable<K, V>
{
  private static final long serialVersionUID = 1L;
  private final LinkedHashMap<K, V> map;
  
  public LinkedHashtable()
  {
    map = new LinkedHashMap();
  }
  
  public LinkedHashtable(int initialCapacity)
  {
    map = new LinkedHashMap(initialCapacity);
  }
  
  public LinkedHashtable(int initialCapacity, float loadFactor)
  {
    map = new LinkedHashMap(initialCapacity, loadFactor);
  }
  
  public LinkedHashtable(Map<K, V> m)
  {
    map = new LinkedHashMap(m);
  }
  
  public synchronized void clear()
  {
    map.clear();
  }
  
  public boolean contains(Object value)
  {
    return containsKey(value);
  }
  
  public synchronized boolean containsKey(Object value)
  {
    return map.containsKey(value);
  }
  
  public synchronized boolean containsValue(Object value)
  {
    return map.containsValue(value);
  }
  
  public Enumeration<V> elements()
  {
    return Collections.enumeration(values());
  }
  
  public synchronized Set<Map.Entry<K, V>> entrySet()
  {
    return map.entrySet();
  }
  
  public synchronized boolean equals(Object o)
  {
    return map.equals(o);
  }
  
  public synchronized V get(Object k)
  {
    return (V)map.get(k);
  }
  
  public synchronized int hashCode()
  {
    return map.hashCode();
  }
  
  public synchronized boolean isEmpty()
  {
    return map.isEmpty();
  }
  
  public Enumeration<K> keys()
  {
    return Collections.enumeration(keySet());
  }
  
  public synchronized Set<K> keySet()
  {
    return map.keySet();
  }
  
  public synchronized V put(K k, V v)
  {
    return (V)map.put(k, v);
  }
  
  public synchronized void putAll(Map<? extends K, ? extends V> m)
  {
    map.putAll(m);
  }
  
  public synchronized V remove(Object k)
  {
    return (V)map.remove(k);
  }
  
  public synchronized int size()
  {
    return map.size();
  }
  
  public synchronized String toString()
  {
    return map.toString();
  }
  
  public synchronized Collection<V> values()
  {
    return map.values();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.LinkedHashtable
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */