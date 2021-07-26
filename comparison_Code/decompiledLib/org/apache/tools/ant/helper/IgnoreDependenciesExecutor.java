package org.apache.tools.ant.helper;

import java.util.Hashtable;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Executor;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;

public class IgnoreDependenciesExecutor
  implements Executor
{
  private static final SingleCheckExecutor SUB_EXECUTOR = new SingleCheckExecutor();
  
  public void executeTargets(Project project, String[] targetNames)
    throws BuildException
  {
    Hashtable<String, Target> targets = project.getTargets();
    BuildException thrownException = null;
    for (String targetName : targetNames) {
      try
      {
        Target t = (Target)targets.get(targetName);
        if (t == null) {
          throw new BuildException("Unknown target " + targetName);
        }
        t.performTasks();
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
 * Qualified Name:     org.apache.tools.ant.helper.IgnoreDependenciesExecutor
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */