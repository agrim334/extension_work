package org.codehaus.plexus.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class ExceptionUtils
{
  static final String WRAPPED_MARKER = " [wrapped] ";
  protected static String[] CAUSE_METHOD_NAMES = { "getCause", "getNextException", "getTargetException", "getException", "getSourceException", "getRootCause", "getCausedByException", "getNested" };
  
  public static void addCauseMethodName(String methodName)
  {
    if ((methodName != null) && (methodName.length() > 0))
    {
      List<String> list = new ArrayList(Arrays.asList(CAUSE_METHOD_NAMES));
      list.add(methodName);
      CAUSE_METHOD_NAMES = (String[])list.toArray(new String[list.size()]);
    }
  }
  
  public static Throwable getCause(Throwable throwable)
  {
    return getCause(throwable, CAUSE_METHOD_NAMES);
  }
  
  public static Throwable getCause(Throwable throwable, String[] methodNames)
  {
    Throwable cause = getCauseUsingWellKnownTypes(throwable);
    if (cause == null)
    {
      for (String methodName : methodNames)
      {
        cause = getCauseUsingMethodName(throwable, methodName);
        if (cause != null) {
          break;
        }
      }
      if (cause == null) {
        cause = getCauseUsingFieldName(throwable, "detail");
      }
    }
    return cause;
  }
  
  public static Throwable getRootCause(Throwable throwable)
  {
    Throwable cause = getCause(throwable);
    if (cause != null)
    {
      throwable = cause;
      while ((throwable = getCause(throwable)) != null) {
        cause = throwable;
      }
    }
    return cause;
  }
  
  private static Throwable getCauseUsingWellKnownTypes(Throwable throwable)
  {
    if ((throwable instanceof SQLException)) {
      return ((SQLException)throwable).getNextException();
    }
    if ((throwable instanceof InvocationTargetException)) {
      return ((InvocationTargetException)throwable).getTargetException();
    }
    return null;
  }
  
  private static Throwable getCauseUsingMethodName(Throwable throwable, String methodName)
  {
    Method method = null;
    try
    {
      method = throwable.getClass().getMethod(methodName, null);
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}catch (SecurityException localSecurityException) {}
    if ((method != null) && (Throwable.class.isAssignableFrom(method.getReturnType()))) {
      try
      {
        return (Throwable)method.invoke(throwable, new Object[0]);
      }
      catch (IllegalAccessException localIllegalAccessException) {}catch (IllegalArgumentException localIllegalArgumentException) {}catch (InvocationTargetException localInvocationTargetException) {}
    }
    return null;
  }
  
  private static Throwable getCauseUsingFieldName(Throwable throwable, String fieldName)
  {
    Field field = null;
    try
    {
      field = throwable.getClass().getField(fieldName);
    }
    catch (NoSuchFieldException localNoSuchFieldException) {}catch (SecurityException localSecurityException) {}
    if ((field != null) && (Throwable.class.isAssignableFrom(field.getType()))) {
      try
      {
        return (Throwable)field.get(throwable);
      }
      catch (IllegalAccessException localIllegalAccessException) {}catch (IllegalArgumentException localIllegalArgumentException) {}
    }
    return null;
  }
  
  public static int getThrowableCount(Throwable throwable)
  {
    int count = 0;
    while (throwable != null)
    {
      count++;
      throwable = getCause(throwable);
    }
    return count;
  }
  
  public static Throwable[] getThrowables(Throwable throwable)
  {
    List<Throwable> list = new ArrayList();
    while (throwable != null)
    {
      list.add(throwable);
      throwable = getCause(throwable);
    }
    return (Throwable[])list.toArray(new Throwable[list.size()]);
  }
  
  public static int indexOfThrowable(Throwable throwable, Class type)
  {
    return indexOfThrowable(throwable, type, 0);
  }
  
  public static int indexOfThrowable(Throwable throwable, Class type, int fromIndex)
  {
    if (fromIndex < 0) {
      throw new IndexOutOfBoundsException("Throwable index out of range: " + fromIndex);
    }
    Throwable[] throwables = getThrowables(throwable);
    if (fromIndex >= throwables.length) {
      throw new IndexOutOfBoundsException("Throwable index out of range: " + fromIndex);
    }
    for (int i = fromIndex; i < throwables.length; i++) {
      if (throwables[i].getClass().equals(type)) {
        return i;
      }
    }
    return -1;
  }
  
  public static void printRootCauseStackTrace(Throwable t, PrintStream stream)
  {
    String[] trace = getRootCauseStackTrace(t);
    for (String aTrace : trace) {
      stream.println(aTrace);
    }
    stream.flush();
  }
  
  public static void printRootCauseStackTrace(Throwable t)
  {
    printRootCauseStackTrace(t, System.err);
  }
  
  public static void printRootCauseStackTrace(Throwable t, PrintWriter writer)
  {
    String[] trace = getRootCauseStackTrace(t);
    for (String aTrace : trace) {
      writer.println(aTrace);
    }
    writer.flush();
  }
  
  public static String[] getRootCauseStackTrace(Throwable t)
  {
    Throwable[] throwables = getThrowables(t);
    int count = throwables.length;
    ArrayList<String> frames = new ArrayList();
    List<String> nextTrace = getStackFrameList(throwables[(count - 1)]);
    int i = count;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      List<String> trace = nextTrace;
      if (i != 0)
      {
        nextTrace = getStackFrameList(throwables[(i - 1)]);
        removeCommonFrames(trace, nextTrace);
      }
      if (i == count - 1) {
        frames.add(throwables[i].toString());
      } else {
        frames.add(" [wrapped] " + throwables[i].toString());
      }
      for (String aTrace : trace) {
        frames.add(aTrace);
      }
    }
    return (String[])frames.toArray(new String[frames.size()]);
  }
  
  private static void removeCommonFrames(List<String> causeFrames, List<String> wrapperFrames)
  {
    int causeFrameIndex = causeFrames.size() - 1;
    int wrapperFrameIndex = wrapperFrames.size() - 1;
    while ((causeFrameIndex >= 0) && (wrapperFrameIndex >= 0))
    {
      String causeFrame = (String)causeFrames.get(causeFrameIndex);
      String wrapperFrame = (String)wrapperFrames.get(wrapperFrameIndex);
      if (causeFrame.equals(wrapperFrame)) {
        causeFrames.remove(causeFrameIndex);
      }
      causeFrameIndex--;
      wrapperFrameIndex--;
    }
  }
  
  public static String getStackTrace(Throwable t)
  {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    t.printStackTrace(pw);
    return sw.getBuffer().toString();
  }
  
  public static String getFullStackTrace(Throwable t)
  {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    Throwable[] ts = getThrowables(t);
    for (Throwable t1 : ts)
    {
      t1.printStackTrace(pw);
      if (isNestedThrowable(t1)) {
        break;
      }
    }
    return sw.getBuffer().toString();
  }
  
  public static boolean isNestedThrowable(Throwable throwable)
  {
    if (throwable == null) {
      return false;
    }
    if ((throwable instanceof SQLException)) {
      return true;
    }
    if ((throwable instanceof InvocationTargetException)) {
      return true;
    }
    for (String CAUSE_METHOD_NAME : CAUSE_METHOD_NAMES) {
      try
      {
        Method method = throwable.getClass().getMethod(CAUSE_METHOD_NAME, null);
        if (method != null) {
          return true;
        }
      }
      catch (NoSuchMethodException localNoSuchMethodException) {}catch (SecurityException localSecurityException) {}
    }
    try
    {
      Field field = throwable.getClass().getField("detail");
      if (field != null) {
        return true;
      }
    }
    catch (NoSuchFieldException localNoSuchFieldException) {}catch (SecurityException localSecurityException1) {}
    return false;
  }
  
  public static String[] getStackFrames(Throwable t)
  {
    return getStackFrames(getStackTrace(t));
  }
  
  static String[] getStackFrames(String stackTrace)
  {
    String linebreak = System.getProperty("line.separator");
    StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
    List<String> list = new LinkedList();
    while (frames.hasMoreTokens()) {
      list.add(frames.nextToken());
    }
    return (String[])list.toArray(new String[list.size()]);
  }
  
  static List<String> getStackFrameList(Throwable t)
  {
    String stackTrace = getStackTrace(t);
    String linebreak = System.getProperty("line.separator");
    StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
    List<String> list = new LinkedList();
    boolean traceStarted = false;
    while (frames.hasMoreTokens())
    {
      String token = frames.nextToken();
      
      int at = token.indexOf("at");
      if ((at != -1) && (token.substring(0, at).trim().length() == 0))
      {
        traceStarted = true;
        list.add(token);
      }
      else
      {
        if (traceStarted) {
          break;
        }
      }
    }
    return list;
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.ExceptionUtils
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */