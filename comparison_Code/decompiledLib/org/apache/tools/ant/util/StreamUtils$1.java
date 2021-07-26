package org.apache.tools.ant.util;

import java.util.Enumeration;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;

class StreamUtils$1
  extends Spliterators.AbstractSpliterator<T>
{
  StreamUtils$1(long arg0, int arg1, Enumeration paramEnumeration)
  {
    super(arg0, arg1);
  }
  
  public boolean tryAdvance(Consumer<? super T> action)
  {
    if (val$e.hasMoreElements())
    {
      action.accept(val$e.nextElement());
      return true;
    }
    return false;
  }
  
  public void forEachRemaining(Consumer<? super T> action)
  {
    while (val$e.hasMoreElements()) {
      action.accept(val$e.nextElement());
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.StreamUtils.1
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */