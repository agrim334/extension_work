package org.apache.tools.ant.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

public final class VectorSet<E>
  extends Vector<E>
{
  private static final long serialVersionUID = 1L;
  private final HashSet<E> set = new HashSet();
  
  public VectorSet() {}
  
  public VectorSet(int initialCapacity)
  {
    super(initialCapacity);
  }
  
  public VectorSet(int initialCapacity, int capacityIncrement)
  {
    super(initialCapacity, capacityIncrement);
  }
  
  public VectorSet(Collection<? extends E> c)
  {
    if (c != null) {
      addAll(c);
    }
  }
  
  public synchronized boolean add(E o)
  {
    if (!set.contains(o))
    {
      doAdd(size(), o);
      return true;
    }
    return false;
  }
  
  public void add(int index, E o)
  {
    doAdd(index, o);
  }
  
  private synchronized void doAdd(int index, E o)
  {
    if (set.add(o))
    {
      int count = size();
      ensureCapacity(count + 1);
      if (index != count) {
        System.arraycopy(elementData, index, elementData, index + 1, count - index);
      }
      elementData[index] = o;
      elementCount += 1;
    }
  }
  
  public synchronized void addElement(E o)
  {
    doAdd(size(), o);
  }
  
  public synchronized boolean addAll(Collection<? extends E> c)
  {
    boolean changed = false;
    for (E e : c) {
      changed |= add(e);
    }
    return changed;
  }
  
  public synchronized boolean addAll(int index, Collection<? extends E> c)
  {
    LinkedList<E> toAdd = new LinkedList();
    for (Iterator localIterator = c.iterator(); localIterator.hasNext();)
    {
      e = localIterator.next();
      if (set.add(e)) {
        toAdd.add(e);
      }
    }
    E e;
    if (toAdd.isEmpty()) {
      return false;
    }
    int count = size();
    ensureCapacity(count + toAdd.size());
    if (index != count) {
      System.arraycopy(elementData, index, elementData, index + toAdd.size(), count - index);
    }
    for (Object o : toAdd) {
      elementData[(index++)] = o;
    }
    elementCount += toAdd.size();
    return true;
  }
  
  public synchronized void clear()
  {
    super.clear();
    set.clear();
  }
  
  public Object clone()
  {
    VectorSet<E> vs = (VectorSet)super.clone();
    set.addAll(set);
    return vs;
  }
  
  public synchronized boolean contains(Object o)
  {
    return set.contains(o);
  }
  
  public synchronized boolean containsAll(Collection<?> c)
  {
    return set.containsAll(c);
  }
  
  public void insertElementAt(E o, int index)
  {
    doAdd(index, o);
  }
  
  public synchronized E remove(int index)
  {
    E o = get(index);
    remove(o);
    return o;
  }
  
  public boolean remove(Object o)
  {
    return doRemove(o);
  }
  
  private synchronized boolean doRemove(Object o)
  {
    if (set.remove(o))
    {
      int index = indexOf(o);
      if (index < elementData.length - 1) {
        System.arraycopy(elementData, index + 1, elementData, index, elementData.length - index - 1);
      }
      elementCount -= 1;
      return true;
    }
    return false;
  }
  
  public synchronized boolean removeAll(Collection<?> c)
  {
    boolean changed = false;
    for (Object o : c) {
      changed |= remove(o);
    }
    return changed;
  }
  
  public synchronized void removeAllElements()
  {
    set.clear();
    super.removeAllElements();
  }
  
  public boolean removeElement(Object o)
  {
    return doRemove(o);
  }
  
  public synchronized void removeElementAt(int index)
  {
    remove(get(index));
  }
  
  public synchronized void removeRange(int fromIndex, int toIndex)
  {
    while (toIndex > fromIndex) {
      remove(--toIndex);
    }
  }
  
  public synchronized boolean retainAll(Collection<?> c)
  {
    if (!(c instanceof Set)) {
      c = new HashSet(c);
    }
    LinkedList<E> l = new LinkedList();
    for (E o : this) {
      if (!c.contains(o)) {
        l.addLast(o);
      }
    }
    if (!l.isEmpty())
    {
      removeAll(l);
      return true;
    }
    return false;
  }
  
  public synchronized E set(int index, E o)
  {
    E orig = get(index);
    if (set.add(o))
    {
      elementData[index] = o;
      set.remove(orig);
    }
    else
    {
      int oldIndexOfO = indexOf(o);
      remove(o);
      remove(orig);
      add(oldIndexOfO > index ? index : index - 1, o);
    }
    return orig;
  }
  
  public void setElementAt(E o, int index)
  {
    set(index, o);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.VectorSet
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */