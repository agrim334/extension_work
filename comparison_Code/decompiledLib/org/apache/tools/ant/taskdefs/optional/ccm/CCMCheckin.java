package org.apache.tools.ant.taskdefs.optional.ccm;

import java.util.Date;

public class CCMCheckin
  extends CCMCheck
{
  public CCMCheckin()
  {
    setCcmAction("ci");
    setComment("Checkin " + new Date());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ccm.CCMCheckin
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */