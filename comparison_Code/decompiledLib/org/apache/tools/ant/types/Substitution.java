package org.apache.tools.ant.types;

import org.apache.tools.ant.Project;

public class Substitution
  extends DataType
{
  public static final String DATA_TYPE_NAME = "substitution";
  private String expression;
  
  public Substitution()
  {
    expression = null;
  }
  
  public void setExpression(String expression)
  {
    this.expression = expression;
  }
  
  public String getExpression(Project p)
  {
    if (isReference()) {
      return getRef(p).getExpression(p);
    }
    return expression;
  }
  
  public Substitution getRef(Project p)
  {
    return (Substitution)getCheckedRef(Substitution.class, getDataTypeName(), p);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.Substitution
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */