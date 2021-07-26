package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;

public class FieldRefCPInfo
  extends ConstantPoolEntry
{
  private String fieldClassName;
  private String fieldName;
  private String fieldType;
  private int classIndex;
  private int nameAndTypeIndex;
  
  public FieldRefCPInfo()
  {
    super(9, 1);
  }
  
  public void read(DataInputStream cpStream)
    throws IOException
  {
    classIndex = cpStream.readUnsignedShort();
    nameAndTypeIndex = cpStream.readUnsignedShort();
  }
  
  public void resolve(ConstantPool constantPool)
  {
    ClassCPInfo fieldClass = (ClassCPInfo)constantPool.getEntry(classIndex);
    
    fieldClass.resolve(constantPool);
    
    fieldClassName = fieldClass.getClassName();
    
    NameAndTypeCPInfo nt = (NameAndTypeCPInfo)constantPool.getEntry(nameAndTypeIndex);
    
    nt.resolve(constantPool);
    
    fieldName = nt.getName();
    fieldType = nt.getType();
    
    super.resolve(constantPool);
  }
  
  public String toString()
  {
    if (isResolved()) {
      return "Field : Class = " + fieldClassName + ", name = " + fieldName + ", type = " + fieldType;
    }
    return "Field : Class index = " + classIndex + ", name and type index = " + nameAndTypeIndex;
  }
  
  public String getFieldClassName()
  {
    return fieldClassName;
  }
  
  public String getFieldName()
  {
    return fieldName;
  }
  
  public String getFieldType()
  {
    return fieldType;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.depend.constantpool.FieldRefCPInfo
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */