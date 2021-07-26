package org.apache.tools.ant.util;

import java.lang.ref.WeakReference;

@Deprecated
public class WeakishReference
{
  private WeakReference<Object> weakref;
  
  WeakishReference(Object reference)
  {
    weakref = new WeakReference(reference);
  }
  
  public Object get()
  {
    return weakref.get();
  }
  
  public static WeakishReference createReference(Object object)
  {
    return new WeakishReference(object);
  }
  
  /**
   * @deprecated
   */
  public static class HardReference
    extends WeakishReference
  {
    public HardReference(Object object)
    {
      super();
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.WeakishReference
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */