package org.apache.tools.ant.types;

public class PropertySet$BuiltinPropertySetName
  extends EnumeratedAttribute
{
  static final String ALL = "all";
  static final String SYSTEM = "system";
  static final String COMMANDLINE = "commandline";
  
  public String[] getValues()
  {
    return new String[] { "all", "system", "commandline" };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.PropertySet.BuiltinPropertySetName
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */