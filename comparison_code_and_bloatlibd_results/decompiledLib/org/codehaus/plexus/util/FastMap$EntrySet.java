package org.codehaus.plexus.util;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map.Entry;

class FastMap$EntrySet
  extends AbstractSet
{
  private FastMap$EntrySet(FastMap paramFastMap) {}
  
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
        return before;
      }
    };
  }
  
  public int size()
  {
    return FastMap.access$1000(this$0);
  }
  
  public boolean contains(Object obj)
  {
    if ((obj instanceof Map.Entry))
    {
      Map.Entry entry = (Map.Entry)obj;
      Map.Entry mapEntry = this$0.getEntry(entry.getKey());
      return entry.equals(mapEntry);
    }
    return false;
  }
  
  public boolean remove(Object obj)
  {
    if ((obj instanceof Map.Entry))
    {
      Map.Entry entry = (Map.Entry)obj;
      FastMap.EntryImpl mapEntry = (FastMap.EntryImpl)this$0.getEntry(entry.getKey());
      if ((mapEntry != null) && (entry.getValue().equals(FastMap.EntryImpl.access$200(mapEntry))))
      {
        FastMap.access$900(this$0, mapEntry);
        return true;
      }
    }
    return false;
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.FastMap.EntrySet
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */