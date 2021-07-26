package org.apache.tools.ant.util.optional;

import java.security.Permission;
import org.apache.tools.ant.ExitException;

public class NoExitSecurityManager
  extends SecurityManager
{
  public void checkExit(int status)
  {
    throw new ExitException(status);
  }
  
  public void checkPermission(Permission perm) {}
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.optional.NoExitSecurityManager
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */