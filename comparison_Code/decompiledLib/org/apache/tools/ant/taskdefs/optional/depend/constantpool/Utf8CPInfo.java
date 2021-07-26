package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

public class Utf8CPInfo
  extends ConstantPoolEntry
{
  private String value;
  
  public Utf8CPInfo()
  {
    super(1, 1);
  }
  
  public void read(DataInputStream cpStream)
    throws IOException
  {
    value = cpStream.readUTF();
  }
  
  public String toString()
  {
    return "UTF8 Value = " + value;
  }
  
  public String getValue()
  {
    return value;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.constantpool.Utf8CPInfo
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */