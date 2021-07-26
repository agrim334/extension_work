package org.apache.tools.ant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class IntrospectionHelper$14
  extends IntrospectionHelper.NestedCreator
{
  IntrospectionHelper$14(IntrospectionHelper this$0, Method m, Object paramObject1, Object paramObject2)
  {
    super(m);
  }
  
  Object create(Project project, Object parent, Object ignore)
    throws InvocationTargetException, IllegalAccessException
  {
    if (!getMethod().getName().endsWith("Configured")) {
      getMethod().invoke(parent, new Object[] { val$realObject });
    }
    return val$nestedObject;
  }
  
  Object getRealObject()
  {
    return val$realObject;
  }
  
  void store(Object parent, Object child)
    throws InvocationTargetException, IllegalAccessException, InstantiationException
  {
    if (getMethod().getName().endsWith("Configured")) {
      getMethod().invoke(parent, new Object[] { val$realObject });
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.IntrospectionHelper.14
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */