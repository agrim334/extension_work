package org.apache.tools.ant.types.resources.comparators;

import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Resource;

public class DelegatedResourceComparator
  extends ResourceComparator
{
  private List<ResourceComparator> resourceComparators = null;
  
  public synchronized void add(ResourceComparator c)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    if (c == null) {
      return;
    }
    resourceComparators = (resourceComparators == null ? new Vector() : resourceComparators);
    resourceComparators.add(c);
    setChecked(false);
  }
  
  public synchronized boolean equals(Object o)
  {
    if (o == this) {
      return true;
    }
    if (isReference()) {
      return ((DelegatedResourceComparator)getCheckedRef(DelegatedResourceComparator.class)).equals(o);
    }
    if ((o instanceof DelegatedResourceComparator))
    {
      List<ResourceComparator> ov = resourceComparators;
      return resourceComparators == null ? false : ov == null ? true : resourceComparators.equals(ov);
    }
    return false;
  }
  
  public synchronized int hashCode()
  {
    if (isReference()) {
      return ((DelegatedResourceComparator)getCheckedRef(DelegatedResourceComparator.class)).hashCode();
    }
    return resourceComparators == null ? 0 : resourceComparators.hashCode();
  }
  
  protected synchronized int resourceCompare(Resource foo, Resource bar)
  {
    return composite(resourceComparators).compare(foo, bar);
  }
  
  protected void dieOnCircularReference(Stack<Object> stk, Project p)
    throws BuildException
  {
    if (isChecked()) {
      return;
    }
    if (isReference())
    {
      super.dieOnCircularReference(stk, p);
    }
    else
    {
      if ((resourceComparators != null) && (!resourceComparators.isEmpty())) {
        for (ResourceComparator resourceComparator : resourceComparators) {
          if ((resourceComparator instanceof DataType)) {
            pushAndInvokeCircularReferenceCheck(resourceComparator, stk, p);
          }
        }
      }
      setChecked(true);
    }
  }
  
  private static Comparator<Resource> composite(List<? extends Comparator<Resource>> foo)
  {
    Comparator<Resource> result = null;
    if (foo != null) {
      for (Comparator<Resource> comparator : foo) {
        if (result == null) {
          result = comparator;
        } else {
          result = result.thenComparing(comparator);
        }
      }
    }
    return result == null ? Comparator.naturalOrder() : result;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.comparators.DelegatedResourceComparator
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */