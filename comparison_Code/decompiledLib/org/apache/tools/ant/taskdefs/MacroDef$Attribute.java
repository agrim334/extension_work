package org.apache.tools.ant.taskdefs;

import java.util.Locale;
import java.util.Objects;
import org.apache.tools.ant.BuildException;

public class MacroDef$Attribute
{
  private String name;
  private String defaultValue;
  private String description;
  private boolean doubleExpanding = true;
  
  public void setName(String name)
  {
    if (!MacroDef.access$000(name)) {
      throw new BuildException("Illegal name [%s] for attribute", new Object[] { name });
    }
    this.name = name.toLowerCase(Locale.ENGLISH);
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setDefault(String defaultValue)
  {
    this.defaultValue = defaultValue;
  }
  
  public String getDefault()
  {
    return defaultValue;
  }
  
  public void setDescription(String desc)
  {
    description = desc;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public void setDoubleExpanding(boolean doubleExpanding)
  {
    this.doubleExpanding = doubleExpanding;
  }
  
  public boolean isDoubleExpanding()
  {
    return doubleExpanding;
  }
  
  public boolean equals(Object obj)
  {
    if (obj == null) {
      return false;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    Attribute other = (Attribute)obj;
    if (name == null)
    {
      if (name != null) {
        return false;
      }
    }
    else if (!name.equals(name)) {
      return false;
    }
    return defaultValue == null ? false : defaultValue == null ? true : 
      defaultValue.equals(defaultValue);
  }
  
  public int hashCode()
  {
    return Objects.hashCode(defaultValue) + Objects.hashCode(name);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.MacroDef.Attribute
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */