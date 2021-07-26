package org.apache.tools.ant.types;

import java.security.Permission;
import org.apache.tools.ant.ExitException;

class Permissions$MySM
  extends SecurityManager
{
  private Permissions$MySM(Permissions paramPermissions) {}
  
  public void checkExit(int status)
  {
    Permission perm = new RuntimePermission("exitVM", null);
    try
    {
      checkPermission(perm);
    }
    catch (SecurityException e)
    {
      throw new ExitException(e.getMessage(), status);
    }
  }
  
  public void checkPermission(Permission perm)
  {
    if (Permissions.access$100(this$0)) {
      if ((Permissions.access$200(this$0)) && (!perm.getName().equals("exitVM")))
      {
        boolean permOK = false;
        if (Permissions.access$300(this$0).implies(perm)) {
          permOK = true;
        }
        checkRevoked(perm);
        if ((!permOK) && (Permissions.access$400(this$0) != null)) {
          Permissions.access$400(this$0).checkPermission(perm);
        }
      }
      else
      {
        if (!Permissions.access$300(this$0).implies(perm)) {
          throw new SecurityException("Permission " + perm + " was not granted.");
        }
        checkRevoked(perm);
      }
    }
  }
  
  private void checkRevoked(Permission perm)
  {
    for (Permissions.Permission revoked : Permissions.access$500(this$0)) {
      if (revoked.matches(perm)) {
        throw new SecurityException("Permission " + perm + " was revoked.");
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.Permissions.MySM
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */