package org.apache.tools.ant.taskdefs;

import java.util.Locale;
import java.util.Objects;
import org.apache.tools.ant.BuildException;

public class MacroDef$Text
{
  private String name;
  private boolean optional;
  private boolean trim;
  private String description;
  private String defaultString;
  
  public void setName(String name)
  {
    if (!MacroDef.access$000(name)) {
      throw new BuildException("Illegal name [%s] for element", new Object[] { name });
    }
    this.name = name.toLowerCase(Locale.ENGLISH);
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setOptional(boolean optional)
  {
    this.optional = optional;
  }
  
  public boolean getOptional()
  {
    return optional;
  }
  
  public void setTrim(boolean trim)
  {
    this.trim = trim;
  }
  
  public boolean getTrim()
  {
    return trim;
  }
  
  public void setDescription(String desc)
  {
    description = desc;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public void setDefault(String defaultString)
  {
    this.defaultString = defaultString;
  }
  
  public String getDefault()
  {
    return defaultString;
  }
  
  public boolean equals(Object obj)
  {
    if (obj == null) {
      return false;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    Text other = (Text)obj;
    if ((Objects.equals(name, name)) && (optional == optional) && (trim == trim)) {}
    return 
    
      Objects.equals(defaultString, defaultString);
  }
  
  public int hashCode()
  {
    return Objects.hashCode(name);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.MacroDef.Text
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */