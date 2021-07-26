package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

public class MethodHandleCPInfo
  extends ConstantPoolEntry
{
  private ConstantPoolEntry reference;
  private ReferenceKind referenceKind;
  private int referenceIndex;
  
  public static enum ReferenceKind
  {
    REF_getField,  REF_getStatic,  REF_putField,  REF_putStatic,  REF_invokeVirtual,  REF_invokeStatic,  REF_invokeSpecial,  REF_newInvokeSpecial,  REF_invokeInterface;
    
    private ReferenceKind() {}
    
    public int value()
    {
      return ordinal() + 1;
    }
  }
  
  public MethodHandleCPInfo()
  {
    super(15, 1);
  }
  
  public void read(DataInputStream cpStream)
    throws IOException
  {
    referenceKind = ReferenceKind.values()[(cpStream.readUnsignedByte() - 1)];
    referenceIndex = cpStream.readUnsignedShort();
  }
  
  public String toString()
  {
    if (isResolved()) {
      return "MethodHandle : " + reference.toString();
    }
    return "MethodHandle : Reference kind = " + referenceKind + "Reference index = " + referenceIndex;
  }
  
  public void resolve(ConstantPool constantPool)
  {
    reference = constantPool.getEntry(referenceIndex);
    reference.resolve(constantPool);
    super.resolve(constantPool);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.constantpool.MethodHandleCPInfo
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */