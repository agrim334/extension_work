package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Restrict;

final class DependSet$NonExistent
  extends Restrict
{
  private DependSet$NonExistent(ResourceCollection rc)
  {
    super.add(rc);
    super.add(DependSet.access$000());
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.DependSet.NonExistent
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */