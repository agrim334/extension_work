package org.codehaus.plexus.util.dag;

import java.util.Iterator;
import java.util.List;

public class CycleDetectedException
  extends Exception
{
  private List<String> cycle;
  
  public CycleDetectedException(String message, List<String> cycle)
  {
    super(message);
    
    this.cycle = cycle;
  }
  
  public List<String> getCycle()
  {
    return cycle;
  }
  
  public String cycleToString()
  {
    StringBuilder buffer = new StringBuilder();
    for (Iterator<String> iterator = cycle.iterator(); iterator.hasNext();)
    {
      buffer.append((String)iterator.next());
      if (iterator.hasNext()) {
        buffer.append(" --> ");
      }
    }
    return buffer.toString();
  }
  
  public String getMessage()
  {
    return super.getMessage() + " " + cycleToString();
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.dag.CycleDetectedException
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */