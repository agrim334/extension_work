package org.apache.tools.ant;

import java.util.EventObject;

public class BuildEvent
  extends EventObject
{
  private static final long serialVersionUID = 4538050075952288486L;
  private final Project project;
  private final Target target;
  private final Task task;
  private String message;
  private int priority = 3;
  private Throwable exception;
  
  public BuildEvent(Project project)
  {
    super(project);
    this.project = project;
    target = null;
    task = null;
  }
  
  public BuildEvent(Target target)
  {
    super(target);
    project = target.getProject();
    this.target = target;
    task = null;
  }
  
  public BuildEvent(Task task)
  {
    super(task);
    project = task.getProject();
    target = task.getOwningTarget();
    this.task = task;
  }
  
  public void setMessage(String message, int priority)
  {
    this.message = message;
    this.priority = priority;
  }
  
  public void setException(Throwable exception)
  {
    this.exception = exception;
  }
  
  public Project getProject()
  {
    return project;
  }
  
  public Target getTarget()
  {
    return target;
  }
  
  public Task getTask()
  {
    return task;
  }
  
  public String getMessage()
  {
    return message;
  }
  
  public int getPriority()
  {
    return priority;
  }
  
  public Throwable getException()
  {
    return exception;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.BuildEvent
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */