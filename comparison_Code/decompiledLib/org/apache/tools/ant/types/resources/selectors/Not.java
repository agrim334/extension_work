package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.types.Resource;

public class Not
  implements ResourceSelector
{
  private ResourceSelector sel;
  
  public Not() {}
  
  public Not(ResourceSelector s)
  {
    add(s);
  }
  
  public void add(ResourceSelector s)
  {
    if (sel != null) {
      throw new IllegalStateException("The Not ResourceSelector accepts a single nested ResourceSelector");
    }
    sel = s;
  }
  
  public boolean isSelected(Resource r)
  {
    return !sel.isSelected(r);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.selectors.Not
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */