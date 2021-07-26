package org.apache.tools.ant.taskdefs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.types.ResourceCollection;

public class Javadoc$ResourceCollectionContainer
  implements Iterable<ResourceCollection>
{
  private final List<ResourceCollection> rcs = new ArrayList();
  
  public Javadoc$ResourceCollectionContainer(Javadoc this$0) {}
  
  public void add(ResourceCollection rc)
  {
    rcs.add(rc);
  }
  
  public Iterator<ResourceCollection> iterator()
  {
    return rcs.iterator();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Javadoc.ResourceCollectionContainer
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */