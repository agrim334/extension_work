package org.apache.tools.ant.dispatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.UnknownElement;

public class DispatchUtils
{
  public static final void execute(Object task)
    throws BuildException
  {
    String methodName = "execute";
    Dispatchable dispatchable = null;
    try
    {
      if ((task instanceof Dispatchable))
      {
        dispatchable = (Dispatchable)task;
      }
      else if ((task instanceof UnknownElement))
      {
        UnknownElement ue = (UnknownElement)task;
        Object realThing = ue.getRealThing();
        if (((realThing instanceof Dispatchable)) && ((realThing instanceof Task))) {
          dispatchable = (Dispatchable)realThing;
        }
      }
      if (dispatchable != null)
      {
        String mName = null;
        try
        {
          String name = dispatchable.getActionParameterName();
          if ((name == null) || (name.trim().isEmpty())) {
            throw new BuildException("Action Parameter Name must not be empty for Dispatchable Task.");
          }
          mName = "get" + name.trim().substring(0, 1).toUpperCase();
          if (name.length() > 1) {
            mName = mName + name.substring(1);
          }
          Class<? extends Dispatchable> c = dispatchable.getClass();
          Method actionM = c.getMethod(mName, new Class[0]);
          if (actionM != null)
          {
            Object o = actionM.invoke(dispatchable, (Object[])null);
            if (o == null) {
              throw new BuildException("Dispatchable Task attribute '" + name.trim() + "' not set or value is empty.");
            }
            methodName = o.toString().trim();
            if (methodName.isEmpty()) {
              throw new BuildException("Dispatchable Task attribute '" + name.trim() + "' not set or value is empty.");
            }
            Method executeM = dispatchable.getClass().getMethod(methodName, new Class[0]);
            if (executeM == null) {
              throw new BuildException("No public " + methodName + "() in " + dispatchable.getClass());
            }
            executeM.invoke(dispatchable, (Object[])null);
            if ((task instanceof UnknownElement)) {
              ((UnknownElement)task).setRealThing(null);
            }
          }
        }
        catch (NoSuchMethodException nsme)
        {
          throw new BuildException("No public " + mName + "() in " + task.getClass());
        }
      }
      else
      {
        Method executeM = null;
        executeM = task.getClass().getMethod(methodName, new Class[0]);
        if (executeM == null) {
          throw new BuildException("No public " + methodName + "() in " + task.getClass());
        }
        executeM.invoke(task, new Object[0]);
        if ((task instanceof UnknownElement)) {
          ((UnknownElement)task).setRealThing(null);
        }
      }
    }
    catch (InvocationTargetException ie)
    {
      Throwable t = ie.getTargetException();
      if ((t instanceof BuildException)) {
        throw ((BuildException)t);
      }
      throw new BuildException(t);
    }
    catch (NoSuchMethodException|IllegalAccessException e)
    {
      throw new BuildException(e);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.dispatch.DispatchUtils
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */