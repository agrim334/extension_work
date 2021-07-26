package org.apache.tools.ant.property;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tools.ant.PropertyHelper;

public class LocalPropertyStack
{
  private final Deque<Map<String, Object>> stack = new LinkedList();
  private final Object LOCK = new Object();
  
  public void addLocal(String property)
  {
    synchronized (LOCK)
    {
      Map<String, Object> map = (Map)stack.peek();
      if (map != null) {
        map.put(property, NullReturn.NULL);
      }
    }
  }
  
  public void enterScope()
  {
    synchronized (LOCK)
    {
      stack.addFirst(new ConcurrentHashMap());
    }
  }
  
  public void exitScope()
  {
    synchronized (LOCK)
    {
      ((Map)stack.removeFirst()).clear();
    }
  }
  
  public LocalPropertyStack copy()
  {
    synchronized (LOCK)
    {
      LocalPropertyStack ret = new LocalPropertyStack();
      stack.addAll(stack);
      return ret;
    }
  }
  
  public Object evaluate(String property, PropertyHelper helper)
  {
    synchronized (LOCK)
    {
      for (Map<String, Object> map : stack)
      {
        Object ret = map.get(property);
        if (ret != null) {
          return ret;
        }
      }
    }
    return null;
  }
  
  public boolean setNew(String property, Object value, PropertyHelper propertyHelper)
  {
    Map<String, Object> map = getMapForProperty(property);
    if (map == null) {
      return false;
    }
    Object currValue = map.get(property);
    if (currValue == NullReturn.NULL) {
      map.put(property, value);
    }
    return true;
  }
  
  public boolean set(String property, Object value, PropertyHelper propertyHelper)
  {
    Map<String, Object> map = getMapForProperty(property);
    if (map == null) {
      return false;
    }
    map.put(property, value);
    return true;
  }
  
  private Map<String, Object> getMapForProperty(String property)
  {
    synchronized (LOCK)
    {
      for (Map<String, Object> map : stack) {
        if (map.get(property) != null) {
          return map;
        }
      }
    }
    return null;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.property.LocalPropertyStack
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */