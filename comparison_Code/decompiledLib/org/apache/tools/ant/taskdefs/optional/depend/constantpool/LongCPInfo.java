package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

public class LongCPInfo
  extends ConstantCPInfo
{
  public LongCPInfo()
  {
    super(5, 2);
  }
  
  public void read(DataInputStream cpStream)
    throws IOException
  {
    setValue(Long.valueOf(cpStream.readLong()));
  }
  
  public String toString()
  {
    return "Long Constant Pool Entry: " + getValue();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.constantpool.LongCPInfo
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */