package org.apache.tools.ant.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtils
{
  public static <T> Stream<T> enumerationAsStream(Enumeration<T> e)
  {
    StreamSupport.stream(new Spliterators.AbstractSpliterator(Long.MAX_VALUE, 16)
    {
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
    }, false);
  }
  
  public static <T> Stream<T> iteratorAsStream(Iterator<T> i)
  {
    return StreamSupport.stream(
      Spliterators.spliteratorUnknownSize(i, 16), false);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.StreamUtils
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */