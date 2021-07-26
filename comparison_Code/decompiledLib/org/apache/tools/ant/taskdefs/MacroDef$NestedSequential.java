package org.apache.tools.ant.taskdefs;

import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;

public class MacroDef$NestedSequential
  implements TaskContainer
{
  private List<Task> nested = new ArrayList();
  
  public void addTask(Task task)
  {
    nested.add(task);
  }
  
  public List<Task> getNested()
  {
    return nested;
  }
  
  public boolean similar(NestedSequential other)
  {
    int size = nested.size();
    if (size != nested.size()) {
      return false;
    }
    for (int i = 0; i < size; i++)
    {
      UnknownElement me = (UnknownElement)nested.get(i);
      UnknownElement o = (UnknownElement)nested.get(i);
      if (!me.similar(o)) {
        return false;
      }
    }
    return true;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.MacroDef.NestedSequential
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */