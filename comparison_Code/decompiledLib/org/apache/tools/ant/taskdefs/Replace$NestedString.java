package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.Project;

public class Replace$NestedString
{
  private boolean expandProperties = false;
  private StringBuffer buf = new StringBuffer();
  
  public Replace$NestedString(Replace this$0) {}
  
  public void setExpandProperties(boolean b)
  {
    expandProperties = b;
  }
  
  public void addText(String val)
  {
    buf.append(val);
  }
  
  public String getText()
  {
    String s = buf.toString();
    return expandProperties ? this$0.getProject().replaceProperties(s) : s;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Replace.NestedString
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */