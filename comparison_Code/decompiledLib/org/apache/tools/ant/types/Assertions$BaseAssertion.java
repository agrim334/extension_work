package org.apache.tools.ant.types;

import org.apache.tools.ant.BuildException;

public abstract class Assertions$BaseAssertion
{
  private String packageName;
  private String className;
  
  public void setClass(String className)
  {
    this.className = className;
  }
  
  public void setPackage(String packageName)
  {
    this.packageName = packageName;
  }
  
  protected String getClassName()
  {
    return className;
  }
  
  protected String getPackageName()
  {
    return packageName;
  }
  
  public abstract String getCommandPrefix();
  
  public String toCommand()
  {
    if ((getPackageName() != null) && (getClassName() != null)) {
      throw new BuildException("Both package and class have been set");
    }
    StringBuilder command = new StringBuilder(getCommandPrefix());
    if (getPackageName() != null)
    {
      command.append(':');
      command.append(getPackageName());
      if (!command.toString().endsWith("...")) {
        command.append("...");
      }
    }
    else if (getClassName() != null)
    {
      command.append(':');
      command.append(getClassName());
    }
    return command.toString();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.Assertions.BaseAssertion
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */