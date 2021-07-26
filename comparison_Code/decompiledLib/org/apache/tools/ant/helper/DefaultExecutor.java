package org.apache.tools.ant.helper;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Executor;
import org.apache.tools.ant.Project;

public class DefaultExecutor
  implements Executor
{
  private static final SingleCheckExecutor SUB_EXECUTOR = new SingleCheckExecutor();
  
  public void executeTargets(Project project, String[] targetNames)
    throws BuildException
  {
    BuildException thrownException = null;
    for (String targetName : targetNames) {
      try
      {
        project.executeTarget(targetName);
      }
      catch (BuildException ex)
      {
        if (project.isKeepGoingMode()) {
          thrownException = ex;
        } else {
          throw ex;
        }
      }
    }
    if (thrownException != null) {
      throw thrownException;
    }
  }
  
  public Executor getSubProjectExecutor()
  {
    return SUB_EXECUTOR;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.helper.DefaultExecutor
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */