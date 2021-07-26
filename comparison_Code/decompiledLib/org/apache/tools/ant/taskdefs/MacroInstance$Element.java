package org.apache.tools.ant.taskdefs;

import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

public class MacroInstance$Element
  implements TaskContainer
{
  private List<Task> unknownElements = new ArrayList();
  
  public void addTask(Task nestedTask)
  {
    unknownElements.add(nestedTask);
  }
  
  public List<Task> getUnknownElements()
  {
    return unknownElements;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.MacroInstance.Element
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */