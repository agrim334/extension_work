package org.codehaus.plexus.util.introspection;

import java.lang.reflect.Method;

final class ClassMap$MethodInfo
{
  Method method;
  String name;
  Class[] parameterTypes;
  boolean upcast;
  
  ClassMap$MethodInfo(Method method)
  {
    this.method = null;
    name = method.getName();
    parameterTypes = method.getParameterTypes();
    upcast = false;
  }
  
  void tryUpcasting(Class clazz)
    throws NoSuchMethodException
  {
    method = clazz.getMethod(name, parameterTypes);
    name = null;
    parameterTypes = null;
    upcast = true;
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.introspection.ClassMap.MethodInfo
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */