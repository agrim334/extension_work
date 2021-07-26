package org.apache.tools.ant.taskdefs.optional.script;

import java.util.Locale;

public class ScriptDef$NestedElement
{
  private String name;
  private String type;
  private String className;
  
  public void setName(String name)
  {
    this.name = name.toLowerCase(Locale.ENGLISH);
  }
  
  public void setType(String type)
  {
    this.type = type;
  }
  
  public void setClassName(String className)
  {
    this.className = className;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.script.ScriptDef.NestedElement
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */