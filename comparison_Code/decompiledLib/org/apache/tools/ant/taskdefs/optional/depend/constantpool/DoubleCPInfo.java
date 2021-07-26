package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

public class DoubleCPInfo
  extends ConstantCPInfo
{
  public DoubleCPInfo()
  {
    super(6, 2);
  }
  
  public void read(DataInputStream cpStream)
    throws IOException
  {
    setValue(Double.valueOf(cpStream.readDouble()));
  }
  
  public String toString()
  {
    return "Double Constant Pool Entry: " + getValue();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.constantpool.DoubleCPInfo
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */