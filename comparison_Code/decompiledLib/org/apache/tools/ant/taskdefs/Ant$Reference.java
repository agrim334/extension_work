package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.Reference;

public class Ant$Reference
  extends Reference
{
  private String targetid = null;
  
  public void setToRefid(String targetid)
  {
    this.targetid = targetid;
  }
  
  public String getToRefid()
  {
    return targetid;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Ant.Reference
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */