package org.apache.tools.ant.types.resources;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

public class First
  extends SizeLimitCollection
{
  protected Collection<Resource> getCollection()
  {
    return 
      (Collection)getResourceCollection().stream().limit(getValidCount()).collect(Collectors.toList());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.First
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */