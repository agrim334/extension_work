package org.apache.tools.ant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class IntrospectionHelper$CreateNestedCreator
  extends IntrospectionHelper.NestedCreator
{
  IntrospectionHelper$CreateNestedCreator(Method m)
  {
    super(m);
  }
  
  Object create(Project project, Object parent, Object ignore)
    throws InvocationTargetException, IllegalAccessException
  {
    return getMethod().invoke(parent, new Object[0]);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.IntrospectionHelper.CreateNestedCreator
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */