package org.apache.tools.ant.helper;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Executor;
import org.apache.tools.ant.Project;

public class SingleCheckExecutor
  implements Executor
{
  public void executeTargets(Project project, String[] targetNames)
    throws BuildException
  {
    project.executeSortedTargets(project
      .topoSort(targetNames, project.getTargets(), false));
  }
  
  public Executor getSubProjectExecutor()
  {
    return this;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.helper.SingleCheckExecutor
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */