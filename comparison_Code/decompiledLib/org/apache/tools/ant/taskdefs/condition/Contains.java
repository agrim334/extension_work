package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;

public class Contains
  implements Condition
{
  private String string;
  private String subString;
  private boolean caseSensitive = true;
  
  public void setString(String string)
  {
    this.string = string;
  }
  
  public void setSubstring(String subString)
  {
    this.subString = subString;
  }
  
  public void setCasesensitive(boolean b)
  {
    caseSensitive = b;
  }
  
  public boolean eval()
    throws BuildException
  {
    if ((string == null) || (subString == null)) {
      throw new BuildException("both string and substring are required in contains");
    }
    return caseSensitive ? 
      string.contains(subString) : 
      string.toLowerCase().contains(subString.toLowerCase());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.condition.Contains
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */