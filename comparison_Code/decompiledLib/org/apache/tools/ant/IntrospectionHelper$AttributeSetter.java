package org.apache.tools.ant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

abstract class IntrospectionHelper$AttributeSetter
{
  private final Method method;
  private final Class<?> type;
  
  protected IntrospectionHelper$AttributeSetter(Method m, Class<?> type)
  {
    method = m;
    this.type = type;
  }
  
  void setObject(Project p, Object parent, Object value)
    throws InvocationTargetException, IllegalAccessException, BuildException
  {
    if (type != null)
    {
      Class<?> useType = type;
      if (type.isPrimitive())
      {
        if (value == null) {
          throw new BuildException("Attempt to set primitive " + IntrospectionHelper.access$500(method.getName(), "set") + " to null on " + parent);
        }
        useType = (Class)IntrospectionHelper.access$600().get(type);
      }
      if ((value == null) || (useType.isInstance(value)))
      {
        method.invoke(parent, new Object[] { value });
        return;
      }
    }
    set(p, parent, value.toString());
  }
  
  abstract void set(Project paramProject, Object paramObject, String paramString)
    throws InvocationTargetException, IllegalAccessException, BuildException;
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.IntrospectionHelper.AttributeSetter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */