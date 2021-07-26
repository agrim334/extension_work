package org.apache.tools.ant;

import java.lang.reflect.Method;
import org.apache.tools.ant.dispatch.DispatchUtils;
import org.apache.tools.ant.dispatch.Dispatchable;

public class TaskAdapter
  extends Task
  implements TypeAdapter
{
  private Object proxy;
  
  public TaskAdapter() {}
  
  public TaskAdapter(Object proxy)
  {
    this();
    setProxy(proxy);
  }
  
  public static void checkTaskClass(Class<?> taskClass, Project project)
  {
    if (!Dispatchable.class.isAssignableFrom(taskClass)) {
      try
      {
        Method executeM = taskClass.getMethod("execute", new Class[0]);
        if (!Void.TYPE.equals(executeM.getReturnType()))
        {
          String message = "return type of execute() should be void but was \"" + executeM.getReturnType() + "\" in " + taskClass;
          
          project.log(message, 1);
        }
      }
      catch (NoSuchMethodException e)
      {
        String message = "No public execute() in " + taskClass;
        project.log(message, 0);
        throw new BuildException(message);
      }
      catch (LinkageError e)
      {
        String message = "Could not load " + taskClass + ": " + e;
        project.log(message, 0);
        throw new BuildException(message, e);
      }
    }
  }
  
  public void checkProxyClass(Class<?> proxyClass)
  {
    checkTaskClass(proxyClass, getProject());
  }
  
  public void execute()
    throws BuildException
  {
    try
    {
      Method setLocationM = proxy.getClass().getMethod("setLocation", new Class[] { Location.class });
      if (setLocationM != null) {
        setLocationM.invoke(proxy, new Object[] { getLocation() });
      }
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}catch (Exception ex)
    {
      log("Error setting location in " + proxy.getClass(), 0);
      
      throw new BuildException(ex);
    }
    try
    {
      Method setProjectM = proxy.getClass().getMethod("setProject", new Class[] { Project.class });
      if (setProjectM != null) {
        setProjectM.invoke(proxy, new Object[] { getProject() });
      }
    }
    catch (NoSuchMethodException localNoSuchMethodException1) {}catch (Exception ex)
    {
      log("Error setting project in " + proxy.getClass(), 0);
      
      throw new BuildException(ex);
    }
    try
    {
      DispatchUtils.execute(proxy);
    }
    catch (BuildException be)
    {
      throw be;
    }
    catch (Exception ex)
    {
      log("Error in " + proxy.getClass(), 3);
      throw new BuildException(ex);
    }
  }
  
  public void setProxy(Object o)
  {
    proxy = o;
  }
  
  public Object getProxy()
  {
    return proxy;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.TaskAdapter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */