package org.apache.tools.ant.util;

import java.util.Enumeration;
import java.util.Iterator;

class CollectionUtils$2
  implements Iterator<E>
{
  public boolean hasNext()
  {
    return val$e.hasMoreElements();
  }
  
  public E next()
  {
    return (E)val$e.nextElement();
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.CollectionUtils.2
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */