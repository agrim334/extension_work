package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.ResourceCollection;

public abstract class SizeLimitCollection
  extends BaseResourceCollectionWrapper
{
  private static final String BAD_COUNT = "size-limited collection count should be set to an int >= 0";
  private int count = 1;
  
  public synchronized void setCount(int i)
  {
    checkAttributesAllowed();
    count = i;
  }
  
  public synchronized int getCount()
  {
    return count;
  }
  
  public synchronized int size()
  {
    return Math.min(getResourceCollection().size(), getValidCount());
  }
  
  protected int getValidCount()
  {
    int ct = getCount();
    if (ct < 0) {
      throw new BuildException("size-limited collection count should be set to an int >= 0");
    }
    return ct;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.SizeLimitCollection
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */