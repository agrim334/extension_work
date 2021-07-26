package org.apache.tools.ant.types.resources.selectors;

import org.apache.tools.ant.types.Comparison;
import org.apache.tools.ant.types.Resource;

public class Size
  implements ResourceSelector
{
  private long size = -1L;
  private Comparison when = Comparison.EQUAL;
  
  public void setSize(long l)
  {
    size = l;
  }
  
  public long getSize()
  {
    return size;
  }
  
  public void setWhen(Comparison c)
  {
    when = c;
  }
  
  public Comparison getWhen()
  {
    return when;
  }
  
  public boolean isSelected(Resource r)
  {
    long diff = r.getSize() - size;
    return when.evaluate(diff == 0L ? 0 : (int)(diff / Math.abs(diff)));
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.selectors.Size
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */