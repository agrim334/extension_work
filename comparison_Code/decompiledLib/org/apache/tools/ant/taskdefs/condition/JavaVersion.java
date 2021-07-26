package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.DeweyDecimal;
import org.apache.tools.ant.util.JavaEnvUtils;

public class JavaVersion
  implements Condition
{
  private String atLeast = null;
  private String exactly = null;
  
  public boolean eval()
    throws BuildException
  {
    validate();
    DeweyDecimal actual = JavaEnvUtils.getParsedJavaVersion();
    if (null != atLeast) {
      return actual.isGreaterThanOrEqual(new DeweyDecimal(atLeast));
    }
    if (null != exactly) {
      return actual.isEqual(new DeweyDecimal(exactly));
    }
    return false;
  }
  
  private void validate()
    throws BuildException
  {
    if ((atLeast != null) && (exactly != null)) {
      throw new BuildException("Only one of atleast or exactly may be set.");
    }
    if ((null == atLeast) && (null == exactly)) {
      throw new BuildException("One of atleast or exactly must be set.");
    }
    if (atLeast != null) {
      try
      {
        new DeweyDecimal(atLeast);
      }
      catch (NumberFormatException e)
      {
        throw new BuildException("The 'atleast' attribute is not a Dewey Decimal eg 1.1.0 : " + atLeast);
      }
    } else {
      try
      {
        new DeweyDecimal(exactly);
      }
      catch (NumberFormatException e)
      {
        throw new BuildException("The 'exactly' attribute is not a Dewey Decimal eg 1.1.0 : " + exactly);
      }
    }
  }
  
  public String getAtLeast()
  {
    return atLeast;
  }
  
  public void setAtLeast(String atLeast)
  {
    this.atLeast = atLeast;
  }
  
  public String getExactly()
  {
    return exactly;
  }
  
  public void setExactly(String exactly)
  {
    this.exactly = exactly;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.condition.JavaVersion
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */