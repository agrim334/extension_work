package org.apache.tools.ant.taskdefs.modules;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class Link$VMType
  extends EnumeratedAttribute
{
  public String[] getValues()
  {
    return new String[] { "client", "server", "minimal", "all" };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.modules.Link.VMType
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */