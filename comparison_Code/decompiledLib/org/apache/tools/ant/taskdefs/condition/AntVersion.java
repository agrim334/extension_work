package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.DeweyDecimal;

public class AntVersion
  extends Task
  implements Condition
{
  private String atLeast = null;
  private String exactly = null;
  private String propertyname = null;
  
  public void execute()
    throws BuildException
  {
    if (propertyname == null) {
      throw new BuildException("'property' must be set.");
    }
    if ((atLeast != null) || (exactly != null))
    {
      if (eval()) {
        getProject().setNewProperty(propertyname, getVersion().toString());
      }
    }
    else {
      getProject().setNewProperty(propertyname, getVersion().toString());
    }
  }
  
  public boolean eval()
    throws BuildException
  {
    validate();
    DeweyDecimal actual = getVersion();
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
        throw new BuildException("The 'atleast' attribute is not a Dewey Decimal eg 1.1.0 : %s", new Object[] { atLeast });
      }
    } else {
      try
      {
        new DeweyDecimal(exactly);
      }
      catch (NumberFormatException e)
      {
        throw new BuildException("The 'exactly' attribute is not a Dewey Decimal eg 1.1.0 : %s", new Object[] { exactly });
      }
    }
  }
  
  private DeweyDecimal getVersion()
  {
    Project p = new Project();
    p.init();
    StringBuilder sb = new StringBuilder();
    boolean foundFirstDigit = false;
    for (char versionChar : p.getProperty("ant.version").toCharArray())
    {
      if (Character.isDigit(versionChar))
      {
        sb.append(versionChar);
        foundFirstDigit = true;
      }
      if ((versionChar == '.') && (foundFirstDigit)) {
        sb.append(versionChar);
      }
      if ((Character.isLetter(versionChar)) && (foundFirstDigit)) {
        break;
      }
    }
    return new DeweyDecimal(sb.toString());
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
  
  public String getProperty()
  {
    return propertyname;
  }
  
  public void setProperty(String propertyname)
  {
    this.propertyname = propertyname;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.condition.AntVersion
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */