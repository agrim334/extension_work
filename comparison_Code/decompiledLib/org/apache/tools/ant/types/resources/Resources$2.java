package org.apache.tools.ant.types.resources;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.tools.ant.types.Resource;

class Resources$2
  implements Iterator<Resource>
{
  public Resource next()
  {
    throw new NoSuchElementException();
  }
  
  public boolean hasNext()
  {
    return false;
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.Resources.2
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */