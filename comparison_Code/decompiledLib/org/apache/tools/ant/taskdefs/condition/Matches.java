package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpUtil;

public class Matches
  extends ProjectComponent
  implements Condition
{
  private String string;
  private boolean caseSensitive = true;
  private boolean multiLine = false;
  private boolean singleLine = false;
  private RegularExpression regularExpression;
  
  public void setString(String string)
  {
    this.string = string;
  }
  
  public void setPattern(String pattern)
  {
    if (regularExpression != null) {
      throw new BuildException("Only one regular expression is allowed.");
    }
    regularExpression = new RegularExpression();
    regularExpression.setPattern(pattern);
  }
  
  public void addRegexp(RegularExpression regularExpression)
  {
    if (this.regularExpression != null) {
      throw new BuildException("Only one regular expression is allowed.");
    }
    this.regularExpression = regularExpression;
  }
  
  public void setCasesensitive(boolean b)
  {
    caseSensitive = b;
  }
  
  public void setMultiline(boolean b)
  {
    multiLine = b;
  }
  
  public void setSingleLine(boolean b)
  {
    singleLine = b;
  }
  
  public boolean eval()
    throws BuildException
  {
    if (string == null) {
      throw new BuildException("Parameter string is required in matches.");
    }
    if (regularExpression == null) {
      throw new BuildException("Missing pattern in matches.");
    }
    int options = RegexpUtil.asOptions(caseSensitive, multiLine, singleLine);
    Regexp regexp = regularExpression.getRegexp(getProject());
    return regexp.matches(string, options);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.condition.Matches
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */