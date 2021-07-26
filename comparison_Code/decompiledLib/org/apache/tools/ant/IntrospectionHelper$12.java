package org.apache.tools.ant;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class IntrospectionHelper$12
  extends IntrospectionHelper.AttributeSetter
{
  IntrospectionHelper$12(IntrospectionHelper this$0, Method m, Class type, boolean paramBoolean, Constructor paramConstructor, Method paramMethod1, String paramString)
  {
    super(m, type);
  }
  
  public void set(Project p, Object parent, String value)
    throws InvocationTargetException, IllegalAccessException, BuildException
  {
    try
    {
      Object[] args = { val$finalIncludeProject ? new Object[] { p, value } : value };
      
      Object attribute = val$finalConstructor.newInstance(args);
      if (p != null) {
        p.setProjectReference(attribute);
      }
      val$m.invoke(parent, new Object[] { attribute });
    }
    catch (InvocationTargetException e)
    {
      Throwable cause = e.getCause();
      if ((cause instanceof IllegalArgumentException)) {
        throw new BuildException("Can't assign value '" + value + "' to attribute " + val$attrName + ", reason: " + cause.getClass() + " with message '" + cause.getMessage() + "'");
      }
      throw e;
    }
    catch (InstantiationException ie)
    {
      throw new BuildException(ie);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.IntrospectionHelper.12
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */