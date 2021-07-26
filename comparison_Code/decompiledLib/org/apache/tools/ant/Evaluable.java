package org.apache.tools.ant;

import java.util.function.Supplier;

public abstract interface Evaluable<T>
  extends Supplier<T>
{
  public abstract T eval();
  
  public T get()
  {
    return (T)eval();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.Evaluable
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */