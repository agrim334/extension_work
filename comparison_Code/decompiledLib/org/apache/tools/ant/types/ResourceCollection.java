package org.apache.tools.ant.types;

import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

public abstract interface ResourceCollection
  extends Iterable<Resource>
{
  public abstract int size();
  
  public abstract boolean isFilesystemOnly();
  
  public Stream<? extends Resource> stream()
  {
    Stream.Builder<Resource> b = Stream.builder();
    forEach(b);
    return b.build();
  }
  
  public boolean isEmpty()
  {
    return size() == 0;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.ResourceCollection
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */