package org.apache.tools.ant.types;

import java.lang.reflect.Constructor;
import java.net.SocketPermission;
import java.security.Permission;
import java.security.UnresolvedPermission;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PropertyPermission;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ExitException;

public class Permissions
{
  private final List<Permission> grantedPermissions = new LinkedList();
  private final List<Permission> revokedPermissions = new LinkedList();
  private java.security.Permissions granted = null;
  private SecurityManager origSm = null;
  private boolean active = false;
  private final boolean delegateToOldSM;
  private static final Class<?>[] PARAMS = { String.class, String.class };
  
  public Permissions()
  {
    this(false);
  }
  
  public Permissions(boolean delegateToOldSM)
  {
    this.delegateToOldSM = delegateToOldSM;
  }
  
  public void addConfiguredGrant(Permission perm)
  {
    grantedPermissions.add(perm);
  }
  
  public void addConfiguredRevoke(Permission perm)
  {
    revokedPermissions.add(perm);
  }
  
  public synchronized void setSecurityManager()
    throws BuildException
  {
    origSm = System.getSecurityManager();
    init();
    System.setSecurityManager(new MySM(null));
    active = true;
  }
  
  private void init()
    throws BuildException
  {
    granted = new java.security.Permissions();
    for (Permission p : revokedPermissions) {
      if (p.getClassName() == null) {
        throw new BuildException("Revoked permission " + p + " does not contain a class.");
      }
    }
    for (Permission p : grantedPermissions)
    {
      if (p.getClassName() == null) {
        throw new BuildException("Granted permission " + p + " does not contain a class.");
      }
      Permission perm = createPermission(p);
      granted.add(perm);
    }
    granted.add(new SocketPermission("localhost:1024-", "listen"));
    granted.add(new PropertyPermission("java.version", "read"));
    granted.add(new PropertyPermission("java.vendor", "read"));
    granted.add(new PropertyPermission("java.vendor.url", "read"));
    granted.add(new PropertyPermission("java.class.version", "read"));
    granted.add(new PropertyPermission("os.name", "read"));
    granted.add(new PropertyPermission("os.version", "read"));
    granted.add(new PropertyPermission("os.arch", "read"));
    granted.add(new PropertyPermission("file.encoding", "read"));
    granted.add(new PropertyPermission("file.separator", "read"));
    granted.add(new PropertyPermission("path.separator", "read"));
    granted.add(new PropertyPermission("line.separator", "read"));
    granted.add(new PropertyPermission("java.specification.version", "read"));
    granted.add(new PropertyPermission("java.specification.vendor", "read"));
    granted.add(new PropertyPermission("java.specification.name", "read"));
    granted.add(new PropertyPermission("java.vm.specification.version", "read"));
    granted.add(new PropertyPermission("java.vm.specification.vendor", "read"));
    granted.add(new PropertyPermission("java.vm.specification.name", "read"));
    granted.add(new PropertyPermission("java.vm.version", "read"));
    granted.add(new PropertyPermission("java.vm.vendor", "read"));
    granted.add(new PropertyPermission("java.vm.name", "read"));
  }
  
  private Permission createPermission(Permission permission)
  {
    try
    {
      Class<? extends Permission> clazz = Class.forName(permission.getClassName()).asSubclass(Permission.class);
      String name = permission.getName();
      String actions = permission.getActions();
      Constructor<? extends Permission> ctr = clazz.getConstructor(PARAMS);
      return (Permission)ctr.newInstance(new Object[] { name, actions });
    }
    catch (Exception e) {}
    return new UnresolvedPermission(permission.getClassName(), permission
      .getName(), permission.getActions(), null);
  }
  
  public synchronized void restoreSecurityManager()
  {
    active = false;
    System.setSecurityManager(origSm);
  }
  
  private class MySM
    extends SecurityManager
  {
    private MySM() {}
    
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
      if (active) {
        if ((delegateToOldSM) && (!perm.getName().equals("exitVM")))
        {
          boolean permOK = false;
          if (granted.implies(perm)) {
            permOK = true;
          }
          checkRevoked(perm);
          if ((!permOK) && (origSm != null)) {
            origSm.checkPermission(perm);
          }
        }
        else
        {
          if (!granted.implies(perm)) {
            throw new SecurityException("Permission " + perm + " was not granted.");
          }
          checkRevoked(perm);
        }
      }
    }
    
    private void checkRevoked(Permission perm)
    {
      for (Permissions.Permission revoked : revokedPermissions) {
        if (revoked.matches(perm)) {
          throw new SecurityException("Permission " + perm + " was revoked.");
        }
      }
    }
  }
  
  public static class Permission
  {
    private String className;
    private String name;
    private String actionString;
    private Set<String> actions;
    
    public void setClass(String aClass)
    {
      className = aClass.trim();
    }
    
    public String getClassName()
    {
      return className;
    }
    
    public void setName(String aName)
    {
      name = aName.trim();
    }
    
    public String getName()
    {
      return name;
    }
    
    public void setActions(String actions)
    {
      actionString = actions;
      if (!actions.isEmpty()) {
        this.actions = parseActions(actions);
      }
    }
    
    public String getActions()
    {
      return actionString;
    }
    
    boolean matches(Permission perm)
    {
      if (!className.equals(perm.getClass().getName())) {
        return false;
      }
      if (name != null) {
        if (name.endsWith("*"))
        {
          if (!perm.getName().startsWith(name.substring(0, name.length() - 1))) {
            return false;
          }
        }
        else if (!name.equals(perm.getName())) {
          return false;
        }
      }
      if (actions != null)
      {
        Set<String> as = parseActions(perm.getActions());
        int size = as.size();
        as.removeAll(actions);
        
        return as.size() != size;
      }
      return true;
    }
    
    private Set<String> parseActions(String actions)
    {
      Set<String> result = new HashSet();
      StringTokenizer tk = new StringTokenizer(actions, ",");
      while (tk.hasMoreTokens())
      {
        String item = tk.nextToken().trim();
        if (!item.isEmpty()) {
          result.add(item);
        }
      }
      return result;
    }
    
    public String toString()
    {
      return "Permission: " + className + " (\"" + name + "\", \"" + actions + "\")";
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.Permissions
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */