package org.apache.tools.ant;

import java.io.IOException;
import java.util.Collections;
import org.apache.tools.ant.dispatch.DispatchUtils;

public abstract class Task
  extends ProjectComponent
{
  @Deprecated
  protected Target target;
  @Deprecated
  protected String taskName;
  @Deprecated
  protected String taskType;
  @Deprecated
  protected RuntimeConfigurable wrapper;
  private boolean invalid;
  private UnknownElement replacement;
  
  public void setOwningTarget(Target target)
  {
    this.target = target;
  }
  
  public Target getOwningTarget()
  {
    return target;
  }
  
  public void setTaskName(String name)
  {
    taskName = name;
  }
  
  public String getTaskName()
  {
    return taskName;
  }
  
  public void setTaskType(String type)
  {
    taskType = type;
  }
  
  public void init()
    throws BuildException
  {}
  
  public void execute()
    throws BuildException
  {}
  
  public RuntimeConfigurable getRuntimeConfigurableWrapper()
  {
    if (wrapper == null) {
      wrapper = new RuntimeConfigurable(this, getTaskName());
    }
    return wrapper;
  }
  
  public void setRuntimeConfigurableWrapper(RuntimeConfigurable wrapper)
  {
    this.wrapper = wrapper;
  }
  
  public void maybeConfigure()
    throws BuildException
  {
    if (invalid) {
      getReplacement();
    } else if (wrapper != null) {
      wrapper.maybeConfigure(getProject());
    }
  }
  
  public void reconfigure()
  {
    if (wrapper != null) {
      wrapper.reconfigure(getProject());
    }
  }
  
  protected void handleOutput(String output)
  {
    log(output, 2);
  }
  
  protected void handleFlush(String output)
  {
    handleOutput(output);
  }
  
  protected int handleInput(byte[] buffer, int offset, int length)
    throws IOException
  {
    return getProject().defaultInput(buffer, offset, length);
  }
  
  protected void handleErrorOutput(String output)
  {
    log(output, 1);
  }
  
  protected void handleErrorFlush(String output)
  {
    handleErrorOutput(output);
  }
  
  public void log(String msg)
  {
    log(msg, 2);
  }
  
  public void log(String msg, int msgLevel)
  {
    if (getProject() == null) {
      super.log(msg, msgLevel);
    } else {
      getProject().log(this, msg, msgLevel);
    }
  }
  
  public void log(Throwable t, int msgLevel)
  {
    if (t != null) {
      log(t.getMessage(), t, msgLevel);
    }
  }
  
  public void log(String msg, Throwable t, int msgLevel)
  {
    if (getProject() == null) {
      super.log(msg, msgLevel);
    } else {
      getProject().log(this, msg, t, msgLevel);
    }
  }
  
  public final void perform()
  {
    if (invalid)
    {
      UnknownElement ue = getReplacement();
      Task task = ue.getTask();
      task.perform();
    }
    else
    {
      getProject().fireTaskStarted(this);
      Throwable reason = null;
      try
      {
        maybeConfigure();
        DispatchUtils.execute(this);
      }
      catch (BuildException ex)
      {
        if (ex.getLocation() == Location.UNKNOWN_LOCATION) {
          ex.setLocation(getLocation());
        }
        reason = ex;
        throw ex;
      }
      catch (Exception ex)
      {
        reason = ex;
        BuildException be = new BuildException(ex);
        be.setLocation(getLocation());
        throw be;
      }
      catch (Error ex)
      {
        reason = ex;
        throw ex;
      }
      finally
      {
        getProject().fireTaskFinished(this, reason);
      }
    }
  }
  
  final void markInvalid()
  {
    invalid = true;
  }
  
  protected final boolean isInvalid()
  {
    return invalid;
  }
  
  private UnknownElement getReplacement()
  {
    if (replacement == null)
    {
      replacement = new UnknownElement(taskType);
      replacement.setProject(getProject());
      replacement.setTaskType(taskType);
      replacement.setTaskName(taskName);
      replacement.setLocation(getLocation());
      replacement.setOwningTarget(target);
      replacement.setRuntimeConfigurableWrapper(wrapper);
      wrapper.setProxy(replacement);
      replaceChildren(wrapper, replacement);
      target.replaceChild(this, replacement);
      replacement.maybeConfigure();
    }
    return replacement;
  }
  
  private void replaceChildren(RuntimeConfigurable wrapper, UnknownElement parentElement)
  {
    for (RuntimeConfigurable childWrapper : Collections.list(wrapper.getChildren()))
    {
      UnknownElement childElement = new UnknownElement(childWrapper.getElementTag());
      parentElement.addChild(childElement);
      childElement.setProject(getProject());
      childElement.setRuntimeConfigurableWrapper(childWrapper);
      childWrapper.setProxy(childElement);
      replaceChildren(childWrapper, childElement);
    }
  }
  
  public String getTaskType()
  {
    return taskType;
  }
  
  protected RuntimeConfigurable getWrapper()
  {
    return wrapper;
  }
  
  public final void bindToOwner(Task owner)
  {
    setProject(owner.getProject());
    setOwningTarget(owner.getOwningTarget());
    setTaskName(owner.getTaskName());
    setDescription(owner.getDescription());
    setLocation(owner.getLocation());
    setTaskType(owner.getTaskType());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.Task
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */