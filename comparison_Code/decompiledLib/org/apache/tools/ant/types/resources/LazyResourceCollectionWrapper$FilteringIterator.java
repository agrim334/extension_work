package org.apache.tools.ant.types.resources;

import java.util.Iterator;
import org.apache.tools.ant.types.Resource;

class LazyResourceCollectionWrapper$FilteringIterator
  implements Iterator<Resource>
{
  Resource next = null;
  boolean ended = false;
  protected final Iterator<Resource> it;
  
  LazyResourceCollectionWrapper$FilteringIterator(Iterator<Resource> arg1)
  {
    this.it = it;
  }
  
  public boolean hasNext()
  {
    if (ended) {
      return false;
    }
    while (next == null)
    {
      if (!it.hasNext())
      {
        ended = true;
        return false;
      }
      next = ((Resource)it.next());
      if (this$0.filterResource(next)) {
        next = null;
      }
    }
    return true;
  }
  
  public Resource next()
  {
    if (!hasNext()) {
      throw new UnsupportedOperationException();
    }
    Resource r = next;
    next = null;
    return r;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.LazyResourceCollectionWrapper.FilteringIterator
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */