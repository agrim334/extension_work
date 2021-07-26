package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;

public class XSLTProcess$Param
{
  private String name = null;
  private String expression = null;
  private String type;
  private Object ifCond;
  private Object unlessCond;
  private Project project;
  
  public void setProject(Project project)
  {
    this.project = project;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void setExpression(String expression)
  {
    this.expression = expression;
  }
  
  public void setType(String type)
  {
    this.type = type;
  }
  
  public String getName()
    throws BuildException
  {
    if (name == null) {
      throw new BuildException("Name attribute is missing.");
    }
    return name;
  }
  
  public String getExpression()
    throws BuildException
  {
    if (expression == null) {
      throw new BuildException("Expression attribute is missing.");
    }
    return expression;
  }
  
  public String getType()
  {
    return type;
  }
  
  public void setIf(Object ifCond)
  {
    this.ifCond = ifCond;
  }
  
  public void setIf(String ifProperty)
  {
    setIf(ifProperty);
  }
  
  public void setUnless(Object unlessCond)
  {
    this.unlessCond = unlessCond;
  }
  
  public void setUnless(String unlessProperty)
  {
    setUnless(unlessProperty);
  }
  
  public boolean shouldUse()
  {
    PropertyHelper ph = PropertyHelper.getPropertyHelper(project);
    return (ph.testIfCondition(ifCond)) && 
      (ph.testUnlessCondition(unlessCond));
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.XSLTProcess.Param
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */