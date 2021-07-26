package org.apache.tools.ant.types;

import org.apache.tools.ant.BuildException;

public class PropertySet$PropertyRef
{
  private int count;
  private String name;
  private String regex;
  private String prefix;
  private String builtin;
  
  public void setName(String name)
  {
    assertValid("name", name);
    this.name = name;
  }
  
  public void setRegex(String regex)
  {
    assertValid("regex", regex);
    this.regex = regex;
  }
  
  public void setPrefix(String prefix)
  {
    assertValid("prefix", prefix);
    this.prefix = prefix;
  }
  
  public void setBuiltin(PropertySet.BuiltinPropertySetName b)
  {
    String pBuiltIn = b.getValue();
    assertValid("builtin", pBuiltIn);
    builtin = pBuiltIn;
  }
  
  private void assertValid(String attr, String value)
  {
    if ((value == null) || (value.length() < 1)) {
      throw new BuildException("Invalid attribute: " + attr);
    }
    if (++count != 1) {
      throw new BuildException("Attributes name, regex, and prefix are mutually exclusive");
    }
  }
  
  public String toString()
  {
    return "name=" + name + ", regex=" + regex + ", prefix=" + prefix + ", builtin=" + builtin;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.PropertySet.PropertyRef
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */