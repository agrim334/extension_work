package org.apache.tools.ant.types.resources;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

public class AllButFirst
  extends SizeLimitCollection
{
  protected Collection<Resource> getCollection()
  {
    return 
      (Collection)getResourceCollection().stream().skip(getValidCount()).collect(Collectors.toList());
  }
  
  public synchronized int size()
  {
    return Math.max(getResourceCollection().size() - getValidCount(), 0);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.AllButFirst
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */