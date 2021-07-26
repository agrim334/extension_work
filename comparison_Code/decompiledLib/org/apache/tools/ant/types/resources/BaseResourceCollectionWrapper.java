package org.apache.tools.ant.types.resources;

import java.util.Collection;
import java.util.Iterator;
import org.apache.tools.ant.types.Resource;

public abstract class BaseResourceCollectionWrapper
  extends AbstractResourceCollectionWrapper
{
  private Collection<Resource> coll = null;
  
  protected Iterator<Resource> createIterator()
  {
    return cacheCollection().iterator();
  }
  
  protected int getSize()
  {
    return cacheCollection().size();
  }
  
  protected abstract Collection<Resource> getCollection();
  
  private synchronized Collection<Resource> cacheCollection()
  {
    if ((coll == null) || (!isCache())) {
      coll = getCollection();
    }
    return coll;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.BaseResourceCollectionWrapper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */