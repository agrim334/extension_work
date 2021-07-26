package org.apache.tools.ant.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

@Deprecated
public final class CollectionUtils$EmptyEnumeration<E>
  implements Enumeration<E>
{
  public boolean hasMoreElements()
  {
    return false;
  }
  
  public E nextElement()
    throws NoSuchElementException
  {
    throw new NoSuchElementException();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.CollectionUtils.EmptyEnumeration
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */