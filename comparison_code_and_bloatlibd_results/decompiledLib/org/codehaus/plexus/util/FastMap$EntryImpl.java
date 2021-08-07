package org.codehaus.plexus.util;

import java.util.Map.Entry;

final class FastMap$EntryImpl<K, V>
  implements Map.Entry<K, V>
{
  private K _key;
  private V _value;
  private int _index;
  private EntryImpl _previous;
  private EntryImpl _next;
  private EntryImpl _before;
  private EntryImpl _after;
  
  public K getKey()
  {
    return (K)_key;
  }
  
  public V getValue()
  {
    return (V)_value;
  }
  
  public V setValue(V value)
  {
    V old = _value;
    _value = value;
    return old;
  }
  
  public boolean equals(Object that)
  {
    if ((that instanceof Map.Entry))
    {
      Map.Entry entry = (Map.Entry)that;
      return (_key.equals(entry.getKey())) && (_value != null ? _value.equals(entry.getValue()) : entry.getValue() == null);
    }
    return false;
  }
  
  public int hashCode()
  {
    return _key.hashCode() ^ (_value != null ? _value.hashCode() : 0);
  }
  
  public String toString()
  {
    return _key + "=" + _value;
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.FastMap.EntryImpl
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */