package org.apache.tools.ant.taskdefs.optional.extension;

/**
 * @deprecated
 */
public final class DeweyDecimal
  extends org.apache.tools.ant.util.DeweyDecimal
{
  public DeweyDecimal(int[] components)
  {
    super(components);
  }
  
  public DeweyDecimal(String string)
    throws NumberFormatException
  {
    super(string);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.extension.DeweyDecimal
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */