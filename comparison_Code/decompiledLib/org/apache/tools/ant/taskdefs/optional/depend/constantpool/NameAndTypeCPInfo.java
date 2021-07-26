package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

public class NameAndTypeCPInfo
  extends ConstantPoolEntry
{
  private String name;
  private String type;
  private int nameIndex;
  private int descriptorIndex;
  
  public NameAndTypeCPInfo()
  {
    super(12, 1);
  }
  
  public void read(DataInputStream cpStream)
    throws IOException
  {
    nameIndex = cpStream.readUnsignedShort();
    descriptorIndex = cpStream.readUnsignedShort();
  }
  
  public String toString()
  {
    if (isResolved()) {
      return "Name = " + name + ", type = " + type;
    }
    return "Name index = " + nameIndex + ", descriptor index = " + descriptorIndex;
  }
  
  public void resolve(ConstantPool constantPool)
  {
    name = ((Utf8CPInfo)constantPool.getEntry(nameIndex)).getValue();
    type = ((Utf8CPInfo)constantPool.getEntry(descriptorIndex)).getValue();
    
    super.resolve(constantPool);
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getType()
  {
    return type;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.constantpool.NameAndTypeCPInfo
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */