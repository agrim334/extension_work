package org.codehaus.plexus.util;

import java.util.AbstractSet;
import java.util.Iterator;

class FastMap$KeySet
  extends AbstractSet
{
  private FastMap$KeySet(FastMap paramFastMap) {}
  
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
        return FastMap.EntryImpl.access$000(before);
      }
    };
  }
  
  public int size()
  {
    return FastMap.access$1000(this$0);
  }
  
  public boolean contains(Object obj)
  {
    return this$0.containsKey(obj);
  }
  
  public boolean remove(Object obj)
  {
    return this$0.remove(obj) != null;
  }
  
  public void clear()
  {
    this$0.clear();
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.FastMap.KeySet
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */