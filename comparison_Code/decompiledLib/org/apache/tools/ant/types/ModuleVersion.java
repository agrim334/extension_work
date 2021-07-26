package org.apache.tools.ant.types;

import java.util.Objects;

public class ModuleVersion
{
  private String number;
  private String preRelease;
  private String build;
  
  public String getNumber()
  {
    return number;
  }
  
  public void setNumber(String number)
  {
    Objects.requireNonNull(number, "Version number cannot be null.");
    if ((number.indexOf('-') >= 0) || (number.indexOf('+') >= 0)) {
      throw new IllegalArgumentException("Version number cannot contain '-' or '+'.");
    }
    this.number = number;
  }
  
  public String getPreRelease()
  {
    return preRelease;
  }
  
  public void setPreRelease(String pre)
  {
    if ((pre != null) && (pre.indexOf('+') >= 0)) {
      throw new IllegalArgumentException("Version's pre-release cannot contain '+'.");
    }
    preRelease = pre;
  }
  
  public String getBuild()
  {
    return build;
  }
  
  public void setBuild(String build)
  {
    this.build = build;
  }
  
  public String toModuleVersionString()
  {
    if (number == null) {
      throw new IllegalStateException("Version number cannot be null.");
    }
    StringBuilder version = new StringBuilder(number);
    if ((preRelease != null) || (build != null)) {
      version.append('-').append(Objects.toString(preRelease, ""));
    }
    if (build != null) {
      version.append('+').append(build);
    }
    return version.toString();
  }
  
  public String toString()
  {
    return getClass().getName() + "[number=" + number + ", preRelease=" + preRelease + ", build=" + build + "]";
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.ModuleVersion
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */