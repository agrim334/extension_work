package org.apache.tools.ant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

abstract class IntrospectionHelper$NestedCreator
{
  private final Method method;
  
  protected IntrospectionHelper$NestedCreator(Method m)
  {
    method = m;
  }
  
  Method getMethod()
  {
    return method;
  }
  
  boolean isPolyMorphic()
  {
    return false;
  }
  
  Object getRealObject()
  {
    return null;
  }
  
  abstract Object create(Project paramProject, Object paramObject1, Object paramObject2)
    throws InvocationTargetException, IllegalAccessException, InstantiationException;
  
  void store(Object parent, Object child)
    throws InvocationTargetException, IllegalAccessException, InstantiationException
  {}
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.IntrospectionHelper.NestedCreator
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */