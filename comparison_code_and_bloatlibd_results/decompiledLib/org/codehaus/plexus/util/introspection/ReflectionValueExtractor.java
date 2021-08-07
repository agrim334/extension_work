package org.codehaus.plexus.util.introspection;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.codehaus.plexus.util.StringUtils;

public class ReflectionValueExtractor
{
  private static final Class<?>[] CLASS_ARGS = new Class[0];
  private static final Object[] OBJECT_ARGS = new Object[0];
  private static final Map<Class<?>, WeakReference<ClassMap>> classMaps = new WeakHashMap();
  static final int EOF = -1;
  static final char PROPERTY_START = '.';
  static final char INDEXED_START = '[';
  static final char INDEXED_END = ']';
  static final char MAPPED_START = '(';
  static final char MAPPED_END = ')';
  
  static class Tokenizer
  {
    final String expression;
    int idx;
    
    public Tokenizer(String expression)
    {
      this.expression = expression;
    }
    
    public int peekChar()
    {
      return idx < expression.length() ? expression.charAt(idx) : -1;
    }
    
    public int skipChar()
    {
      return idx < expression.length() ? expression.charAt(idx++) : -1;
    }
    
    public String nextToken(char delimiter)
    {
      int start = idx;
      while ((idx < expression.length()) && (delimiter != expression.charAt(idx))) {
        idx += 1;
      }
      if ((idx <= start) || (idx >= expression.length())) {
        return null;
      }
      return expression.substring(start, idx++);
    }
    
    public String nextPropertyName()
    {
      int start = idx;
      while ((idx < expression.length()) && (Character.isJavaIdentifierPart(expression.charAt(idx)))) {
        idx += 1;
      }
      if ((idx <= start) || (idx > expression.length())) {
        return null;
      }
      return expression.substring(start, idx);
    }
    
    public int getPosition()
    {
      return idx < expression.length() ? idx : -1;
    }
    
    public String toString()
    {
      return idx < expression.length() ? expression.substring(idx) : "<EOF>";
    }
  }
  
  public static Object evaluate(String expression, Object root)
    throws Exception
  {
    return evaluate(expression, root, true);
  }
  
  public static Object evaluate(String expression, Object root, boolean trimRootToken)
    throws Exception
  {
    Object value = root;
    if ((StringUtils.isEmpty(expression)) || (!Character.isJavaIdentifierStart(expression.charAt(0)))) {
      return null;
    }
    boolean hasDots = expression.indexOf('.') >= 0;
    Tokenizer tokenizer;
    if ((trimRootToken) && (hasDots))
    {
      Tokenizer tokenizer = new Tokenizer(expression);
      tokenizer.nextPropertyName();
      if (tokenizer.getPosition() == -1) {
        return null;
      }
    }
    else
    {
      tokenizer = new Tokenizer("." + expression);
    }
    int propertyPosition = tokenizer.getPosition();
    while ((value != null) && (tokenizer.peekChar() != -1)) {
      switch (tokenizer.skipChar())
      {
      case 91: 
        value = getIndexedValue(expression, propertyPosition, tokenizer.getPosition(), value, tokenizer.nextToken(']'));
        
        break;
      case 40: 
        value = getMappedValue(expression, propertyPosition, tokenizer.getPosition(), value, tokenizer.nextToken(')'));
        
        break;
      case 46: 
        propertyPosition = tokenizer.getPosition();
        value = getPropertyValue(value, tokenizer.nextPropertyName());
        break;
      default: 
        return null;
      }
    }
    return value;
  }
  
  private static Object getMappedValue(String expression, int from, int to, Object value, String key)
    throws Exception
  {
    if ((value == null) || (key == null)) {
      return null;
    }
    if ((value instanceof Map))
    {
      Object[] localParams = { key };
      ClassMap classMap = getClassMap(value.getClass());
      Method method = classMap.findMethod("get", localParams);
      return method.invoke(value, localParams);
    }
    String message = String.format("The token '%s' at position '%d' refers to a java.util.Map, but the value seems is an instance of '%s'", new Object[] { expression.subSequence(from, to), Integer.valueOf(from), value.getClass() });
    
    throw new Exception(message);
  }
  
  private static Object getIndexedValue(String expression, int from, int to, Object value, String indexStr)
    throws Exception
  {
    try
    {
      int index = Integer.parseInt(indexStr);
      if (value.getClass().isArray()) {
        return Array.get(value, index);
      }
      if ((value instanceof List))
      {
        ClassMap classMap = getClassMap(value.getClass());
        
        Object[] localParams = { Integer.valueOf(index) };
        Method method = classMap.findMethod("get", localParams);
        return method.invoke(value, localParams);
      }
    }
    catch (NumberFormatException e)
    {
      return null;
    }
    catch (InvocationTargetException e)
    {
      if ((e.getCause() instanceof IndexOutOfBoundsException)) {
        return null;
      }
      throw e;
    }
    String message = String.format("The token '%s' at position '%d' refers to a java.util.List or an array, but the value seems is an instance of '%s'", new Object[] { expression.subSequence(from, to), Integer.valueOf(from), value.getClass() });
    
    throw new Exception(message);
  }
  
  private static Object getPropertyValue(Object value, String property)
    throws Exception
  {
    if ((value == null) || (property == null)) {
      return null;
    }
    ClassMap classMap = getClassMap(value.getClass());
    String methodBase = StringUtils.capitalizeFirstLetter(property);
    String methodName = "get" + methodBase;
    Method method = classMap.findMethod(methodName, CLASS_ARGS);
    if (method == null)
    {
      methodName = "is" + methodBase;
      
      method = classMap.findMethod(methodName, CLASS_ARGS);
    }
    if (method == null) {
      return null;
    }
    try
    {
      return method.invoke(value, OBJECT_ARGS);
    }
    catch (InvocationTargetException e)
    {
      throw e;
    }
  }
  
  private static ClassMap getClassMap(Class<?> clazz)
  {
    WeakReference<ClassMap> softRef = (WeakReference)classMaps.get(clazz);
    ClassMap classMap;
    ClassMap classMap;
    if ((softRef == null) || ((classMap = (ClassMap)softRef.get()) == null))
    {
      classMap = new ClassMap(clazz);
      
      classMaps.put(clazz, new WeakReference(classMap));
    }
    return classMap;
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.introspection.ReflectionValueExtractor
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */