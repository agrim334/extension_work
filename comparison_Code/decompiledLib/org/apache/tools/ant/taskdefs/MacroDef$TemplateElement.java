package org.apache.tools.ant.taskdefs;

import java.util.Locale;
import java.util.Objects;
import org.apache.tools.ant.BuildException;

public class MacroDef$TemplateElement
{
  private String name;
  private String description;
  private boolean optional = false;
  private boolean implicit = false;
  
  public void setName(String name)
  {
    if (!MacroDef.access$000(name)) {
      throw new BuildException("Illegal name [%s] for macro element", new Object[] { name });
    }
    this.name = name.toLowerCase(Locale.ENGLISH);
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setDescription(String desc)
  {
    description = desc;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public void setOptional(boolean optional)
  {
    this.optional = optional;
  }
  
  public boolean isOptional()
  {
    return optional;
  }
  
  public void setImplicit(boolean implicit)
  {
    this.implicit = implicit;
  }
  
  public boolean isImplicit()
  {
    return implicit;
  }
  
  public boolean equals(Object obj)
  {
    if (obj == this) {
      return true;
    }
    if ((obj == null) || (!obj.getClass().equals(getClass()))) {
      return false;
    }
    TemplateElement t = (TemplateElement)obj;
    return (name == null ? name == null : name
      .equals(name)) && (optional == optional) && (implicit == implicit);
  }
  
  public int hashCode()
  {
    return 
      Objects.hashCode(name) + (optional ? 1 : 0) + (implicit ? 1 : 0);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.MacroDef.TemplateElement
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */