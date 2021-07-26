package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskAdapter;

public class Taskdef
  extends Typedef
{
  public Taskdef()
  {
    setAdapterClass(TaskAdapter.class);
    setAdaptToClass(Task.class);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Taskdef
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */