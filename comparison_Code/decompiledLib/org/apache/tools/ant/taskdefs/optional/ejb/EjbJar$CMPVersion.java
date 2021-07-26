package org.apache.tools.ant.taskdefs.optional.ejb;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class EjbJar$CMPVersion
  extends EnumeratedAttribute
{
  public static final String CMP1_0 = "1.0";
  public static final String CMP2_0 = "2.0";
  
  public String[] getValues()
  {
    return new String[] { "1.0", "2.0" };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ejb.EjbJar.CMPVersion
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */