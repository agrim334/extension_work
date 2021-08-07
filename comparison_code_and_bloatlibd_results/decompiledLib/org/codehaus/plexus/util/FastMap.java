package org.codehaus.plexus.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class FastMap<K, V>
  implements Map<K, V>, Cloneable, Serializable
{
  private transient EntryImpl[] _entries;
  private transient int _capacity;
  private transient int _mask;
  private transient EntryImpl _poolFirst;
  private transient EntryImpl _mapFirst;
  private transient EntryImpl _mapLast;
  private transient int _size;
  private transient FastMap<K, V>.Values _values;
  private transient FastMap<K, V>.EntrySet _entrySet;
  private transient FastMap<K, V>.KeySet _keySet;
  
  public FastMap()
  {
    initialize(256);
  }
  
  public FastMap(Map map)
  {
    int capacity = (map instanceof FastMap) ? ((FastMap)map).capacity() : map.size();
    initialize(capacity);
    putAll(map);
  }
  
  public FastMap(int capacity)
  {
    initialize(capacity);
  }
  
  public int size()
  {
    return _size;
  }
  
  public int capacity()
  {
    return _capacity;
  }
  
  public boolean isEmpty()
  {
    return _size == 0;
  }
  
  public boolean containsKey(Object key)
  {
    EntryImpl entry = _entries[(keyHash(key) & _mask)];
    while (entry != null)
    {
      if (key.equals(_key)) {
        return true;
      }
      entry = _next;
    }
    return false;
  }
  
  public boolean containsValue(Object value)
  {
    EntryImpl entry = _mapFirst;
    while (entry != null)
    {
      if (value.equals(_value)) {
        return true;
      }
      entry = _after;
    }
    return false;
  }
  
  public V get(Object key)
  {
    EntryImpl<K, V> entry = _entries[(keyHash(key) & _mask)];
    while (entry != null)
    {
      if (key.equals(_key)) {
        return (V)_value;
      }
      entry = _next;
    }
    return null;
  }
  
  public Map.Entry getEntry(Object key)
  {
    EntryImpl entry = _entries[(keyHash(key) & _mask)];
    while (entry != null)
    {
      if (key.equals(_key)) {
        return entry;
      }
      entry = _next;
    }
    return null;
  }
  
  public Object put(Object key, Object value)
  {
    EntryImpl entry = _entries[(keyHash(key) & _mask)];
    while (entry != null)
    {
      if (key.equals(_key))
      {
        Object prevValue = _value;
        _value = value;
        return prevValue;
      }
      entry = _next;
    }
    addEntry(key, value);
    return null;
  }
  
  public void putAll(Map<? extends K, ? extends V> map)
  {
    for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
      addEntry(entry.getKey(), entry.getValue());
    }
  }
  
  public V remove(Object key)
  {
    EntryImpl<K, V> entry = _entries[(keyHash(key) & _mask)];
    while (entry != null)
    {
      if (key.equals(_key))
      {
        V prevValue = _value;
        removeEntry(entry);
        return prevValue;
      }
      entry = _next;
    }
    return null;
  }
  
  public void clear()
  {
    for (EntryImpl entry = _mapFirst; entry != null; entry = _after)
    {
      _key = null;
      _value = null;
      _before = null;
      _next = null;
      if (_previous == null) {
        _entries[_index] = null;
      } else {
        _previous = null;
      }
    }
    if (_mapLast != null)
    {
      _mapLast._after = _poolFirst;
      _poolFirst = _mapFirst;
      _mapFirst = null;
      _mapLast = null;
      _size = 0;
      sizeChanged();
    }
  }
  
  public void setCapacity(int newCapacity)
  {
    if (newCapacity > _capacity) {
      for (int i = _capacity; i < newCapacity; i++)
      {
        EntryImpl entry = new EntryImpl(null);
        _after = _poolFirst;
        _poolFirst = entry;
      }
    } else if (newCapacity < _capacity) {
      for (int i = newCapacity; (i < _capacity) && (_poolFirst != null); i++)
      {
        EntryImpl entry = _poolFirst;
        _poolFirst = _after;
        _after = null;
      }
    }
    int tableLength = 16;
    while (tableLength < newCapacity) {
      tableLength <<= 1;
    }
    if (_entries.length != tableLength)
    {
      _entries = new EntryImpl[tableLength];
      _mask = (tableLength - 1);
      
      EntryImpl entry = _mapFirst;
      while (entry != null)
      {
        int index = keyHash(_key) & _mask;
        _index = index;
        
        _previous = null;
        EntryImpl next = _entries[index];
        _next = next;
        if (next != null) {
          _previous = entry;
        }
        _entries[index] = entry;
        
        entry = _after;
      }
    }
    _capacity = newCapacity;
  }
  
  public Object clone()
  {
    try
    {
      FastMap clone = (FastMap)super.clone();
      clone.initialize(_capacity);
      clone.putAll(this);
      return clone;
    }
    catch (CloneNotSupportedException e)
    {
      throw new InternalError();
    }
  }
  
  public boolean equals(Object obj)
  {
    if (obj == this) {
      return true;
    }
    if ((obj instanceof Map))
    {
      Map that = (Map)obj;
      if (size() == that.size())
      {
        EntryImpl entry = _mapFirst;
        while (entry != null)
        {
          if (!that.entrySet().contains(entry)) {
            return false;
          }
          entry = _after;
        }
        return true;
      }
      return false;
    }
    return false;
  }
  
  public int hashCode()
  {
    int code = 0;
    EntryImpl entry = _mapFirst;
    while (entry != null)
    {
      code += entry.hashCode();
      entry = _after;
    }
    return code;
  }
  
  public String toString()
  {
    return entrySet().toString();
  }
  
  public Collection values()
  {
    return _values;
  }
  
  private class Values
    extends AbstractCollection
  {
    private Values() {}
    
    public Iterator iterator()
    {
      new Iterator()
      {
        FastMap.EntryImpl after = _mapFirst;
        FastMap.EntryImpl before;
        
        public void remove()
        {
          FastMap.this.removeEntry(before);
        }
        
        public boolean hasNext()
        {
          return after != null;
        }
        
        public Object next()
        {
          before = after;
          after = after._after;
          return before._value;
        }
      };
    }
    
    public int size()
    {
      return _size;
    }
    
    public boolean contains(Object o)
    {
      return containsValue(o);
    }
    
    public void clear()
    {
      FastMap.this.clear();
    }
  }
  
  public Set entrySet()
  {
    return _entrySet;
  }
  
  private class EntrySet
    extends AbstractSet
  {
    private EntrySet() {}
    
    public Iterator iterator()
    {
      new Iterator()
      {
        FastMap.EntryImpl after = _mapFirst;
        FastMap.EntryImpl before;
        
        public void remove()
        {
          FastMap.this.removeEntry(before);
        }
        
        public boolean hasNext()
        {
          return after != null;
        }
        
        public Object next()
        {
          before = after;
          after = after._after;
          return before;
        }
      };
    }
    
    public int size()
    {
      return _size;
    }
    
    public boolean contains(Object obj)
    {
      if ((obj instanceof Map.Entry))
      {
        Map.Entry entry = (Map.Entry)obj;
        Map.Entry mapEntry = getEntry(entry.getKey());
        return entry.equals(mapEntry);
      }
      return false;
    }
    
    public boolean remove(Object obj)
    {
      if ((obj instanceof Map.Entry))
      {
        Map.Entry entry = (Map.Entry)obj;
        FastMap.EntryImpl mapEntry = (FastMap.EntryImpl)getEntry(entry.getKey());
        if ((mapEntry != null) && (entry.getValue().equals(_value)))
        {
          FastMap.this.removeEntry(mapEntry);
          return true;
        }
      }
      return false;
    }
  }
  
  public Set keySet()
  {
    return _keySet;
  }
  
  private class KeySet
    extends AbstractSet
  {
    private KeySet() {}
    
    public Iterator iterator()
    {
      new Iterator()
      {
        FastMap.EntryImpl after = _mapFirst;
        FastMap.EntryImpl before;
        
        public void remove()
        {
          FastMap.this.removeEntry(before);
        }
        
        public boolean hasNext()
        {
          return after != null;
        }
        
        public Object next()
        {
          before = after;
          after = after._after;
          return before._key;
        }
      };
    }
    
    public int size()
    {
      return _size;
    }
    
    public boolean contains(Object obj)
    {
      return containsKey(obj);
    }
    
    public boolean remove(Object obj)
    {
      return remove(obj) != null;
    }
    
    public void clear()
    {
      FastMap.this.clear();
    }
  }
  
  protected void sizeChanged()
  {
    if (size() > capacity()) {
      setCapacity(capacity() * 2);
    }
  }
  
  private static int keyHash(Object key)
  {
    int hashCode = key.hashCode();
    hashCode += (hashCode << 9 ^ 0xFFFFFFFF);
    hashCode ^= hashCode >>> 14;
    hashCode += (hashCode << 4);
    hashCode ^= hashCode >>> 10;
    return hashCode;
  }
  
  private void addEntry(Object key, Object value)
  {
    EntryImpl entry = _poolFirst;
    if (entry != null)
    {
      _poolFirst = _after;
      _after = null;
    }
    else
    {
      entry = new EntryImpl(null);
    }
    _key = key;
    _value = value;
    int index = keyHash(key) & _mask;
    _index = index;
    
    EntryImpl next = _entries[index];
    _next = next;
    if (next != null) {
      _previous = entry;
    }
    _entries[index] = entry;
    if (_mapLast != null)
    {
      _before = _mapLast;
      _mapLast._after = entry;
    }
    else
    {
      _mapFirst = entry;
    }
    _mapLast = entry;
    
    _size += 1;
    sizeChanged();
  }
  
  private void removeEntry(EntryImpl entry)
  {
    EntryImpl previous = _previous;
    EntryImpl next = _next;
    if (previous != null)
    {
      _next = next;
      _previous = null;
    }
    else
    {
      _entries[_index] = next;
    }
    if (next != null)
    {
      _previous = previous;
      _next = null;
    }
    EntryImpl before = _before;
    EntryImpl after = _after;
    if (before != null)
    {
      _after = after;
      _before = null;
    }
    else
    {
      _mapFirst = after;
    }
    if (after != null) {
      _before = before;
    } else {
      _mapLast = before;
    }
    _key = null;
    _value = null;
    
    _after = _poolFirst;
    _poolFirst = entry;
    
    _size -= 1;
    sizeChanged();
  }
  
  private void initialize(int capacity)
  {
    int tableLength = 16;
    while (tableLength < capacity) {
      tableLength <<= 1;
    }
    _entries = new EntryImpl[tableLength];
    _mask = (tableLength - 1);
    _capacity = capacity;
    _size = 0;
    
    _values = new Values(null);
    _entrySet = new EntrySet(null);
    _keySet = new KeySet(null);
    
    _poolFirst = null;
    _mapFirst = null;
    _mapLast = null;
    for (int i = 0; i < capacity; i++)
    {
      EntryImpl entry = new EntryImpl(null);
      _after = _poolFirst;
      _poolFirst = entry;
    }
  }
  
  private void readObject(ObjectInputStream stream)
    throws IOException, ClassNotFoundException
  {
    int capacity = stream.readInt();
    initialize(capacity);
    int size = stream.readInt();
    for (int i = 0; i < size; i++)
    {
      Object key = stream.readObject();
      Object value = stream.readObject();
      addEntry(key, value);
    }
  }
  
  private void writeObject(ObjectOutputStream stream)
    throws IOException
  {
    stream.writeInt(_capacity);
    stream.writeInt(_size);
    int count = 0;
    EntryImpl entry = _mapFirst;
    while (entry != null)
    {
      stream.writeObject(_key);
      stream.writeObject(_value);
      count++;
      entry = _after;
    }
    if (count != _size) {
      throw new IOException("FastMap Corrupted");
    }
  }
  
  private static final class EntryImpl<K, V>
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
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.FastMap
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */