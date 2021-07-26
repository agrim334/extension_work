package org.apache.tools.ant.taskdefs.optional.unix;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.ExecuteOn;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.FileSet;

public abstract class AbstractAccessTask
  extends ExecuteOn
{
  public AbstractAccessTask()
  {
    super.setParallel(true);
    super.setSkipEmptyFilesets(true);
  }
  
  public void setFile(File src)
  {
    FileSet fs = new FileSet();
    fs.setFile(src);
    addFileset(fs);
  }
  
  public void setCommand(Commandline cmdl)
  {
    throw new BuildException(getTaskType() + " doesn't support the command attribute", getLocation());
  }
  
  public void setSkipEmptyFilesets(boolean skip)
  {
    throw new BuildException(getTaskType() + " doesn't support the skipemptyfileset attribute", getLocation());
  }
  
  public void setAddsourcefile(boolean b)
  {
    throw new BuildException(getTaskType() + " doesn't support the addsourcefile attribute", getLocation());
  }
  
  protected boolean isValidOs()
  {
    return (getOs() == null) && (getOsFamily() == null) ? 
      Os.isFamily("unix") : super.isValidOs();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.unix.AbstractAccessTask
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */