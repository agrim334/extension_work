package org.apache.tools.ant.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.Stack;

public class IdentityStack<E>
  extends Stack<E>
{
  private static final long serialVersionUID = -5555522620060077046L;
  
  public static <E> IdentityStack<E> getInstance(Stack<E> s)
  {
    if ((s instanceof IdentityStack)) {
      return (IdentityStack)s;
    }
    IdentityStack<E> result = new IdentityStack();
    if (s != null) {
      result.addAll(s);
    }
    return result;
  }
  
  public IdentityStack() {}
  
  public IdentityStack(E o)
  {
    push(o);
  }
  
  public synchronized boolean contains(Object o)
  {
    return indexOf(o) >= 0;
  }
  
  public synchronized int indexOf(Object o, int pos)
  {
    int size = size();
    for (int i = pos; i < size; i++) {
      if (get(i) == o) {
        return i;
      }
    }
    return -1;
  }
  
  public synchronized int lastIndexOf(Object o, int pos)
  {
    for (int i = pos; i >= 0; i--) {
      if (get(i) == o) {
        return i;
      }
    }
    return -1;
  }
  
  public synchronized boolean removeAll(Collection<?> c)
  {
    if (!(c instanceof Set)) {
      c = new HashSet(c);
    }
    return super.removeAll(c);
  }
  
  public synchronized boolean retainAll(Collection<?> c)
  {
    if (!(c instanceof Set)) {
      c = new HashSet(c);
    }
    return super.retainAll(c);
  }
  
  public synchronized boolean containsAll(Collection<?> c)
  {
    IdentityHashMap<Object, Boolean> map = new IdentityHashMap();
    for (Object e : this) {
      map.put(e, Boolean.TRUE);
    }
    return map.keySet().containsAll(c);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.IdentityStack
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */