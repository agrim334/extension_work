package org.apache.tools.ant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class IntrospectionHelper$13
  extends IntrospectionHelper.AttributeSetter
{
  IntrospectionHelper$13(IntrospectionHelper this$0, Method m, Class type, Class paramClass1, Method paramMethod1)
  {
    super(m, type);
  }
  
  public void set(Project p, Object parent, String value)
    throws InvocationTargetException, IllegalAccessException, BuildException
  {
    try
    {
      Enum<?> enumValue = Enum.valueOf(val$reflectedArg, value);
      
      setValue = enumValue;
    }
    catch (IllegalArgumentException e)
    {
      Enum<?> setValue;
      throw new BuildException("'" + value + "' is not a permitted value for " + val$reflectedArg.getName());
    }
    Enum<?> setValue;
    val$m.invoke(parent, new Object[] { setValue });
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.IntrospectionHelper.13
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */