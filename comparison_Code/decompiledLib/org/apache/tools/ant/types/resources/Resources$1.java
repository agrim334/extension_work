package org.apache.tools.ant.types.resources;

import java.util.Iterator;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

class Resources$1
  implements ResourceCollection
{
  public boolean isFilesystemOnly()
  {
    return true;
  }
  
  public Iterator<Resource> iterator()
  {
    return Resources.EMPTY_ITERATOR;
  }
  
  public int size()
  {
    return 0;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.Resources.1
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */