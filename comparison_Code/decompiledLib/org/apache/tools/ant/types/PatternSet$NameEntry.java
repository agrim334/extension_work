package org.apache.tools.ant.types;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;

public class PatternSet$NameEntry
{
  private String name;
  private Object ifCond;
  private Object unlessCond;
  
  public PatternSet$NameEntry(PatternSet this$0) {}
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public void setIf(Object cond)
  {
    ifCond = cond;
  }
  
  public void setIf(String cond)
  {
    setIf(cond);
  }
  
  public void setUnless(Object cond)
  {
    unlessCond = cond;
  }
  
  public void setUnless(String cond)
  {
    setUnless(cond);
  }
  
  public String getName()
  {
    return name;
  }
  
  public String evalName(Project p)
  {
    return valid(p) ? name : null;
  }
  
  private boolean valid(Project p)
  {
    PropertyHelper ph = PropertyHelper.getPropertyHelper(p);
    return (ph.testIfCondition(ifCond)) && 
      (ph.testUnlessCondition(unlessCond));
  }
  
  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    if (name == null) {
      buf.append("noname");
    } else {
      buf.append(name);
    }
    if ((ifCond != null) || (unlessCond != null))
    {
      buf.append(":");
      String connector = "";
      if (ifCond != null)
      {
        buf.append("if->");
        buf.append(ifCond);
        connector = ";";
      }
      if (unlessCond != null)
      {
        buf.append(connector);
        buf.append("unless->");
        buf.append(unlessCond);
      }
    }
    return buf.toString();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.PatternSet.NameEntry
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */