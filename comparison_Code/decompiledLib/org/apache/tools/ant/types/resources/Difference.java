package org.apache.tools.ant.types.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

public class Difference
  extends BaseResourceCollectionContainer
{
  protected Collection<Resource> getCollection()
  {
    List<ResourceCollection> rcs = getResourceCollections();
    int size = rcs.size();
    if (size < 2) {
      throw new BuildException("The difference of %d resource %s is undefined.", new Object[] {Integer.valueOf(size), size == 1 ? "collection" : "collections" });
    }
    Set<Resource> hs = new HashSet();
    List<Resource> al = new ArrayList();
    for (ResourceCollection rc : rcs) {
      for (Resource r : rc) {
        if (hs.add(r)) {
          al.add(r);
        } else {
          al.remove(r);
        }
      }
    }
    return al;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.Difference
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */