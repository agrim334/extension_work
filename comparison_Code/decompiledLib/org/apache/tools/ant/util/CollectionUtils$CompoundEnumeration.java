package org.apache.tools.ant.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

final class CollectionUtils$CompoundEnumeration<E>
  implements Enumeration<E>
{
  private final Enumeration<E> e1;
  private final Enumeration<E> e2;
  
  public CollectionUtils$CompoundEnumeration(Enumeration<E> e1, Enumeration<E> e2)
  {
    this.e1 = e1;
    this.e2 = e2;
  }
  
  public boolean hasMoreElements()
  {
    return (e1.hasMoreElements()) || (e2.hasMoreElements());
  }
  
  public E nextElement()
    throws NoSuchElementException
  {
    if (e1.hasMoreElements()) {
      return (E)e1.nextElement();
    }
    return (E)e2.nextElement();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.CollectionUtils.CompoundEnumeration
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */