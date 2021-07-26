package org.apache.tools.ant.taskdefs.optional.vss;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class MSVSS$WritableFiles
  extends EnumeratedAttribute
{
  public String[] getValues()
  {
    return new String[] { "replace", "skip", "fail" };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.vss.MSVSS.WritableFiles
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */