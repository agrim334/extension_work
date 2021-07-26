package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

public class InvokeDynamicCPInfo
  extends ConstantCPInfo
{
  private int bootstrapMethodAttrIndex;
  private int nameAndTypeIndex;
  private NameAndTypeCPInfo nameAndTypeCPInfo;
  
  public InvokeDynamicCPInfo()
  {
    super(18, 1);
  }
  
  public void read(DataInputStream cpStream)
    throws IOException
  {
    bootstrapMethodAttrIndex = cpStream.readUnsignedShort();
    nameAndTypeIndex = cpStream.readUnsignedShort();
  }
  
  public String toString()
  {
    if (isResolved()) {
      return 
        "Name = " + nameAndTypeCPInfo.getName() + ", type = " + nameAndTypeCPInfo.getType();
    }
    return "BootstrapMethodAttrIndex inx = " + bootstrapMethodAttrIndex + "NameAndType index = " + nameAndTypeIndex;
  }
  
  public void resolve(ConstantPool constantPool)
  {
    nameAndTypeCPInfo = ((NameAndTypeCPInfo)constantPool.getEntry(nameAndTypeIndex));
    nameAndTypeCPInfo.resolve(constantPool);
    super.resolve(constantPool);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.constantpool.InvokeDynamicCPInfo
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */