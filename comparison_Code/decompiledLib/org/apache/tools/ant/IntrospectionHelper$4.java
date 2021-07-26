package org.apache.tools.ant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class IntrospectionHelper$4
  extends IntrospectionHelper.AttributeSetter
{
  IntrospectionHelper$4(IntrospectionHelper this$0, Method m, Class type, String paramString, Method paramMethod1)
  {
    super(m, type);
  }
  
  public void set(Project p, Object parent, String value)
    throws InvocationTargetException, IllegalAccessException
  {
    if (value.isEmpty()) {
      throw new BuildException("The value \"\" is not a legal value for attribute \"" + val$attrName + "\"");
    }
    val$m.invoke(parent, (Object[])new Character[] { Character.valueOf(value.charAt(0)) });
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.IntrospectionHelper.4
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */