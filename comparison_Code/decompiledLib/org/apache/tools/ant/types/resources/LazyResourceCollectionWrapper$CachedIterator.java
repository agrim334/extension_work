package org.apache.tools.ant.types.resources;

import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.types.Resource;

class LazyResourceCollectionWrapper$CachedIterator
  implements Iterator<Resource>
{
  int cursor = 0;
  private final Iterator<Resource> it;
  
  public LazyResourceCollectionWrapper$CachedIterator(Iterator<Resource> arg1)
  {
    this.it = it;
  }
  
  public boolean hasNext()
  {
    synchronized (LazyResourceCollectionWrapper.access$000(this$0))
    {
      if (LazyResourceCollectionWrapper.access$000(this$0).size() > cursor) {
        return true;
      }
      if (!it.hasNext()) {
        return false;
      }
      Resource r = (Resource)it.next();
      LazyResourceCollectionWrapper.access$000(this$0).add(r);
    }
    return true;
  }
  
  /* Error */
  public Resource next()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 49	org/apache/tools/ant/types/resources/LazyResourceCollectionWrapper$CachedIterator:hasNext	()Z
    //   4: ifne +11 -> 15
    //   7: new 50	java/util/NoSuchElementException
    //   10: dup
    //   11: invokespecial 52	java/util/NoSuchElementException:<init>	()V
    //   14: athrow
    //   15: aload_0
    //   16: getfield 1	org/apache/tools/ant/types/resources/LazyResourceCollectionWrapper$CachedIterator:this$0	Lorg/apache/tools/ant/types/resources/LazyResourceCollectionWrapper;
    //   19: invokestatic 21	org/apache/tools/ant/types/resources/LazyResourceCollectionWrapper:access$000	(Lorg/apache/tools/ant/types/resources/LazyResourceCollectionWrapper;)Ljava/util/List;
    //   22: dup
    //   23: astore_1
    //   24: monitorenter
    //   25: aload_0
    //   26: getfield 1	org/apache/tools/ant/types/resources/LazyResourceCollectionWrapper$CachedIterator:this$0	Lorg/apache/tools/ant/types/resources/LazyResourceCollectionWrapper;
    //   29: invokestatic 21	org/apache/tools/ant/types/resources/LazyResourceCollectionWrapper:access$000	(Lorg/apache/tools/ant/types/resources/LazyResourceCollectionWrapper;)Ljava/util/List;
    //   32: aload_0
    //   33: dup
    //   34: getfield 13	org/apache/tools/ant/types/resources/LazyResourceCollectionWrapper$CachedIterator:cursor	I
    //   37: dup_x1
    //   38: iconst_1
    //   39: iadd
    //   40: putfield 13	org/apache/tools/ant/types/resources/LazyResourceCollectionWrapper$CachedIterator:cursor	I
    //   43: invokeinterface 53 2 0
    //   48: checkcast 43	org/apache/tools/ant/types/Resource
    //   51: aload_1
    //   52: monitorexit
    //   53: areturn
    //   54: astore_2
    //   55: aload_1
    //   56: monitorexit
    //   57: aload_2
    //   58: athrow
    // Line number table:
    //   Java source line #163	-> byte code offset #0
    //   Java source line #164	-> byte code offset #7
    //   Java source line #166	-> byte code offset #15
    //   Java source line #169	-> byte code offset #25
    //   Java source line #170	-> byte code offset #54
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	59	0	this	CachedIterator
    //   23	33	1	Ljava/lang/Object;	Object
    //   54	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   25	53	54	finally
    //   54	57	54	finally
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.resources.LazyResourceCollectionWrapper.CachedIterator
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */