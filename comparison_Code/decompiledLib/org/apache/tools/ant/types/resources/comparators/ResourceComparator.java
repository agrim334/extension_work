package org.apache.tools.ant.types.resources.comparators;

import java.util.Comparator;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Resource;

public abstract class ResourceComparator
  extends DataType
  implements Comparator<Resource>
{
  public final int compare(Resource foo, Resource bar)
  {
    dieOnCircularReference();
    ResourceComparator c = isReference() ? getRef() : this;
    return c.resourceCompare(foo, bar);
  }
  
  public boolean equals(Object o)
  {
    if (isReference()) {
      return getRef().equals(o);
    }
    return (o != null) && ((o == this) || (o.getClass().equals(getClass())));
  }
  
  public synchronized int hashCode()
  {
    if (isReference()) {
      return getRef().hashCode();
    }
    return getClass().hashCode();
  }
  
  protected abstract int resourceCompare(Resource paramResource1, Resource paramResource2);
  
  private ResourceComparator getRef()
  {
    return (ResourceComparator)getCheckedRef(ResourceComparator.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.comparators.ResourceComparator
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */