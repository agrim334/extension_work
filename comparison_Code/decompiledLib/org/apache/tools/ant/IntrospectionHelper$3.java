package org.apache.tools.ant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class IntrospectionHelper$3
  extends IntrospectionHelper.AttributeSetter
{
  IntrospectionHelper$3(IntrospectionHelper this$0, Method m, Class type, Method paramMethod1)
  {
    super(m, type);
  }
  
  public void set(Project p, Object parent, String value)
    throws InvocationTargetException, IllegalAccessException
  {
    val$m.invoke(parent, (Object[])new String[] { value });
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.IntrospectionHelper.3
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */