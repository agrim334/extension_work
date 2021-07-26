package org.apache.tools.ant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.tools.ant.types.EnumeratedAttribute;

class IntrospectionHelper$10
  extends IntrospectionHelper.AttributeSetter
{
  IntrospectionHelper$10(IntrospectionHelper this$0, Method m, Class type, Class paramClass1, Method paramMethod1)
  {
    super(m, type);
  }
  
  public void set(Project p, Object parent, String value)
    throws InvocationTargetException, IllegalAccessException, BuildException
  {
    try
    {
      EnumeratedAttribute ea = (EnumeratedAttribute)val$reflectedArg.newInstance();
      ea.setValue(value);
      val$m.invoke(parent, new Object[] { ea });
    }
    catch (InstantiationException ie)
    {
      throw new BuildException(ie);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.IntrospectionHelper.10
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */