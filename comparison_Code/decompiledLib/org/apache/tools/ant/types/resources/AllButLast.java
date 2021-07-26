package org.apache.tools.ant.types.resources;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

public class AllButLast
  extends SizeLimitCollection
{
  protected Collection<Resource> getCollection()
  {
    int ct = getValidCount();
    ResourceCollection nested = getResourceCollection();
    if (ct > nested.size()) {
      return Collections.emptyList();
    }
    return 
      (Collection)nested.stream().limit(nested.size() - ct).collect(Collectors.toList());
  }
  
  public synchronized int size()
  {
    return Math.max(getResourceCollection().size() - getValidCount(), 0);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.AllButLast
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */