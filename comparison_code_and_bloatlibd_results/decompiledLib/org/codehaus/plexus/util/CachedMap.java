package org.codehaus.plexus.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class CachedMap
  implements Map
{
  private final FastMap _backingFastMap;
  private final Map _backingMap;
  private final FastMap _keysMap;
  private final int _mask;
  private final Object[] _keys;
  private final Object[] _values;
  
  public CachedMap()
  {
    this(256, new FastMap());
  }
  
  public CachedMap(int cacheSize)
  {
    this(cacheSize, new FastMap(cacheSize));
  }
  
  public CachedMap(int cacheSize, Map backingMap)
  {
    int actualCacheSize = 1;
    while (actualCacheSize < cacheSize) {
      actualCacheSize <<= 1;
    }
    _keys = new Object[actualCacheSize];
    _values = new Object[actualCacheSize];
    _mask = (actualCacheSize - 1);
    if ((backingMap instanceof FastMap))
    {
      _backingFastMap = ((FastMap)backingMap);
      _backingMap = _backingFastMap;
      _keysMap = null;
    }
    else
    {
      _backingFastMap = null;
      _backingMap = backingMap;
      _keysMap = new FastMap(backingMap.size());
      for (Object key : backingMap.keySet()) {
        _keysMap.put(key, key);
      }
    }
  }
  
  public int getCacheSize()
  {
    return _keys.length;
  }
  
  public Map getBackingMap()
  {
    return _backingFastMap != null ? _backingFastMap : _backingMap;
  }
  
  public void flush()
  {
    for (int i = 0; i < _keys.length; i++)
    {
      _keys[i] = null;
      _values[i] = null;
    }
    if (_keysMap != null) {
      for (Object key : _backingMap.keySet()) {
        _keysMap.put(key, key);
      }
    }
  }
  
  public Object get(Object key)
  {
    int index = key.hashCode() & _mask;
    return key.equals(_keys[index]) ? _values[index] : getCacheMissed(key, index);
  }
  
  private Object getCacheMissed(Object key, int index)
  {
    if (_backingFastMap != null)
    {
      Map.Entry entry = _backingFastMap.getEntry(key);
      if (entry != null)
      {
        _keys[index] = entry.getKey();
        Object value = entry.getValue();
        _values[index] = value;
        return value;
      }
      return null;
    }
    Object mapKey = _keysMap.get(key);
    if (mapKey != null)
    {
      _keys[index] = mapKey;
      Object value = _backingMap.get(key);
      _values[index] = value;
      return value;
    }
    return null;
  }
  
  public Object put(Object key, Object value)
  {
    int index = key.hashCode() & _mask;
    if (key.equals(_keys[index])) {
      _values[index] = value;
    } else if (_keysMap != null) {
      _keysMap.put(key, key);
    }
    return _backingMap.put(key, value);
  }
  
  public Object remove(Object key)
  {
    int index = key.hashCode() & _mask;
    if (key.equals(_keys[index])) {
      _keys[index] = null;
    }
    if (_keysMap != null) {
      _keysMap.remove(key);
    }
    return _backingMap.remove(key);
  }
  
  public boolean containsKey(Object key)
  {
    int index = key.hashCode() & _mask;
    if (key.equals(_keys[index])) {
      return true;
    }
    return _backingMap.containsKey(key);
  }
  
  public int size()
  {
    return _backingMap.size();
  }
  
  public boolean isEmpty()
  {
    return _backingMap.isEmpty();
  }
  
  public boolean containsValue(Object value)
  {
    return _backingMap.containsValue(value);
  }
  
  public void putAll(Map map)
  {
    _backingMap.putAll(map);
    flush();
  }
  
  public void clear()
  {
    _backingMap.clear();
    flush();
  }
  
  public Set keySet()
  {
    return Collections.unmodifiableSet(_backingMap.keySet());
  }
  
  public Collection values()
  {
    return Collections.unmodifiableCollection(_backingMap.values());
  }
  
  public Set entrySet()
  {
    return Collections.unmodifiableSet(_backingMap.entrySet());
  }
  
  public boolean equals(Object o)
  {
    return _backingMap.equals(o);
  }
  
  public int hashCode()
  {
    return _backingMap.hashCode();
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.CachedMap
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */