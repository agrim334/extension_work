package org.apache.tools.ant.taskdefs.optional.unix;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline.Argument;

public class Chown
  extends AbstractAccessTask
{
  private boolean haveOwner = false;
  
  public Chown()
  {
    super.setExecutable("chown");
  }
  
  public void setOwner(String owner)
  {
    createArg().setValue(owner);
    haveOwner = true;
  }
  
  protected void checkConfiguration()
  {
    if (!haveOwner) {
      throw new BuildException("Required attribute owner not set in chown", getLocation());
    }
    super.checkConfiguration();
  }
  
  public void setExecutable(String e)
  {
    throw new BuildException(getTaskType() + " doesn't support the executable attribute", getLocation());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.unix.Chown
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */