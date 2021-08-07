package org.codehaus.plexus.util;

import java.util.AbstractCollection;
import java.util.Iterator;

class FastMap$Values
  extends AbstractCollection
{
  private FastMap$Values(FastMap paramFastMap) {}
  
  public Iterator iterator()
  {
    new Iterator()
    {
      FastMap.EntryImpl after = FastMap.access$800(this$0);
      FastMap.EntryImpl before;
      
      public void remove()
      {
        FastMap.access$900(this$0, before);
      }
      
      public boolean hasNext()
      {
        return after != null;
      }
      
      public Object next()
      {
        before = after;
        after = FastMap.EntryImpl.access$300(after);
        return FastMap.EntryImpl.access$200(before);
      }
    };
  }
  
  public int size()
  {
    return FastMap.access$1000(this$0);
  }
  
  public boolean contains(Object o)
  {
    return this$0.containsValue(o);
  }
  
  public void clear()
  {
    this$0.clear();
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.FastMap.Values
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */