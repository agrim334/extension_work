package org.codehaus.plexus.util.introspection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MethodMap
{
  private static final int MORE_SPECIFIC = 0;
  private static final int LESS_SPECIFIC = 1;
  private static final int INCOMPARABLE = 2;
  Map<String, List<Method>> methodByNameMap;
  
  public MethodMap()
  {
    methodByNameMap = new Hashtable();
  }
  
  public void add(Method method)
  {
    String methodName = method.getName();
    
    List<Method> l = get(methodName);
    if (l == null)
    {
      l = new ArrayList();
      methodByNameMap.put(methodName, l);
    }
    l.add(method);
  }
  
  public List<Method> get(String key)
  {
    return (List)methodByNameMap.get(key);
  }
  
  public Method find(String methodName, Object[] args)
    throws MethodMap.AmbiguousException
  {
    List<Method> methodList = get(methodName);
    if (methodList == null) {
      return null;
    }
    int l = args.length;
    Class[] classes = new Class[l];
    for (int i = 0; i < l; i++)
    {
      Object arg = args[i];
      
      classes[i] = (arg == null ? null : arg.getClass());
    }
    return getMostSpecific(methodList, classes);
  }
  
  private static Method getMostSpecific(List<Method> methods, Class[] classes)
    throws MethodMap.AmbiguousException
  {
    LinkedList<Method> applicables = getApplicables(methods, classes);
    if (applicables.isEmpty()) {
      return null;
    }
    if (applicables.size() == 1) {
      return (Method)applicables.getFirst();
    }
    LinkedList<Method> maximals = new LinkedList();
    for (Method app : applicables)
    {
      Class[] appArgs = app.getParameterTypes();
      boolean lessSpecific = false;
      for (Iterator<Method> maximal = maximals.iterator(); (!lessSpecific) && (maximal.hasNext());)
      {
        Method max = (Method)maximal.next();
        switch (moreSpecific(appArgs, max.getParameterTypes()))
        {
        case 0: 
          maximal.remove();
          break;
        case 1: 
          lessSpecific = true;
        }
      }
      if (!lessSpecific) {
        maximals.addLast(app);
      }
    }
    if (maximals.size() > 1) {
      throw new AmbiguousException();
    }
    return (Method)maximals.getFirst();
  }
  
  private static int moreSpecific(Class[] c1, Class[] c2)
  {
    boolean c1MoreSpecific = false;
    boolean c2MoreSpecific = false;
    for (int i = 0; i < c1.length; i++) {
      if (c1[i] != c2[i])
      {
        c1MoreSpecific = (c1MoreSpecific) || (isStrictMethodInvocationConvertible(c2[i], c1[i]));
        c2MoreSpecific = (c2MoreSpecific) || (isStrictMethodInvocationConvertible(c1[i], c2[i]));
      }
    }
    if (c1MoreSpecific)
    {
      if (c2MoreSpecific) {
        return 2;
      }
      return 0;
    }
    if (c2MoreSpecific) {
      return 1;
    }
    return 2;
  }
  
  private static LinkedList<Method> getApplicables(List<Method> methods, Class[] classes)
  {
    LinkedList<Method> list = new LinkedList();
    for (Object method1 : methods)
    {
      Method method = (Method)method1;
      if (isApplicable(method, classes)) {
        list.add(method);
      }
    }
    return list;
  }
  
  private static boolean isApplicable(Method method, Class[] classes)
  {
    Class[] methodArgs = method.getParameterTypes();
    if (methodArgs.length != classes.length) {
      return false;
    }
    for (int i = 0; i < classes.length; i++) {
      if (!isMethodInvocationConvertible(methodArgs[i], classes[i])) {
        return false;
      }
    }
    return true;
  }
  
  private static boolean isMethodInvocationConvertible(Class formal, Class actual)
  {
    if ((actual == null) && (!formal.isPrimitive())) {
      return true;
    }
    if ((actual != null) && (formal.isAssignableFrom(actual))) {
      return true;
    }
    if (formal.isPrimitive())
    {
      if ((formal == Boolean.TYPE) && (actual == Boolean.class)) {
        return true;
      }
      if ((formal == Character.TYPE) && (actual == Character.class)) {
        return true;
      }
      if ((formal == Byte.TYPE) && (actual == Byte.class)) {
        return true;
      }
      if ((formal == Short.TYPE) && ((actual == Short.class) || (actual == Byte.class))) {
        return true;
      }
      if ((formal == Integer.TYPE) && ((actual == Integer.class) || (actual == Short.class) || (actual == Byte.class))) {
        return true;
      }
      if ((formal == Long.TYPE) && ((actual == Long.class) || (actual == Integer.class) || (actual == Short.class) || (actual == Byte.class))) {
        return true;
      }
      if ((formal == Float.TYPE) && ((actual == Float.class) || (actual == Long.class) || (actual == Integer.class) || (actual == Short.class) || (actual == Byte.class))) {
        return true;
      }
      if ((formal == Double.TYPE) && ((actual == Double.class) || (actual == Float.class) || (actual == Long.class) || (actual == Integer.class) || (actual == Short.class) || (actual == Byte.class))) {
        return true;
      }
    }
    return false;
  }
  
  private static boolean isStrictMethodInvocationConvertible(Class formal, Class actual)
  {
    if ((actual == null) && (!formal.isPrimitive())) {
      return true;
    }
    if (formal.isAssignableFrom(actual)) {
      return true;
    }
    if (formal.isPrimitive())
    {
      if ((formal == Short.TYPE) && (actual == Byte.TYPE)) {
        return true;
      }
      if ((formal == Integer.TYPE) && ((actual == Short.TYPE) || (actual == Byte.TYPE))) {
        return true;
      }
      if ((formal == Long.TYPE) && ((actual == Integer.TYPE) || (actual == Short.TYPE) || (actual == Byte.TYPE))) {
        return true;
      }
      if ((formal == Float.TYPE) && ((actual == Long.TYPE) || (actual == Integer.TYPE) || (actual == Short.TYPE) || (actual == Byte.TYPE))) {
        return true;
      }
      if ((formal == Double.TYPE) && ((actual == Float.TYPE) || (actual == Long.TYPE) || (actual == Integer.TYPE) || (actual == Short.TYPE) || (actual == Byte.TYPE))) {
        return true;
      }
    }
    return false;
  }
  
  public static class AmbiguousException
    extends Exception
  {}
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.introspection.MethodMap
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */