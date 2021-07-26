package org.apache.tools.ant.taskdefs.optional.unix;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline.Argument;

public class Chgrp
  extends AbstractAccessTask
{
  private boolean haveGroup = false;
  
  public Chgrp()
  {
    super.setExecutable("chgrp");
  }
  
  public void setGroup(String group)
  {
    createArg().setValue(group);
    haveGroup = true;
  }
  
  protected void checkConfiguration()
  {
    if (!haveGroup) {
      throw new BuildException("Required attribute group not set in chgrp", getLocation());
    }
    super.checkConfiguration();
  }
  
  public void setExecutable(String e)
  {
    throw new BuildException(getTaskType() + " doesn't support the executable attribute", getLocation());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.unix.Chgrp
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */