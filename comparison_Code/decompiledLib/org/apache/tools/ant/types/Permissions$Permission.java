package org.apache.tools.ant.types;

import java.security.Permission;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class Permissions$Permission
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

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.Permissions.Permission
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */