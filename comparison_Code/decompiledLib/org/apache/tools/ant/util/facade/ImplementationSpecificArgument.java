package org.apache.tools.ant.util.facade;

import org.apache.tools.ant.types.Commandline.Argument;

public class ImplementationSpecificArgument
  extends Commandline.Argument
{
  private String impl;
  
  public void setImplementation(String impl)
  {
    this.impl = impl;
  }
  
  public final String[] getParts(String chosenImpl)
  {
    if ((impl == null) || (impl.equals(chosenImpl))) {
      return super.getParts();
    }
    return new String[0];
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.facade.ImplementationSpecificArgument
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */