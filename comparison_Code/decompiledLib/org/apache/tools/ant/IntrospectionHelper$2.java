package org.apache.tools.ant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class IntrospectionHelper$2
  extends IntrospectionHelper.AttributeSetter
{
  IntrospectionHelper$2(IntrospectionHelper this$0, Method m, Class type)
  {
    super(m, type);
  }
  
  public void set(Project p, Object parent, String value)
    throws InvocationTargetException, IllegalAccessException
  {
    throw new BuildException("Internal ant problem - this should not get called");
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.IntrospectionHelper.2
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */