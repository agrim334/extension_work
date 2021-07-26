package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Restrict;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.types.resources.selectors.Type;

class Checksum$FileUnion
  extends Restrict
{
  private Union u;
  
  Checksum$FileUnion()
  {
    u = new Union();
    super.add(u);
    super.add(Type.FILE);
  }
  
  public void add(ResourceCollection rc)
  {
    u.add(rc);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Checksum.FileUnion
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */