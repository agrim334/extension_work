package org.apache.tools.ant.types;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public class Assertions
  extends DataType
  implements Cloneable
{
  private Boolean enableSystemAssertions;
  private ArrayList<BaseAssertion> assertionList = new ArrayList();
  
  public void addEnable(EnabledAssertion assertion)
  {
    checkChildrenAllowed();
    assertionList.add(assertion);
  }
  
  public void addDisable(DisabledAssertion assertion)
  {
    checkChildrenAllowed();
    assertionList.add(assertion);
  }
  
  public void setEnableSystemAssertions(Boolean enableSystemAssertions)
  {
    checkAttributesAllowed();
    this.enableSystemAssertions = enableSystemAssertions;
  }
  
  public void setRefid(Reference ref)
  {
    if ((!assertionList.isEmpty()) || (enableSystemAssertions != null)) {
      throw tooManyAttributes();
    }
    super.setRefid(ref);
  }
  
  private Assertions getFinalReference()
  {
    if (getRefid() == null) {
      return this;
    }
    Object o = getRefid().getReferencedObject(getProject());
    if (!(o instanceof Assertions)) {
      throw new BuildException("reference is of wrong type");
    }
    return (Assertions)o;
  }
  
  public int size()
  {
    Assertions clause = getFinalReference();
    return clause.getFinalSize();
  }
  
  private int getFinalSize()
  {
    return assertionList.size() + (enableSystemAssertions != null ? 1 : 0);
  }
  
  public void applyAssertions(List<String> commandList)
  {
    getProject().log("Applying assertions", 4);
    Assertions clause = getFinalReference();
    if (Boolean.TRUE.equals(enableSystemAssertions))
    {
      getProject().log("Enabling system assertions", 4);
      commandList.add("-enablesystemassertions");
    }
    else if (Boolean.FALSE.equals(enableSystemAssertions))
    {
      getProject().log("disabling system assertions", 4);
      commandList.add("-disablesystemassertions");
    }
    for (BaseAssertion assertion : assertionList)
    {
      String arg = assertion.toCommand();
      getProject().log("adding assertion " + arg, 4);
      commandList.add(arg);
    }
  }
  
  public void applyAssertions(CommandlineJava command)
  {
    Assertions clause = getFinalReference();
    if (Boolean.TRUE.equals(enableSystemAssertions)) {
      addVmArgument(command, "-enablesystemassertions");
    } else if (Boolean.FALSE.equals(enableSystemAssertions)) {
      addVmArgument(command, "-disablesystemassertions");
    }
    for (BaseAssertion assertion : assertionList)
    {
      String arg = assertion.toCommand();
      addVmArgument(command, arg);
    }
  }
  
  public void applyAssertions(ListIterator<String> commandIterator)
  {
    getProject().log("Applying assertions", 4);
    Assertions clause = getFinalReference();
    if (Boolean.TRUE.equals(enableSystemAssertions))
    {
      getProject().log("Enabling system assertions", 4);
      commandIterator.add("-enablesystemassertions");
    }
    else if (Boolean.FALSE.equals(enableSystemAssertions))
    {
      getProject().log("disabling system assertions", 4);
      commandIterator.add("-disablesystemassertions");
    }
    for (BaseAssertion assertion : assertionList)
    {
      String arg = assertion.toCommand();
      getProject().log("adding assertion " + arg, 4);
      commandIterator.add(arg);
    }
  }
  
  private static void addVmArgument(CommandlineJava command, String arg)
  {
    Commandline.Argument argument = command.createVmArgument();
    argument.setValue(arg);
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    Assertions that = (Assertions)super.clone();
    assertionList = new ArrayList(assertionList);
    return that;
  }
  
  public static abstract class BaseAssertion
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
  
  public static class EnabledAssertion
    extends Assertions.BaseAssertion
  {
    public String getCommandPrefix()
    {
      return "-ea";
    }
  }
  
  public static class DisabledAssertion
    extends Assertions.BaseAssertion
  {
    public String getCommandPrefix()
    {
      return "-da";
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.Assertions
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */