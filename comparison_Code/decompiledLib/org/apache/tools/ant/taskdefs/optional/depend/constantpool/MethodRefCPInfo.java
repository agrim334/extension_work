package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

public class MethodRefCPInfo
  extends ConstantPoolEntry
{
  private String methodClassName;
  private String methodName;
  private String methodType;
  private int classIndex;
  private int nameAndTypeIndex;
  
  public MethodRefCPInfo()
  {
    super(10, 1);
  }
  
  public void read(DataInputStream cpStream)
    throws IOException
  {
    classIndex = cpStream.readUnsignedShort();
    nameAndTypeIndex = cpStream.readUnsignedShort();
  }
  
  public String toString()
  {
    if (isResolved()) {
      return "Method : Class = " + methodClassName + ", name = " + methodName + ", type = " + methodType;
    }
    return "Method : Class index = " + classIndex + ", name and type index = " + nameAndTypeIndex;
  }
  
  public void resolve(ConstantPool constantPool)
  {
    ClassCPInfo methodClass = (ClassCPInfo)constantPool.getEntry(classIndex);
    
    methodClass.resolve(constantPool);
    
    methodClassName = methodClass.getClassName();
    
    NameAndTypeCPInfo nt = (NameAndTypeCPInfo)constantPool.getEntry(nameAndTypeIndex);
    
    nt.resolve(constantPool);
    
    methodName = nt.getName();
    methodType = nt.getType();
    
    super.resolve(constantPool);
  }
  
  public String getMethodClassName()
  {
    return methodClassName;
  }
  
  public String getMethodName()
  {
    return methodName;
  }
  
  public String getMethodType()
  {
    return methodType;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.constantpool.MethodRefCPInfo
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */