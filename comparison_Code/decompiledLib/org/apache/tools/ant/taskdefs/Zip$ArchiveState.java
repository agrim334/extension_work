package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.Resource;

public class Zip$ArchiveState
{
  private final boolean outOfDate;
  private final Resource[][] resourcesToAdd;
  
  Zip$ArchiveState(boolean state, Resource[][] r)
  {
    outOfDate = state;
    resourcesToAdd = r;
  }
  
  public boolean isOutOfDate()
  {
    return outOfDate;
  }
  
  public Resource[][] getResourcesToAdd()
  {
    return resourcesToAdd;
  }
  
  public boolean isWithoutAnyResources()
  {
    if (resourcesToAdd == null) {
      return true;
    }
    for (Resource[] element : resourcesToAdd) {
      if ((element != null) && (element.length > 0)) {
        return false;
      }
    }
    return true;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Zip.ArchiveState
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */