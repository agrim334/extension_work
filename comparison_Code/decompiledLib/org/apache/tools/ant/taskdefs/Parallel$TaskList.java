package org.apache.tools.ant.taskdefs;

import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

public class Parallel$TaskList
  implements TaskContainer
{
  private List<Task> tasks = new ArrayList();
  
  public void addTask(Task nestedTask)
  {
    tasks.add(nestedTask);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Parallel.TaskList
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */