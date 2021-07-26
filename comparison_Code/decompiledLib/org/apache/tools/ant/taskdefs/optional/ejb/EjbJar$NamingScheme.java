package org.apache.tools.ant.taskdefs.optional.ejb;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class EjbJar$NamingScheme
  extends EnumeratedAttribute
{
  public static final String EJB_NAME = "ejb-name";
  public static final String DIRECTORY = "directory";
  public static final String DESCRIPTOR = "descriptor";
  public static final String BASEJARNAME = "basejarname";
  
  public String[] getValues()
  {
    return new String[] { "ejb-name", "directory", "descriptor", "basejarname" };
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.ejb.EjbJar.NamingScheme
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */