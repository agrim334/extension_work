package org.apache.tools.ant.taskdefs.optional.ccm;

public class CCMCheckinDefault
  extends CCMCheck
{
  public static final String DEFAULT_TASK = "default";
  
  public CCMCheckinDefault()
  {
    setCcmAction("ci");
    setTask("default");
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ccm.CCMCheckinDefault
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */