package org.codehaus.plexus.util;

import java.util.Iterator;

class FastMap$KeySet$1
  implements Iterator
{
  FastMap.EntryImpl after = FastMap.access$800(this$1.this$0);
  FastMap.EntryImpl before;
  
  FastMap$KeySet$1(FastMap.KeySet paramKeySet) {}
  
  public void remove()
  {
    FastMap.access$900(this$1.this$0, before);
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
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.FastMap.KeySet.1
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */