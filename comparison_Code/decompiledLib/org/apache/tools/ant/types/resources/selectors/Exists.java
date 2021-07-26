package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.types.Resource;

public class Exists
  implements ResourceSelector
{
  public boolean isSelected(Resource r)
  {
    return r.isExists();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.selectors.Exists
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */