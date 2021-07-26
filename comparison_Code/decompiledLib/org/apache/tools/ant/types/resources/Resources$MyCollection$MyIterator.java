package org.apache.tools.ant.types.resources;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

class Resources$MyCollection$MyIterator
  implements Iterator<Resource>
{
  private Iterator<ResourceCollection> rci = Resources.access$200(this$1.this$0).iterator();
  private Iterator<Resource> ri = null;
  
  private Resources$MyCollection$MyIterator(Resources.MyCollection paramMyCollection) {}
  
  public boolean hasNext()
  {
    boolean result = (ri != null) && (ri.hasNext());
    while ((!result) && (rci.hasNext()))
    {
      ri = ((ResourceCollection)rci.next()).iterator();
      result = ri.hasNext();
    }
    return result;
  }
  
  public Resource next()
  {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    return (Resource)ri.next();
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.Resources.MyCollection.MyIterator
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */