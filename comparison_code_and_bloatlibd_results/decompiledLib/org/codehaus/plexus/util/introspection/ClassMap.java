package org.codehaus.plexus.util.introspection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Hashtable;
import java.util.Map;

public class ClassMap
{
  private static final CacheMiss CACHE_MISS = new CacheMiss(null);
  private static final Object OBJECT = new Object();
  private final Class clazz;
  private Map<String, Object> methodCache = new Hashtable();
  private final MethodMap methodMap = new MethodMap();
  
  public ClassMap(Class clazz)
  {
    this.clazz = clazz;
    populateMethodCache();
  }
  
  Class getCachedClass()
  {
    return clazz;
  }
  
  public Method findMethod(String name, Object[] params)
    throws MethodMap.AmbiguousException
  {
    String methodKey = makeMethodKey(name, params);
    Object cacheEntry = methodCache.get(methodKey);
    if (cacheEntry == CACHE_MISS) {
      return null;
    }
    if (cacheEntry == null)
    {
      try
      {
        cacheEntry = methodMap.find(name, params);
      }
      catch (MethodMap.AmbiguousException ae)
      {
        methodCache.put(methodKey, CACHE_MISS);
        
        throw ae;
      }
      if (cacheEntry == null) {
        methodCache.put(methodKey, CACHE_MISS);
      } else {
        methodCache.put(methodKey, cacheEntry);
      }
    }
    return (Method)cacheEntry;
  }
  
  private void populateMethodCache()
  {
    Method[] methods = getAccessibleMethods(clazz);
    for (Method method : methods)
    {
      Method publicMethod = getPublicMethod(method);
      if (publicMethod != null)
      {
        methodMap.add(publicMethod);
        methodCache.put(makeMethodKey(publicMethod), publicMethod);
      }
    }
  }
  
  private String makeMethodKey(Method method)
  {
    Class[] parameterTypes = method.getParameterTypes();
    
    StringBuilder methodKey = new StringBuilder(method.getName());
    for (Class parameterType : parameterTypes) {
      if (parameterType.isPrimitive())
      {
        if (parameterType.equals(Boolean.TYPE)) {
          methodKey.append("java.lang.Boolean");
        } else if (parameterType.equals(Byte.TYPE)) {
          methodKey.append("java.lang.Byte");
        } else if (parameterType.equals(Character.TYPE)) {
          methodKey.append("java.lang.Character");
        } else if (parameterType.equals(Double.TYPE)) {
          methodKey.append("java.lang.Double");
        } else if (parameterType.equals(Float.TYPE)) {
          methodKey.append("java.lang.Float");
        } else if (parameterType.equals(Integer.TYPE)) {
          methodKey.append("java.lang.Integer");
        } else if (parameterType.equals(Long.TYPE)) {
          methodKey.append("java.lang.Long");
        } else if (parameterType.equals(Short.TYPE)) {
          methodKey.append("java.lang.Short");
        }
      }
      else {
        methodKey.append(parameterType.getName());
      }
    }
    return methodKey.toString();
  }
  
  private static String makeMethodKey(String method, Object[] params)
  {
    StringBuilder methodKey = new StringBuilder().append(method);
    for (Object param : params)
    {
      Object arg = param;
      if (arg == null) {
        arg = OBJECT;
      }
      methodKey.append(arg.getClass().getName());
    }
    return methodKey.toString();
  }
  
  private static Method[] getAccessibleMethods(Class clazz)
  {
    Method[] methods = clazz.getMethods();
    if (Modifier.isPublic(clazz.getModifiers())) {
      return methods;
    }
    MethodInfo[] methodInfos = new MethodInfo[methods.length];
    for (int i = methods.length; i-- > 0;) {
      methodInfos[i] = new MethodInfo(methods[i]);
    }
    int upcastCount = getAccessibleMethods(clazz, methodInfos, 0);
    if (upcastCount < methods.length) {
      methods = new Method[upcastCount];
    }
    int j = 0;
    for (MethodInfo methodInfo : methodInfos) {
      if (upcast) {
        methods[(j++)] = method;
      }
    }
    return methods;
  }
  
  private static int getAccessibleMethods(Class clazz, MethodInfo[] methodInfos, int upcastCount)
  {
    int l = methodInfos.length;
    if (Modifier.isPublic(clazz.getModifiers()))
    {
      for (int i = 0; (i < l) && (upcastCount < l); i++) {
        try
        {
          MethodInfo methodInfo = methodInfos[i];
          if (!upcast)
          {
            methodInfo.tryUpcasting(clazz);
            upcastCount++;
          }
        }
        catch (NoSuchMethodException localNoSuchMethodException) {}
      }
      if (upcastCount == l) {
        return upcastCount;
      }
    }
    Class superclazz = clazz.getSuperclass();
    if (superclazz != null)
    {
      upcastCount = getAccessibleMethods(superclazz, methodInfos, upcastCount);
      if (upcastCount == l) {
        return upcastCount;
      }
    }
    Class[] interfaces = clazz.getInterfaces();
    for (int i = interfaces.length; i-- > 0;)
    {
      upcastCount = getAccessibleMethods(interfaces[i], methodInfos, upcastCount);
      if (upcastCount == l) {
        return upcastCount;
      }
    }
    return upcastCount;
  }
  
  public static Method getPublicMethod(Method method)
  {
    Class clazz = method.getDeclaringClass();
    if ((clazz.getModifiers() & 0x1) != 0) {
      return method;
    }
    return getPublicMethod(clazz, method.getName(), method.getParameterTypes());
  }
  
  private static Method getPublicMethod(Class clazz, String name, Class[] paramTypes)
  {
    if ((clazz.getModifiers() & 0x1) != 0) {
      try
      {
        return clazz.getMethod(name, paramTypes);
      }
      catch (NoSuchMethodException e)
      {
        return null;
      }
    }
    Class superclazz = clazz.getSuperclass();
    if (superclazz != null)
    {
      Method superclazzMethod = getPublicMethod(superclazz, name, paramTypes);
      if (superclazzMethod != null) {
        return superclazzMethod;
      }
    }
    Class[] interfaces = clazz.getInterfaces();
    for (Class anInterface : interfaces)
    {
      Method interfaceMethod = getPublicMethod(anInterface, name, paramTypes);
      if (interfaceMethod != null) {
        return interfaceMethod;
      }
    }
    return null;
  }
  
  private static final class MethodInfo
  {
    Method method;
    String name;
    Class[] parameterTypes;
    boolean upcast;
    
    MethodInfo(Method method)
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
  
  private static final class CacheMiss {}
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.introspection.ClassMap
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */