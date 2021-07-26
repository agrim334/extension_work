package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

public class MethodTypeCPInfo
  extends ConstantCPInfo
{
  private int methodDescriptorIndex;
  private String methodDescriptor;
  
  public MethodTypeCPInfo()
  {
    super(16, 1);
  }
  
  public void read(DataInputStream cpStream)
    throws IOException
  {
    methodDescriptorIndex = cpStream.readUnsignedShort();
  }
  
  public void resolve(ConstantPool constantPool)
  {
    Utf8CPInfo methodClass = (Utf8CPInfo)constantPool.getEntry(methodDescriptorIndex);
    methodClass.resolve(constantPool);
    methodDescriptor = methodClass.getValue();
    super.resolve(constantPool);
  }
  
  public String toString()
  {
    if (isResolved()) {
      return "MethodDescriptor: " + methodDescriptor;
    }
    return "MethodDescriptorIndex: " + methodDescriptorIndex;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.constantpool.MethodTypeCPInfo
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */