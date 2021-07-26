package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

public class IntegerCPInfo
  extends ConstantCPInfo
{
  public IntegerCPInfo()
  {
    super(3, 1);
  }
  
  public void read(DataInputStream cpStream)
    throws IOException
  {
    setValue(Integer.valueOf(cpStream.readInt()));
  }
  
  public String toString()
  {
    return "Integer Constant Pool Entry: " + getValue();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.constantpool.IntegerCPInfo
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */