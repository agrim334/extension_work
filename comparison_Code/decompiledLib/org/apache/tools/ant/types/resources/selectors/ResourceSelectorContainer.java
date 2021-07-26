package org.apache.tools.ant.types.resources.selectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;

public class ResourceSelectorContainer
  extends DataType
{
  private final List<ResourceSelector> resourceSelectors = new ArrayList();
  
  public ResourceSelectorContainer() {}
  
  public ResourceSelectorContainer(ResourceSelector... resourceSelectors)
  {
    for (ResourceSelector rsel : resourceSelectors) {
      add(rsel);
    }
  }
  
  public void add(ResourceSelector s)
  {
    if (isReference()) {
      throw noChildrenAllowed();
    }
    if (s == null) {
      return;
    }
    resourceSelectors.add(s);
    setChecked(false);
  }
  
  public boolean hasSelectors()
  {
    if (isReference()) {
      return getRef().hasSelectors();
    }
    dieOnCircularReference();
    return !resourceSelectors.isEmpty();
  }
  
  public int selectorCount()
  {
    if (isReference()) {
      return getRef().selectorCount();
    }
    dieOnCircularReference();
    return resourceSelectors.size();
  }
  
  public Iterator<ResourceSelector> getSelectors()
  {
    if (isReference()) {
      return getRef().getSelectors();
    }
    return getResourceSelectors().iterator();
  }
  
  public List<ResourceSelector> getResourceSelectors()
  {
    if (isReference()) {
      return getRef().getResourceSelectors();
    }
    dieOnCircularReference();
    return Collections.unmodifiableList(resourceSelectors);
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
      for (ResourceSelector resourceSelector : resourceSelectors) {
        if ((resourceSelector instanceof DataType)) {
          pushAndInvokeCircularReferenceCheck((DataType)resourceSelector, stk, p);
        }
      }
      setChecked(true);
    }
  }
  
  private ResourceSelectorContainer getRef()
  {
    return (ResourceSelectorContainer)getCheckedRef(ResourceSelectorContainer.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.selectors.ResourceSelectorContainer
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */