package org.apache.tools.ant.util;

import java.lang.reflect.Constructor;

public class ReflectWrapper
{
  private Object obj;
  
  public ReflectWrapper(ClassLoader loader, String name)
  {
    try
    {
      Class<?> clazz = Class.forName(name, true, loader);
      Constructor<?> constructor = clazz.getConstructor(new Class[0]);
      obj = constructor.newInstance(new Object[0]);
    }
    catch (Exception t)
    {
      ReflectUtil.throwBuildException(t);
    }
  }
  
  public ReflectWrapper(Object obj)
  {
    this.obj = obj;
  }
  
  public <T> T getObject()
  {
    return (T)obj;
  }
  
  public <T> T invoke(String methodName)
  {
    return (T)ReflectUtil.invoke(obj, methodName);
  }
  
  public <T> T invoke(String methodName, Class<?> argType, Object arg)
  {
    return (T)ReflectUtil.invoke(obj, methodName, argType, arg);
  }
  
  public <T> T invoke(String methodName, Class<?> argType1, Object arg1, Class<?> argType2, Object arg2)
  {
    return (T)ReflectUtil.invoke(obj, methodName, argType1, arg1, argType2, arg2);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.ReflectWrapper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */