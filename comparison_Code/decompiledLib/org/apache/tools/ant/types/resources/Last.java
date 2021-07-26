package org.apache.tools.ant.types.resources;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

public class Last
  extends SizeLimitCollection
{
  protected Collection<Resource> getCollection()
  {
    int count = getValidCount();
    ResourceCollection rc = getResourceCollection();
    int size = rc.size();
    int skip = Math.max(0, size - count);
    
    List<Resource> result = (List)rc.stream().skip(skip).collect(Collectors.toList());
    
    int found = result.size();
    if ((found == count) || ((size < count) && (found == size))) {
      return result;
    }
    String msg = String.format("Resource collection %s reports size %d but returns %d elements.", new Object[] { rc, 
    
      Integer.valueOf(size), Integer.valueOf(found + skip) });
    if (found > count)
    {
      log(msg, 1);
      return result.subList(found - count, found);
    }
    throw new BuildException(msg);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.Last
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */