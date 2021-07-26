package org.apache.tools.ant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.tools.ant.types.resources.FileResource;

class IntrospectionHelper$9
  extends IntrospectionHelper.AttributeSetter
{
  IntrospectionHelper$9(IntrospectionHelper this$0, Method m, Class type, Method paramMethod1)
  {
    super(m, type);
  }
  
  void set(Project p, Object parent, String value)
    throws InvocationTargetException, IllegalAccessException, BuildException
  {
    val$m.invoke(parent, new Object[] { new FileResource(p, p.resolveFile(value)) });
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.IntrospectionHelper.9
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */