package org.apache.tools.ant;

public final class ProjectHelper$OnMissingExtensionPoint
{
  public static final OnMissingExtensionPoint FAIL = new OnMissingExtensionPoint("fail");
  public static final OnMissingExtensionPoint WARN = new OnMissingExtensionPoint("warn");
  public static final OnMissingExtensionPoint IGNORE = new OnMissingExtensionPoint("ignore");
  private static final OnMissingExtensionPoint[] values = { FAIL, WARN, IGNORE };
  private final String name;
  
  private ProjectHelper$OnMissingExtensionPoint(String name)
  {
    this.name = name;
  }
  
  public String name()
  {
    return name;
  }
  
  public String toString()
  {
    return name;
  }
  
  public static OnMissingExtensionPoint valueOf(String name)
  {
    if (name == null) {
      throw new NullPointerException();
    }
    for (OnMissingExtensionPoint value : values) {
      if (name.equals(value.name())) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown onMissingExtensionPoint " + name);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.ProjectHelper.OnMissingExtensionPoint
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */