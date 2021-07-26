package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class FixCRLF$CrLf
  extends EnumeratedAttribute
{
  public String[] getValues()
  {
    return new String[] { "asis", "cr", "lf", "crlf", "mac", "unix", "dos" };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.FixCRLF.CrLf
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */