package org.apache.tools.ant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class IntrospectionHelper$6
  extends IntrospectionHelper.AttributeSetter
{
  IntrospectionHelper$6(IntrospectionHelper this$0, Method m, Class type, Method paramMethod1)
  {
    super(m, type);
  }
  
  public void set(Project p, Object parent, String value)
    throws InvocationTargetException, IllegalAccessException, BuildException
  {
    try
    {
      val$m.invoke(parent, new Object[] { Class.forName(value) });
    }
    catch (ClassNotFoundException ce)
    {
      throw new BuildException(ce);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.IntrospectionHelper.6
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */