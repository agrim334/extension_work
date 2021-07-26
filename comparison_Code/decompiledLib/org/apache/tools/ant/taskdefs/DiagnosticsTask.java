package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Diagnostics;
import org.apache.tools.ant.Task;

public class DiagnosticsTask
  extends Task
{
  private static final String[] ARGS = new String[0];
  
  public void execute()
    throws BuildException
  {
    Diagnostics.main(ARGS);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.DiagnosticsTask
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */