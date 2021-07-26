package org.apache.tools.ant.util;

import java.util.Enumeration;
import java.util.Iterator;

class CollectionUtils$1
  implements Enumeration<E>
{
  public boolean hasMoreElements()
  {
    return val$iter.hasNext();
  }
  
  public E nextElement()
  {
    return (E)val$iter.next();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.CollectionUtils.1
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */