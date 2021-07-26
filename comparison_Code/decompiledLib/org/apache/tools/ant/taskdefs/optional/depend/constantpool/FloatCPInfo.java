package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

public class FloatCPInfo
  extends ConstantCPInfo
{
  public FloatCPInfo()
  {
    super(4, 1);
  }
  
  public void read(DataInputStream cpStream)
    throws IOException
  {
    setValue(Float.valueOf(cpStream.readFloat()));
  }
  
  public String toString()
  {
    return "Float Constant Pool Entry: " + getValue();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.constantpool.FloatCPInfo
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */